package com.github.invader.agent.interceptors.validation;

import org.hibernate.validator.HibernateValidator;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.spi.ValidationProvider;

/**
 * Wraps Validator class creation logic.
 *
 * The Validator initialization is done based on direct class name (FQN) resolution
 * rather than by using the default provider Validation.buildDefaultValidatorFactory()
 * because - in order to work inside an agent - the validator library (hibernate validator)
 * is relocated to another package in order not to interfere with the main application's
 * own validator provider library. The default validation factory logic would not work here.
 *
 * Notice: additionally for the (relocated) validator provider to work without
 * writing a custom resolver, an appropriate SPI javax.validation implementation file
 * must be placed in META-INF.
 *
 * Created by Jacek on 2017-07-04.
 */
public class JavaxValidatorFactory {

    public static Validator createValidator() {
        Validator validator;
        String validatorClassName = null;
        try {
            validatorClassName = HibernateValidator.class.getName();
            validator = Validation
                    .byProvider(
                            (Class<? extends ValidationProvider>)
                                    Class.forName(validatorClassName)
                    )
                    .configure()
                    .buildValidatorFactory()
                    .getValidator();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot initialize ValidatorProvider "+validatorClassName, e);
        }
        return validator;
    }

}
