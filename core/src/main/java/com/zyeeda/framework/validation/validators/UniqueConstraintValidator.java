package com.zyeeda.framework.validation.validators;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.validation.ValidationEvent;
import com.zyeeda.framework.validation.constraints.Unique;
import com.zyeeda.framework.validation.internal.AbstractConstraintValidator;

public class UniqueConstraintValidator extends AbstractConstraintValidator<Unique, Object> {

	private final static Logger logger = LoggerFactory.getLogger(UniqueConstraintValidator.class);
	
	private Unique constraint;
	
	public UniqueConstraintValidator(PersistenceService persistenceSvc,	ValidationEvent event) {
		super(persistenceSvc, event);
	}

	@Override
	public void initialize(Unique constraintAnnotation) {
		this.constraint = constraintAnnotation;
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
		if (this.getValidationEvent() != this.constraint.event()) {
			return true;
		}
		
		EntityManager session = this.getPersistenceService().getCurrentSession();
		Query query = session.createNamedQuery(this.constraint.namedQuery());
		Set<Parameter<?>> params = query.getParameters();
		try {
			for (Parameter<?> param : params) {
				String name = param.getName();
				query.setParameter(name, BeanUtils.getNestedProperty(obj, name));
			}
			int count = query.getMaxResults();
			return count == 0;
		} catch (NoSuchMethodException e) {
			LoggerHelper.error(logger, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LoggerHelper.error(logger, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LoggerHelper.error(logger, e.getMessage(), e);
		}
		return false;
	}

}
