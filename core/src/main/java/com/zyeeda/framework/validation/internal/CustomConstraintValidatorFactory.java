package com.zyeeda.framework.validation.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ValidationException;

import org.hibernate.validator.engine.ConstraintValidatorFactoryImpl;

import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.validation.ValidationEvent;

public class CustomConstraintValidatorFactory extends ConstraintValidatorFactoryImpl implements ConstraintValidatorFactory {
	
	private final PersistenceService persistenceSvc;
	private final ValidationEvent event;
	
	public CustomConstraintValidatorFactory(PersistenceService persistenceSvc, ValidationEvent event) {
		this.persistenceSvc = persistenceSvc;
		this.event = event;
	}

	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		if (key.getSuperclass().equals(AbstractConstraintValidator.class)) {
			Constructor<T> ctor;
			try {
				ctor = key.getConstructor(PersistenceService.class, ValidationEvent.class);
				T constraintValidator = ctor.newInstance(this.persistenceSvc, this.event);
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
