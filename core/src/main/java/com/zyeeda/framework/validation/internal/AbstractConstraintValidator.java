package com.zyeeda.framework.validation.internal;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;

import org.slf4j.Logger;

import com.zyeeda.framework.persistence.PersistenceService;

public abstract class AbstractConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

	// Injected
	private final PersistenceService persistenceSvc;
	private final Logger logger;

    public AbstractConstraintValidator(PersistenceService persistenceSvc, Logger logger) {
    	this.persistenceSvc = persistenceSvc;
    	this.logger = logger;
    }
    
    protected PersistenceService getPersistenceService() {
    	return this.persistenceSvc;
    }
    
    protected Logger getLogger() {
    	return this.logger;
    }
    
}
