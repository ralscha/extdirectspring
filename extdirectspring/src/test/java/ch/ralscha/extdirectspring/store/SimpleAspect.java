package ch.ralscha.extdirectspring.store;

import javax.inject.Named;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
@Named
public class SimpleAspect {	
		
	@Before("execution(* ch.ralscha.extdirectspring.store.*.*(..))")
	public void logSomething(JoinPoint jp) {
		System.out.println(jp);		
	}

}
