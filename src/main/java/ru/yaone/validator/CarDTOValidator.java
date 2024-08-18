package ru.yaone.validator;

import ru.yaone.dto.CarDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code CarDTOValidator} предоставляет методы для валидации объектов {@code CarDTO}.
 * <p>
 * Этот класс обеспечивает проверку корректности данных, содержащихся в объекте
 * {@code CarDTO}, включая идентификатор автомобиля, марку, модель, год выпуска,
 * цену и состояние автомобиля. При обнаружении ошибок валидации, они добавляются в
 * список ошибок, который возвращается как результат работы метода.
 * </p>
 */
public class CarDTOValidator {

    /**
     * Проверяет корректность данных, содержащихся в {@code CarDTO}.
     *
     * @param carDTO объект {@code CarDTO}, данные которого подлежат валидации.
     * @return список строк с описаниями ошибок валидации. Если ошибок нет, возвращается пустой список.
     */
    public static List<String> validate(CarDTO carDTO) {
        List<String> errors = new ArrayList<>();

        if (carDTO.getId() <= 0) {
            errors.add("ID должен быть положительным");
        }

        if (carDTO.getMake() == null || carDTO.getMake().isBlank()) {
            errors.add("Марка является обязательной");
        }

        if (carDTO.getModel() == null || carDTO.getModel().isBlank()) {
            errors.add("Модель является обязательной");
        }

        if (carDTO.getYear() <= 0) {
            errors.add("Год должен быть положительным");
        }

        if (carDTO.getPrice() <= 0) {
            errors.add("Цена должна быть положительной");
        }

        if (carDTO.getCondition() == null) {
            errors.add("Состояние автомобиля не может быть null");
        }

        return errors;
    }
}