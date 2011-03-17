package com.zyeeda.framework.validation;

import javax.validation.ValidatorFactory;

import com.zyeeda.framework.service.Service;

public interface ValidationService extends Service {
	
	public ValidatorFactory getPreInsertValidatorFactory();
	
	public ValidatorFactory getPreUpdateValidatorFactory();
	
	public ValidatorFactory getPreDeleteValidatorFactory();

}
