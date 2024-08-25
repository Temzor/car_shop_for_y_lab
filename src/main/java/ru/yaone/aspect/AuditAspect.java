package ru.yaone.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.yaone.constants.SqlScriptsForAudit;
import ru.yaone.manager.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final DatabaseConnectionManager databaseConnectionManager;

    @Pointcut("@annotation(ru.yaone.aspect.annotation.Loggable)")
    public void loggablePointcut() {}

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
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SqlScriptsForAudit.INSERT_EVENTS)) {
            preparedStatement.setString(1, methodName);
            preparedStatement.setString(2, Arrays.toString(methodArgs));
            preparedStatement.setLong(3, executionTime);
            preparedStatement.setString(4, result != null ? result.toString() : null);
            preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            DatabaseConnectionManager.closeConnection();
        }
    }
}