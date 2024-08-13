package ru.yaone.impl;

import ru.yaone.services.*;

public class CarShopFactory implements ItemFactory {

    @Override
    public AuditService getAuditService() {
        return new AuditServiceImpl();
    }

    @Override
    public CarService getCarService() {
        return new CarServiceImpl();
    }

    @Override
    public ClientService getClientService() {
        return new ClientServiceImpl();
    }

    @Override
    public OrderService getOrderService() {
        return new OrderServiceImpl();
    }

    @Override
    public UserService getUserService() {
        return new UserServiceImpl();
    }
}