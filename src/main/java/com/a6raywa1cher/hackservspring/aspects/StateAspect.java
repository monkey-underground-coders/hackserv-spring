package com.a6raywa1cher.hackservspring.aspects;

import com.a6raywa1cher.hackservspring.aspects.annotations.OnlyOnHackState;
import com.a6raywa1cher.hackservspring.model.HackState;
import com.a6raywa1cher.hackservspring.rest.exc.InvalidHackStateOperationException;
import com.a6raywa1cher.hackservspring.service.HackStateService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Set;


@Aspect
@Component
public class StateAspect {

	private final HackStateService hackStateService;

	public StateAspect(HackStateService hackStateService) {
		this.hackStateService = hackStateService;
	}

	@Pointcut("@annotation(com.a6raywa1cher.hackservspring.aspects.annotations.OnlyOnHackState)")
	public void callOnRegistration() {
	}

	@Around("callOnRegistration()")
	public Object aroundCallOnRegistration(ProceedingJoinPoint pjp) throws Throwable {
		OnlyOnHackState annotation = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(OnlyOnHackState.class);
		Set<HackState> stateSet = Set.of(annotation.value());
		HackState currentState = hackStateService.get();
		if (stateSet.contains(currentState)) {
			return pjp.proceed();
		} else {
			throw new InvalidHackStateOperationException();
		}
	}

}
