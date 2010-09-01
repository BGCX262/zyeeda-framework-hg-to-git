package com.zyeeda.framework.validation.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ValidationException;

import org.hibernate.validator.engine.ConstraintValidatorFactoryImpl;
import org.slf4j.Logger;

import com.zyeeda.framework.persistence.PersistenceService;

public class CustomConstraintValidatorFactory extends ConstraintValidatorFactoryImpl implements ConstraintValidatorFactory {
	
	private final PersistenceService persistenceSvc;
	private final Logger logger;
	
	public CustomConstraintValidatorFactory(PersistenceService persistenceSvc,
			Logger logger) {
		
		this.persistenceSvc = persistenceSvc;
		this.logger = logger;
	}

	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		if (key.getSuperclass().equals(AbstractConstraintValidator.class)) {
			Constructor<T> ctor;
			try {
				ctor = key.getConstructor(PersistenceService.class, Logger.class);
				T constraintValidator = ctor.newInstance(this.persistenceSvc, this.logger);
				return constraintValidator;
			} catch (SecurityException e) {
				throw new ValidationException(e);
			} catch (NoSuchMethodException e) {
				throw new ValidationException(e);
			} catch (IllegalArgumentException e) {
				throw new ValidationException(e);
			} catch (InstantiationException e) {
				throw new ValidationException(e);
			} catch (IllegalAccessException e) {
				throw new ValidationException(e);
			} catch (InvocationTargetException e) {
				throw new ValidationException(e);
			}
		}
		
		return super.getInstance(key);
	}

}
