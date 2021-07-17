package com.a6raywa1cher.hackservspring.utils.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that changes field serialization depending on a current view.
 * <p>
 * If the current view matches the parameter (or is a subclass), then
 * serialization will proceed as usual. Otherwise, only the identifier is serializing.
 * <p>
 * Supports both single objects and collections
 *
 * @see com.fasterxml.jackson.annotation.JsonView
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = JsonViewOrIdJsonSerializer.class)
public @interface JsonViewOrId {
	Class<?> value();
}
