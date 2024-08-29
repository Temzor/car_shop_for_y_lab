package ru.yaone.services;

/**
 * Интерфейс {@code ItemFactory} предоставляет методы для получения сервисов
 * связанные с различными аспектами бизнес-логики приложения.
 *
 * <p>Интерфейс назначает методы для доступа к следующим сервисам:
 * {@link AuditService}, {@link CarService}, {@link ClientService},
 * {@link OrderService}, и {@link UserService}. Каждый из этих сервисов
 * управляет соответствующей частью приложения.</p>
 */
public interface ItemFactory {

    /**
     * Получает сервис для работы с аудитом.
     *
     * @return экземпляр {@link AuditService}
     */
    AuditService getAuditService();

    /**
     * Получает сервис для работы с автомобилями.
     *
     * @return экземпляр {@link CarService}
     */
    CarService getCarService();

    /**
     * Получает сервис для управления клиентами.
     *
     * @return экземпляр {@link ClientService}
     */
    ClientService getClientService();

    /**
     * Получает сервис для работы с заказами.
     *
     * @return экземпляр {@link OrderService}
     */
    OrderService getOrderService();

    /**
     * Получает сервис для работы с пользователями.
     *
     * @return экземпляр {@link UserService}
     */
    UserService getUserService();
}