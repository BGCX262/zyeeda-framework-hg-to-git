package com.zyeeda.framework.validation.internal;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.slf4j.Logger;

import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.validation.ValidationService;

public class HibernateValidationServiceProvider extends AbstractService	implements ValidationService {

	// Injected
	private final PersistenceService persistenceSvc;
	private final Logger logger;
	
	private ValidatorFactory validatorFactory;
	
	public HibernateValidationServiceProvider(PersistenceService persistenceSvc, Logger logger) {
		this.persistenceSvc = persistenceSvc;
		this.logger = logger;
	}
	
    @Override
    public void start() throws Exception {
        HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
        this.validatorFactory = config.constraintValidatorFactory(
        		new CustomConstraintValidatorFactory(
        				this.persistenceSvc, this.logger)).buildValidatorFactory();
    }
	
    @Override
    public ValidatorFactory getValidatorFactory() {
        return this.validatorFactory;
    }
    
}
