package ru.yaone.services;

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
     * <p>Метод принимает объект {@link Client} и сохраняет его в базе данных.</p>
     *
     * @param client объект {@link Client}, который нужно добавить
     */
    void addClient(Client client);

    /**
     * Получает список всех клиентов.
     *
     * <p>Метод возвращает список объектов {@link Client}, представляющих всех клиентов,
     * сохраненных в базе данных.</p>
     *
     * @return список клиентов {@link List<Client>}
     */
    List<Client> getAllClients();

    /**
     * Получает клиента по уникальному идентификатору.
     *
     * <p>Если клиент с указанным идентификатором существует, метод возвращает объект
     * {@link Client}, иначе возвращает null.</p>
     *
     * @param id уникальный идентификатор клиента
     * @return объект {@link Client} с данными клиента или null, если клиент не найден
     */
    Client getClientById(int id);

    /**
     * Удаляет клиента по уникальному идентификатору.
     *
     * <p>Метод принимает идентификатор клиента и удаляет соответствующий
     * объект {@link Client} из базы данных.</p>
     *
     * @param id уникальный идентификатор клиента, которого нужно удалить
     */
    void deleteClientById(int id);

    /**
     * Обновляет информацию о клиенте.
     *
     * <p>Метод обновляет информацию о клиенте с указанным идентификатором
     * на основании данных, предоставленных в объекте {@link Client}.</p>
     *
     * @param id            уникальный идентификатор клиента, которого нужно обновить
     * @param updatedClient объект {@link Client} с новыми данными
     */
    void updateClient(int id, Client updatedClient);
}