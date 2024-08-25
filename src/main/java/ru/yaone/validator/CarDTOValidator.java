package ru.yaone.validator;


import ru.yaone.dto.CarDTO;

import java.util.ArrayList;
import java.util.List;


public class CarDTOValidator {

    public static List<String> validate(CarDTO carDTO) {
        List<String> errors = new ArrayList<>();

        if (carDTO.getId() <= 0) {
            errors.add("ID не может быть нулевым и должен быть положительным");
        }

        if (carDTO.getMake() == null) {
            errors.add("Марка автомобиля не может быть нулевой");
        }

        if (carDTO.getModel() == null) {
            errors.add("Модель автомобиля не может быть нулевой");
        }

        if (carDTO.getYear() <= 1830) {
            errors.add("Год производства автомобиля не может быть меньше 1830 года");
        }

        if (carDTO.getPrice() <= 0) {
            errors.add("Цена автомобиля не может быть меньше нуля");
        }

        if (carDTO.getCondition() == null) {
            errors.add("Состояние автомобиля не может быть null");
        }

        return errors;
    }
}