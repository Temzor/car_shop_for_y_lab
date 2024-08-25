package ru.yaone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.yaone.dto.ClientDTO;
import ru.yaone.model.Client;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "clientName", target = "clientName"),
            @Mapping(source = "contactInfo", target = "contactInfo"),
    })
    ClientDTO clientToClientDTO(Client client);

    List<ClientDTO> clientToClientDTO(List<Client> clients);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "clientName", target = "clientName"),
            @Mapping(source = "contactInfo", target = "contactInfo"),
    })
    Client clientDTOToClient(ClientDTO clientDTO);

    List<Client> clientDTOToClient(List<Client> clientsDTO);
}