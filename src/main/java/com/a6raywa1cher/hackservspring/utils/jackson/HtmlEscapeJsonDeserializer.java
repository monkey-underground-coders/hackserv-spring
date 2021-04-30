package com.a6raywa1cher.hackservspring.utils.jackson;

import com.a6raywa1cher.hackservspring.utils.LocalHtmlUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class HtmlEscapeJsonDeserializer extends StringDeserializer {
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String extracted = super.deserialize(p, ctxt);
		return LocalHtmlUtils.htmlEscape(extracted);
	}
}
