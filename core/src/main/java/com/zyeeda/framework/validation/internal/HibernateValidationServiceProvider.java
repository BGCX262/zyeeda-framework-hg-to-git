package com.zyeeda.framework.validation.internal;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.validation.ValidationEvent;
import com.zyeeda.framework.validation.ValidationService;

@ServiceId("hibernate-validation-service")
@Marker(Primary.class)
public class HibernateValidationServiceProvider extends AbstractService	implements ValidationService {

	// Injected
	private final PersistenceService persistenceSvc;
	
	private ValidatorFactory preInsertValidatorFactory;
	private ValidatorFactory preUpdateValidatorFactory;
	private ValidatorFactory preDeleteValidatorFactory;
	
	public HibernateValidationServiceProvider(
			@Primary PersistenceService persistenceSvc, RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
		this.persistenceSvc = persistenceSvc;
	}
	
    @Override
    public void start() throws Exception {
        this.preInsertValidatorFactory = this.getValidatorFactory(ValidationEvent.PRE_INSERT);
        this.preUpdateValidatorFactory = this.getValidatorFactory(ValidationEvent.PRE_UPDATE);
        this.preDeleteValidatorFactory = this.getValidatorFactory(ValidationEvent.PRE_DELETE);
    }
    
    private ValidatorFactory getValidatorFactory(ValidationEvent event) {
    	HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
    	return config.constraintValidatorFactory(
    			new CustomConstraintValidatorFactory(this.persistenceSvc, event)).buildValidatorFactory();
    }
	
    @Override
    public ValidatorFactory getPreInsertValidatorFactory() {
    	return this.preInsertValidatorFactory;
    }
    
    @Override
    public ValidatorFactory getPreUpdateValidatorFactory() {
    	return this.preUpdateValidatorFactory;
    }
    
    @Override
    public ValidatorFactory getPreDeleteValidatorFactory() {
    	return this.preDeleteValidatorFactory;
    }
    
}
