package ru.yaone.validator;

import ru.yaone.dto.OrderDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code OrderDTOValidator} обеспечивает валидацию объектов {@code OrderDTO}.
 * <p>
 * Этот класс проверяет корректность данных, содержащихся в объекте {@code OrderDTO},
 * включая наличие идентификаторов, даты создания и статуса заказа. При наличии ошибок
 * валидации они добавляются в список ошибок, который возвращается как результат работы метода.
 * </p>
 */
public class OrderDTOValidator {

    /**
     * Проверяет корректность данных, содержащихся в {@code OrderDTO}.
     *
     * @param orderDTO объект {@code OrderDTO}, данные которого подлежат валидации.
     * @return список строк с описаниями ошибок валидации. Если ошибок нет, возвращается пустой список.
     */
    public static List<String> validate(OrderDTO orderDTO) {
        List<String> errors = new ArrayList<>();

        if (orderDTO.getId() <= 0) {
            errors.add("ID не может быть нулевым и должен быть положительным");
        }

        if (orderDTO.getClientId() <= 0) {
            errors.add("Идентификатор клиента является обязательным");
        }

        if (orderDTO.getCarId() <= 0) {
            errors.add("Идентификатор автомобиля является обязательным");
        }

        if (orderDTO.getCreationDate() == null) {
            errors.add("Дата не может быть нулевой");
        }

        if (orderDTO.getStatus() == null) {
            errors.add("Статус заказа не может быть нулевым");
        }

        return errors;
    }
}