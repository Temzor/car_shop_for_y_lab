package ru.yaone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

/**
 * Класс {@code ClientDTO} представляет собой Data Transfer Object (DTO) для клиента.
 *
 * <p>Содержит информацию о клиенте, включая его идентификатор, имя и контактные данные.
 * Этот класс используется для передачи данных между слоями приложения.</p>
 *
 * @author Ваше имя
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    /**
     * Уникальный идентификатор клиента.
     *
     * <p>Не имеет дополнительных требований валидации.</p>
     */
    private int id;

    /**
     * Имя клиента.
     *
     * <p>Не должно быть пустым (не может быть {@code null} или состоять из пробелов).</p>
     */
    @NotBlank(message = "Client name is mandatory")
    private String clientName;

    /**
     * Контактная информация клиента.
     *
     * <p>Не должна быть пустой (не может быть {@code null} или состоять из пробелов).</p>
     */
    @NotBlank(message = "Contact information is mandatory")
    private String contactInfo;
}