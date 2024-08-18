package ru.yaone.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yaone.constans.SqlScriptsForAudit;
import ru.yaone.constans.SqlScriptsForOrder;
import ru.yaone.manager.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * Класс {@code AuditAspect} реализует аспект для аудита методов,
 * помеченных аннотацией {@link ru.yaone.aspect.annotation.Loggable}.
 *
 * <p>Аспект позволяет отслеживать выполнение методов, включая
 * время выполнения, переданные аргументы, результаты и исключения.</p>
 *
 * <p>Информация об аудитах сохраняется в базе данных.</p>
 *
 * <p>Зависимости:</p>
 * <ul>
 *     <li>{@link DatabaseConnectionManager} - для управления соединением с базой данных.</li>
 *     <li>{@link SqlScriptsForOrder} - для работы с SQL запросами.</li>
 *     <li>SLF4J для ведения логов.</li>
 * </ul>
 *
 * @author Ваше имя
 * @version 1.0
 */
@Aspect
public class AuditAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditAspect.class);

    /**
     * Точка среза для методов с аннотацией {@link ru.yaone.aspect.annotation.Loggable}.
     */
    @Pointcut("@within(ru.yaone.aspect.annotation.Loggable) || @annotation(ru.yaone.aspect.annotation.Loggable)")
    public void annotationByAuditable() {
    }

    /**
     * Метод, который выполняет аудит вокруг целевых методов.
     *
     * @param joinPoint информация о выполнении соединения (join point), предоставляющая доступ к методам,
     *                  аргументам и другим данным вызываемого метода.
     * @return результат выполнения целевого метода.
     * @throws Throwable если возникло исключение во время выполнения метода.
     */
    @Around("annotationByAuditable()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Calling method: " + joinPoint.getSignature().getName());

        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();

        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            exception = e;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution of method: " + methodName + " took " + (endTime - startTime) + " ms");

        saveAuditLog(methodName, methodArgs, endTime - startTime, result, exception);

        if (exception != null) {
            throw exception;
        }
        return result;
    }

    /**
     * Сохраняет данные аудита в базе данных.
     *
     * @param methodName    имя вызываемого метода.
     * @param methodArgs    аргументы, переданные методу.
     * @param executionTime время выполнения метода в миллисекундах.
     * @param result        результат выполнения метода.
     * @param exception     исключение, если оно возникло во время выполнения метода.
     */
    private void saveAuditLog(String methodName, Object[] methodArgs, long executionTime, Object result, Exception exception) {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SqlScriptsForAudit.INSERT_EVENTS)) {

            preparedStatement.setString(1, methodName);
            preparedStatement.setString(2, Arrays.toString(methodArgs));
            preparedStatement.setLong(3, executionTime);
            preparedStatement.setString(4, result != null ? result.toString() : null);
            preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(6, exception != null ? exception.getMessage() : null);
            preparedStatement.executeUpdate();
            LOGGER.info("Audit log saved successfully");
        } catch (Exception e) {
            LOGGER.error("Ошибка при сохранении данных аудита в базе данных", e);
        }
    }
}