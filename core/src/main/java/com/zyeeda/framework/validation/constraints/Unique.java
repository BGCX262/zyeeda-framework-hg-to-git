package com.zyeeda.framework.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import javax.validation.Constraint;

import com.zyeeda.framework.validation.ValidationEvent;
import com.zyeeda.framework.validation.validators.UniqueConstraintValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueConstraintValidator.class)
@Documented
public @interface Unique {

	ValidationEvent event() default ValidationEvent.PRE_INSERT;
	
	String namedQuery();
	
}
