package ru.yaone.app;

import org.junit.jupiter.api.*;
import ru.yaone.impl.CarServiceImpl;
import ru.yaone.impl.OrderServiceImpl;
import ru.yaone.impl.UserServiceImpl;
import ru.yaone.model.*;
import ru.yaone.model.Order;
import ru.yaone.model.enumeration.*;
import ru.yaone.services.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarShopAppTest {

    private CarService carService;
    private OrderService orderService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        carService = new CarServiceImpl();
        orderService = new OrderServiceImpl();
        userService = new UserServiceImpl();
    }

    @Test
    void testAddCar() {
        Car car = new Car(1, "Toyota", "Camry", 2020, 25000, CarCondition.NEW);
        carService.addCar(car);
        assertEquals(1, carService.getAllCars().size());
        assertEquals(car, carService.getCarById(1));
    }

    @Test
    void testSearchCars() {
        carService.addCar(new Car(1, "Toyota", "Camry", 2020, 25000, CarCondition.NEW));
        carService.addCar(new Car(2, "Honda", "Civic", 2019, 20000, CarCondition.USED));

        List<Car> searchedCars = carService.searchCars("Honda", "Civic", 2019, 20000, CarCondition.USED);
        assertEquals(1, searchedCars.size());
    }

    @Test
    void testAddOrder() {
        User user = new User(1, "user1", "password", UserRole.CLIENT);
        Car car = new Car(1, "Toyota", "Camry", 2020, 25000, CarCondition.NEW);
        Client client = new Client(user, "contact@yaone.ru");
        Order order = new Order(1, client, car, LocalDateTime.now(), OrderStatus.PENDING);

        orderService.addOrder(order);
        assertEquals(1, orderService.getAllOrders().size());
        assertEquals(order, orderService.getOrderById(1));
    }

    @Test
    void testUserManagement() {
        User user = new User(1, "user1", "password", UserRole.CLIENT);
        userService.addUser(user);

        assertEquals(1, userService.getAllUsers().size());
        assertEquals(user, userService.getUserById(1));
    }

    @Test
    void testRemoveCar() {
        Car car = new Car(1, "Toyota", "Camry", 2020, 25000, CarCondition.NEW);
        carService.addCar(car);
        assertEquals(1, carService.getAllCars().size());

        carService.deleteCar(1);
        assertEquals(0, carService.getAllCars().size());
    }

    @Test
    void testCancelOrder() {
        User user = new User(1, "user1", "password", UserRole.CLIENT);
        Car car = new Car(1, "Toyota", "Camry", 2020, 25000, CarCondition.NEW);
        Client client = new Client(user, "contact@yaone.ru");
        Order order = new Order(1, client, car, LocalDateTime.now(), OrderStatus.PENDING);
        orderService.addOrder(order);

        assertEquals(1, orderService.getAllOrders().size());
        orderService.deleteOrder(1);
        assertEquals(0, orderService.getAllOrders().size());
    }

    @Test
    void testInvalidCarSearch() {
        carService.addCar(new Car(1, "Toyota", "Camry", 2020, 25000, CarCondition.NEW));
        List<Car> searchedCars = carService.searchCars("Ford", "Focus", 2021, 30000, CarCondition.NEW);
        assertEquals(0, searchedCars.size());
    }

    @Test
    void testDuplicateUserAddition() {
        User user = new User(1, "user1", "password", UserRole.CLIENT);
        userService.addUser(user);
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(user));
    }

    @AfterEach
    void tearDown() {
        // Очистка состояния между тестами, если необходимо
    }
}