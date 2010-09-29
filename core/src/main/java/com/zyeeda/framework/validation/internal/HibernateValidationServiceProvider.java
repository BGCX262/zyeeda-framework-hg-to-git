package com.zyeeda.framework.validation.internal;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.slf4j.Logger;

import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.validation.ValidationService;

@ServiceId("HibernateValidationServiceProvider")
@Marker(Primary.class)
public class HibernateValidationServiceProvider extends AbstractService	implements ValidationService {

	// Injected
	private final PersistenceService persistenceSvc;
	
	private ValidatorFactory validatorFactory;
	
	public HibernateValidationServiceProvider(
			@Primary PersistenceService persistenceSvc, Logger logger, RegistryShutdownHub shutdownHub) {
		super(logger, shutdownHub);
		this.persistenceSvc = persistenceSvc;
	}
	
    @Override
    public void start() throws Exception {
        HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
        this.validatorFactory = config.constraintValidatorFactory(
        		new CustomConstraintValidatorFactory(
        				this.persistenceSvc, this.getLogger())).buildValidatorFactory();
    }
	
    @Override
    public ValidatorFactory getValidatorFactory() {
        return this.validatorFactory;
    }
    
}
