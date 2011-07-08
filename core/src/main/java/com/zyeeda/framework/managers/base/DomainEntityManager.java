package com.zyeeda.framework.managers.base;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Query;

import org.hibernate.ejb.HibernateEntityManagerFactory;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.MetadataUtil;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;
import com.googlecode.genericdao.search.jpa.hibernate.HibernateMetadataUtil;
import com.zyeeda.framework.persistence.PersistenceService;

@SuppressWarnings("unchecked")
public class DomainEntityManager<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	private PersistenceService persistenceSvc;
	protected Class<T> entityClass = (Class<T>) ((ParameterizedType) this
			.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
	public DomainEntityManager(PersistenceService persistenceSvc) {
		this.persistenceSvc = persistenceSvc;
		
		this.setEntityManager(this.persistenceSvc.getCurrentSession());
		
		HibernateEntityManagerFactory emf = (HibernateEntityManagerFactory) this.persistenceSvc.getSessionFactory();
		MetadataUtil util = HibernateMetadataUtil.getInstanceForEntityManagerFactory(emf);
		JPASearchProcessor processor = new JPASearchProcessor(util);
		this.setSearchProcessor(processor);
		
	}
	
	protected PersistenceService getPersistenceService() {
		return this.persistenceSvc;
	}

	@Deprecated
	protected static void setQueryParams(Query query, Object[] queryParams) {
		if (queryParams != null && queryParams.length > 0) {
			for (int i = 0; i < queryParams.length; i++) {
				query.setParameter(i + 1, queryParams[i]);
			}
		}
	}
	
	/**
	 * 获取实体的名称
	 * @param <E>
	 * @param clazz 实体类
	 * @return
	 */
	@Deprecated
	protected static <E> String getEntityName(Class<E> clazz){
		String entityname = clazz.getSimpleName();
		Entity entity = clazz.getAnnotation(Entity.class);
		if(entity.name() != null && !"".equals(entity.name())){
			entityname = entity.name();
		}
		return entityname;
	}

	/**
	 * 组装order by语句
	 * 
	 * @param orderby
	 * @return
	 */
	@Deprecated
	protected static String buildOrderBy(LinkedHashMap<String, Object> orderby) {
		StringBuffer orderbyql = new StringBuffer("");
		if (orderby != null && orderby.size() > 0) {
			orderbyql.append(" order by ");
			for (String key : orderby.keySet()) {
				orderbyql.append("o.").append(key).append(" ").append(
						orderby.get(key)).append(",");
			}
			orderbyql.deleteCharAt(orderbyql.length() - 1);
		}

		return orderbyql.toString();
	}

	@Deprecated
	protected static <E> String getCountField(Class<E> clazz) {
		String out = "o";
		try {
			PropertyDescriptor[] propertyDescriptors = Introspector
					.getBeanInfo(clazz).getPropertyDescriptors();
			for (PropertyDescriptor propertydesc : propertyDescriptors) {
				Method method = propertydesc.getReadMethod();
				if (method != null
						&& method.isAnnotationPresent(EmbeddedId.class)) {
					PropertyDescriptor[] ps = Introspector.getBeanInfo(
							propertydesc.getPropertyType())
							.getPropertyDescriptors();
					out = "o."
							+ propertydesc.getName()
							+ "."
							+ (!ps[1].getName().equals("class") ? ps[1]
									.getName() : ps[0].getName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	
}
