package com.a6raywa1cher.hackservspring.service.impl;

import com.a6raywa1cher.hackservspring.model.HackState;
import com.a6raywa1cher.hackservspring.model.KeyValueItems;
import com.a6raywa1cher.hackservspring.service.HackStateService;
import com.a6raywa1cher.hackservspring.service.KeyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HackStateServiceImpl implements HackStateService {

	private final KeyValueService keyValueService;

	@Autowired
	public HackStateServiceImpl(KeyValueService keyValueService) {
		this.keyValueService = keyValueService;
	}

	@Override
	public HackState get() {
		return HackState.valueOf(keyValueService.getOrDefault(KeyValueItems.HACK_STATE, HackState.PLANNING.toString()));
	}

	@Override
	public HackState set(HackState state) {
		return HackState.valueOf(keyValueService.setKeyValue(KeyValueItems.HACK_STATE, state.toString()));
	}
}
