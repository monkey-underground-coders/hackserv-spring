package com.a6raywa1cher.hackservspring.utils.jackson;

import com.a6raywa1cher.hackservspring.utils.LocalHtmlUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class HtmlEscapeJsonDeserializer extends StringDeserializer implements ContextualDeserializer {
	private int maxLength;

	@Override
	public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,
	                                            final BeanProperty property) throws JsonMappingException {

		maxLength = property.getAnnotation(HtmlEscape.class).value();

		return this;
	}

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String extracted = super.deserialize(p, ctxt);
		return maxLength > 0 ?
				LocalHtmlUtils.htmlEscape(extracted, maxLength) :
				LocalHtmlUtils.htmlEscape(extracted);
	}
}
