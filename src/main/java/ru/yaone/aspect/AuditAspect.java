package ru.yaone.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.yaone.repositoryimpl.AspectRepositoryImpl;


@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AspectRepositoryImpl aspectRepository;

    @Pointcut("@annotation(ru.yaone.aspect.annotation.Loggable)")
    public void loggablePointcut() {
    }

    @Around("loggablePointcut()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        saveAuditLog(methodName, methodArgs, endTime - startTime, result);
        return result;
    }

    private void saveAuditLog(String methodName, Object[] methodArgs, long executionTime, Object result) {
        aspectRepository.saveAuditLog(methodName, methodArgs, executionTime, result);
    }
}