package ru.yaone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.yaone.dto.OrderDTO;
import ru.yaone.model.Order;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "clientId", target = "clientId"),
            @Mapping(source = "carId", target = "carId"),
            @Mapping(source = "creationDate", target = "creationDate"),
            @Mapping(source = "status", target = "status"),
    })
    OrderDTO orderToOrderDTO(Order order);

    List<OrderDTO> orderToOrderDTO(List<Order> orders);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "clientId", target = "clientId"),
            @Mapping(source = "carId", target = "carId"),
            @Mapping(source = "creationDate", target = "creationDate"),
            @Mapping(source = "status", target = "status"),
    })
    Order orderDTOToOrder(OrderDTO orderDTO);

    List<Order> orderDTOToOrder(List<OrderDTO> orderDTO);
}