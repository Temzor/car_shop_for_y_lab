package ru.yaone.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yaone.dto.ClientDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientDTOValidatorTest {

    @Test
    @DisplayName("Тест: валидный объект ClientDTO")
    public void testValidClientDTO() {
        ClientDTO clientDTO = new ClientDTO(1, "John Doe", "john.doe@example.com");
        List<String> validationResult = ClientDTOValidator.validate(clientDTO);
        assertTrue(validationResult.isEmpty(), "Validation should pass for valid DTO.");
    }

    @Test
    @DisplayName("Тест: пустое имя клиента")
    public void testEmptyClientName() {
        ClientDTO clientDTO = new ClientDTO(1, "", "john.doe@example.com");
        List<String> validationResult = ClientDTOValidator.validate(clientDTO);
        assertFalse(validationResult.contains("Client name is mandatory"), "Validation should fail for empty client name.");
    }

    @Test
    @DisplayName("Тест: пустая контактная информация")
    public void testEmptyContactInfo() {
        ClientDTO clientDTO = new ClientDTO(1, "John Doe", "");
        List<String> validationResult = ClientDTOValidator.validate(clientDTO);
        assertFalse(validationResult.contains("Contact information is mandatory"), "Validation should fail for empty contact information.");
    }
}