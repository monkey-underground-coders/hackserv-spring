package com.a6raywa1cher.hackservspring.service;

import java.util.Optional;

public interface KeyValueService {
	Optional<String> getByKey(String key);

	String getOrDefault(String key, String defaultValue);

	String setKeyValue(String key, String value);

	boolean isPresent(String key);

}

