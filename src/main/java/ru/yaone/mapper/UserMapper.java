package ru.yaone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.yaone.dto.UserDTO;
import ru.yaone.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role")
    })
    UserDTO userToUserDTO(User user);

    List<UserDTO> userToUserDTO(List<User> users);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "role", target = "role")
    })
    User userDTOToUser(UserDTO userDTO);

    List<User> userDTOToUser(List<UserDTO> usersDRO);
}