package ru.yaone.validator;

import ru.yaone.dto.ClientDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code ClientDTOValidator} предоставляет методы для валидации объектов {@code ClientDTO}.
 * <p>
 * Этот класс обеспечивает проверку корректности данных, содержащихся в объекте
 * {@code ClientDTO}, включая имя клиента и контактную информацию. При обнаружении ошибок
 * валидации, они добавляются в список ошибок, который возвращается как результат работы метода.
 * </p>
 */
public class ClientDTOValidator {

    /**
     * Проверяет корректность данных, содержащихся в {@code ClientDTO}.
     *
     * @param clientDTO объект {@code ClientDTO}, данные которого подлежат валидации.
     * @return список строк с описаниями ошибок валидации. Если ошибок нет, возвращается пустой список.
     */
    public static List<String> validate(ClientDTO clientDTO) {
        List<String> errors = new ArrayList<>();

        if (clientDTO.getClientName() == null || clientDTO.getClientName().isBlank()) {
            errors.add("Имя клиента является обязательным");
        }

        if (clientDTO.getContactInfo() == null || clientDTO.getContactInfo().isBlank()) {
            errors.add("Контактная информация является обязательной");
        }

        return errors;
    }
}