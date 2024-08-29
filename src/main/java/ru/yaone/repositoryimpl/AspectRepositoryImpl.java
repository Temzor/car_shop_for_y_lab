package ru.yaone.repositoryimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.constants.SqlScriptsForAudit;
import ru.yaone.manager.DatabaseConnectionManager;
import ru.yaone.repository.AspectRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Arrays;

@Repository
@Loggable("Логирование класса AspectRepositoryImpl")
@RequiredArgsConstructor
public class AspectRepositoryImpl implements AspectRepository {
    private final DatabaseConnectionManager databaseConnectionManager;

    @Loggable("Логирование метода AspectRepositoryImpl.saveAuditLog")
    @Override
    public void saveAuditLog(String methodName, Object[] methodArgs, long executionTime, Object result) {
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
        }
    }
}
