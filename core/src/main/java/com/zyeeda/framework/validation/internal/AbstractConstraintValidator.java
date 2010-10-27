package com.zyeeda.framework.validation.internal;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;

import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.validation.ValidationEvent;

public abstract class AbstractConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

	// Injected
	private final PersistenceService persistenceSvc;
	private final ValidationEvent event;

    public AbstractConstraintValidator(PersistenceService persistenceSvc, ValidationEvent event) {
    	this.persistenceSvc = persistenceSvc;
    	this.event = event;
    }
    
    protected PersistenceService getPersistenceService() {
    	return this.persistenceSvc;
    }
    
    protected ValidationEvent getValidationEvent() {
    	return this.event;
    }
    
}
