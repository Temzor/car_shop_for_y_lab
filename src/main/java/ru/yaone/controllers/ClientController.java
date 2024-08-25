package ru.yaone.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Loggable("Логирование контроллера ClientController")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Api(value = "Client Controller", tags = "Операции с клиентами")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @GetMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получить всех клиентов", response = ClientDTO.class, responseContainer = "List")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        List<ClientDTO> clientsDTO = clientMapper.clientToClientDTO(clients);
        return ResponseEntity.ok(clientsDTO);
    }

    @GetMapping("/clients/{id}")
    @ApiOperation(value = "Получить клиента по ID", response = ClientDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно найден клиент"),
            @ApiResponse(code = 404, message = "Клиент не найден")
    })
    public ResponseEntity<ClientDTO> getClientById(
            @ApiParam(value = "ID клиента", required = true) @PathVariable("id") int id) {
        Client client = clientService.getClientById(id);
        if (client != null) {
            ClientDTO clientDTO = clientMapper.clientToClientDTO(client);
            return ResponseEntity.ok(clientDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/clients")
    @ApiOperation(value = "Добавить нового клиента")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Клиент успешно добавлен"),
            @ApiResponse(code = 400, message = "Неверные данные клиента")
    })
    public ResponseEntity<Void> addClient(
            @ApiParam(value = "Данные клиента", required = true) @RequestBody ClientDTO clientDTO) {
        Client client = clientMapper.clientDTOToClient(clientDTO);
        clientService.addClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/clients/{id}")
    @ApiOperation(value = "Удалить клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Клиент успешно удален"),
            @ApiResponse(code = 404, message = "Клиент не найден")
    })
    public ResponseEntity<Void> deleteClientById(
            @ApiParam(value = "ID клиента", required = true) @PathVariable("id") int id) {
        boolean isDeleted = clientService.deleteClientById(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/clients/{id}")
    @ApiOperation(value = "Обновить информацию о клиенте")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о клиенте обновлена"),
            @ApiResponse(code = 400, message = "Неверные данные клиента"),
            @ApiResponse(code = 404, message = "Клиент не найден"),
            @ApiResponse(code = 500, message = "Ошибка сервера")
    })
    public ResponseEntity<Void> updateClient(
            @ApiParam(value = "ID клиента", required = true) @PathVariable("id") int id,
            @ApiParam(value = "Обновленные данные клиента", required = true) @RequestBody ClientDTO updatedClientDTO) {
        if (updatedClientDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        Client updatedClient = clientMapper.clientDTOToClient(updatedClientDTO);
        try {
            if (clientService.getClientById(id) == null) {
                return ResponseEntity.notFound().build();
            }
            clientService.updateClient(id, updatedClient);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}