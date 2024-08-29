package ru.yaone.repository;

public interface AspectRepository {
    void saveAuditLog(String methodName, Object[] methodArgs, long executionTime, Object result);
}
