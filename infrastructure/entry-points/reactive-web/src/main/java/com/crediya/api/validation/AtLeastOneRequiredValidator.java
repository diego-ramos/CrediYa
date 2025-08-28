package com.crediya.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;

public class AtLeastOneRequiredValidator implements ConstraintValidator<AtLeastOneRequired, Object> {

    private String firstField;
    private String secondField;

    @Override
    public void initialize(AtLeastOneRequired constraintAnnotation) {
        this.firstField = constraintAnnotation.first();
        this.secondField = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Object firstVal = beanWrapper.getPropertyValue(firstField);
        Object secondVal = beanWrapper.getPropertyValue(secondField);

        return firstVal != null || secondVal != null;
    }
}
