package com.a6raywa1cher.hackservspring.utils.jackson;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HtmlEscapeValidator implements ConstraintValidator<HtmlEscape, String> {
	private int maxLength;

	@Override
	public void initialize(HtmlEscape constraintAnnotation) {
		maxLength = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null || HtmlEscape.UNLIMITED == maxLength || value.length() <= maxLength;
	}
}
