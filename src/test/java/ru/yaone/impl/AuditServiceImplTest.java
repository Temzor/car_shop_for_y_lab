// AuditServiceImplTest.java
package ru.yaone.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yaone.model.AuditLog;
import ru.yaone.model.User;
import ru.yaone.model.enumeration.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class AuditServiceImplTest {

    private AuditServiceImpl auditService;
    private User user;

    @BeforeEach
    public void setUp() {
        auditService = new AuditServiceImpl();
        new User(1, "Palkin", "123", UserRole.ADMIN);
    }

    @Test
    public void testLogAction() {
        String action = "testAction";
        auditService.logAction(user, action);

        List<AuditLog> logs = auditService.getAllLogs();
        assertThat(logs).isNotEmpty();
        assertThat(logs).hasSize(1);
    }

    @Test
    public void testGetAllLogs() {
        auditService.logAction(user, "action1");
        auditService.logAction(user, "action2");

        List<AuditLog> logs = auditService.getAllLogs();
        assertThat(logs).hasSize(2);
    }

    @Test
    public void testGetAllLogsEmptyList() {
        List<AuditLog> logs = auditService.getAllLogs();
        assertThat(logs).isEmpty();
    }
}