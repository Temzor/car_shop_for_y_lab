package ru.yaone.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yaone.aspect.annotation.Loggable;
import ru.yaone.dto.ClientDTO;
import ru.yaone.mapper.ClientMapper;
import ru.yaone.model.Client;
import ru.yaone.services.ClientService;

import java.util.List;

/**
 * Контроллер для работы с клиентами.
 */
@Loggable("Логирование контроллера ClientController")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Client Controller", description = "Операции с клиентами")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    /**
     * Получить всех клиентов.
     *
     * @return Список клиентов в формате ClientDTO.
     */
    @GetMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Получить всех клиентов")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        List<ClientDTO> clientsDTO = clientMapper.clientToClientDTO(clients);
        return ResponseEntity.ok(clientsDTO);
    }

    /**
     * Получить клиента по ID.
     *
     * @param id Идентификатор клиента.
     * @return Клиент в формате ClientDTO, если найден, иначе 404.
     */
    @GetMapping("/clients/{id}")
    @Operation(description = "Получить клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно найден клиент"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    public ResponseEntity<ClientDTO> getClientById(
            @Parameter(description = "ID клиента", required = true) @PathVariable("id") int id) {

        Client client = clientService.getClientById(id);
        return client != null
                ? ResponseEntity.ok(clientMapper.clientToClientDTO(client))
                : ResponseEntity.notFound().build();
    }

    /**
     * Добавить нового клиента.
     *
     * @param clientDTO Данные клиента для добавления.
     * @return Статус 201, если клиент успешно добавлен.
     */
    @PostMapping(value = "/clients")
    @Operation(description = "Добавить нового клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные клиента")
    })
    public ResponseEntity<Void> addClient(
            @Parameter(description = "Данные клиента", required = true) @RequestBody ClientDTO clientDTO) {

        Client client = clientMapper.clientDTOToClient(clientDTO);
        clientService.addClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Удалить клиента по ID.
     *
     * @param id Идентификатор клиента для удаления.
     * @return Статус 204, если клиент успешно удален, или 404, если клиент не найден.
     */
    @DeleteMapping("/clients/{id}")
    @Operation(description = "Удалить клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиент успешно удален"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    public ResponseEntity<Void> deleteClientById(
            @Parameter(description = "ID клиента", required = true) @PathVariable("id") int id) {
        boolean isDeleted = clientService.deleteClientById(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Обновить информацию о клиенте.
     *
     * @param id               Идентификатор клиента для обновления.
     * @param updatedClientDTO Обновленные данные клиента.
     * @return Статус 200, если информация успешно обновлена,
     * статус 400, если данные неверны,
     * статус 404, если клиент не найден,
     * статус 500 при ошибке сервера.
     */
    @PutMapping("/clients/{id}")
    @Operation(description = "Обновить информацию о клиенте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о клиенте обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные клиента"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateClient(
            @Parameter(description = "ID клиента", required = true) @PathVariable("id") int id,
            @Parameter(description = "Обновленные данные клиента", required = true) @RequestBody ClientDTO updatedClientDTO) {

        if (clientService.getClientById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        Client updatedClient = clientMapper.clientDTOToClient(updatedClientDTO);
        try {
            clientService.updateClient(id, updatedClient);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}