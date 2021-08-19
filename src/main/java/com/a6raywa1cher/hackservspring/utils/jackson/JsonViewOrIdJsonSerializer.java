package com.a6raywa1cher.hackservspring.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Collection;

public class JsonViewOrIdJsonSerializer extends StdSerializer<Object> implements ContextualSerializer {
	private Class<?> parameterView;

	private String fieldName;

	public JsonViewOrIdJsonSerializer() {
		super(Object.class);
	}

	public JsonViewOrIdJsonSerializer(Class<?> parameterView, String fieldName) {
		super(Object.class);
		this.parameterView = parameterView;
		this.fieldName = fieldName;
	}

	@SneakyThrows
	private void serializeObject(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		Class<?> activeView = serializers.getActiveView();
		if (activeView != null && parameterView.isAssignableFrom(activeView)) {
			serializers.defaultSerializeValue(value, gen);
		} else {
			Long id = (Long) value.getClass().getMethod("getId").invoke(value);
			if (id == null) {
				gen.writeNull();
			} else {
				gen.writeNumber(id);
			}
		}
	}

	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (Collection.class.isAssignableFrom(value.getClass())) {
			Collection<?> collection = (Collection<?>) value;
			gen.writeStartArray();
			for (Object a : collection) {
				serializeObject(a, gen, serializers);
			}
			gen.writeEndArray();
		} else {
			gen.writeFieldName(fieldName);
			serializeObject(value, gen, serializers);
		}
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
		Class<?> clazz = null;
		JsonViewOrId ann = null;
		if (property != null) {
			ann = property.getAnnotation(JsonViewOrId.class);
		}
		if (ann != null) {
			clazz = ann.value();
		}
		return new JsonViewOrIdJsonSerializer(clazz, property.getName());
	}
}
