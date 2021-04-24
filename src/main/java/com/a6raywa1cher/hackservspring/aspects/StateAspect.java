package com.a6raywa1cher.hackservspring.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class StateAspect {

	@Pointcut("@annotation(annotations.OnlyOnRegistration)")
	public void callOnRegistration() {
	}

	@Before("callOnRegistration()")
	public void beforeCallOnRegistration() {
		System.out.println("work!");
	}

}
