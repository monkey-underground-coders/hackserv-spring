package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.KeyValue;
import com.a6raywa1cher.hackservspring.model.repo.KeyValueRepository;
import com.a6raywa1cher.hackservspring.service.KeyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
public class KeyValueServiceImpl implements KeyValueService {

	private final KeyValueRepository repository;

	@Autowired
	public KeyValueServiceImpl(KeyValueRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<String> getByKey(String key) {
		Optional<KeyValue> optionalKeyValue = repository.findById(key);

		return optionalKeyValue.map(KeyValue::getValue);
	}

	@Override
	public String getOrDefault(String key, String defaultValue) {
		Optional<String> optionalString = this.getByKey(key);
		if (optionalString.isEmpty()) {
			return this.setKeyValue(key, defaultValue);
		} else {
			return optionalString.get();
		}
	}

	@Override
	public String setKeyValue(String key, String value) {
		Assert.notNull(value, "value can't be null");

		Optional<KeyValue> optionalKeyValue = repository.findById(key);
		if (optionalKeyValue.isEmpty()) {
			KeyValue newKeyValue = new KeyValue();
			newKeyValue.setKey(key);
			newKeyValue.setValue(value);
			repository.save(newKeyValue);
		} else {
			optionalKeyValue.get().setValue(value);
			repository.save(optionalKeyValue.get());
		}
		return value;
	}

	@Override
	public boolean isPresent(String key) {
		return repository.findById(key).isPresent();
	}


}
