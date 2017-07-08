package com.github.invader.agent.interceptors.validation;

import org.hibernate.validator.HibernateValidator;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;

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
 */
public class JavaxValidatorFactory {

    public static Validator createValidator() {
        return
                Validation
                        .byDefaultProvider()
                        .providerResolver(() -> Arrays.asList(new HibernateValidator()))
                        .configure()
                        .buildValidatorFactory()
                        .getValidator();
    }

}
