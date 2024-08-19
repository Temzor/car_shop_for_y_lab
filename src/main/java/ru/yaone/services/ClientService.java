package ru.yaone.services;

import ru.yaone.dto.ClientDTO;
import ru.yaone.model.Client;

import java.util.List;

/**
 * Интерфейс, предоставляющий методы для управления клиентами.
 *
 * <p>Содержит методы для добавления, получения, удаления и обновления клиентов.</p>
 */
public interface ClientService {

    /**
     * Добавляет нового клиента.
     *
     * <p>Метод принимает объект {@link ClientDTO} и сохраняет его в базе данных.</p>
     *
     * @param clientDTO объект {@link ClientDTO}, который нужно добавить
     */
    void addClient(ClientDTO clientDTO);

    /**
     * Получает список всех клиентов.
     *
     * <p>Метод возвращает список объектов {@link ClientDTO}, представляющих всех клиентов,
     * сохраненных в базе данных.</p>
     *
     * @return список клиентов {@link List<Client>}
     */
    List<ClientDTO> getAllClients();

    /**
     * Получает клиента по уникальному идентификатору.
     *
     * <p>Если клиент с указанным идентификатором существует, метод возвращает объект
     * {@link ClientDTO}, иначе возвращает null.</p>
     *
     * @param id уникальный идентификатор клиента
     * @return объект {@link ClientDTO} с данными клиента или null, если клиент не найден
     */
    ClientDTO getClientById(int id);

    /**
     * Удаляет клиента по уникальному идентификатору.
     *
     * <p>Метод принимает идентификатор клиента и удаляет соответствующий
     * объект {@link ClientDTO} из базы данных.</p>
     *
     * @param id уникальный идентификатор клиента, которого нужно удалить
     */
    boolean deleteClientById(int id);

    /**
     * Обновляет информацию о клиенте.
     *
     * <p>Метод обновляет информацию о клиенте с указанным идентификатором
     * на основании данных, предоставленных в объекте {@link Client}.</p>
     *
     * @param id            уникальный идентификатор клиента, которого нужно обновить
     * @param updatedClientDTO объект {@link Client} с новыми данными
     */
    void updateClient(int id, ClientDTO updatedClientDTO);
}