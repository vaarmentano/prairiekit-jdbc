package org.varmentano.nocode_plugin.jdbc;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.varmentano.nocode_plugin.jdbc.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.jdbc.domain.definition.ObjectDefinition;
import org.varmentano.nocode_plugin.jdbc.mapping.DynamicEntityMapper;
import org.varmentano.nocode_plugin.jdbc.mapping.SessionFactoryMapper;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UdoService {
    private final DataSource dataSource;
    private final SessionFactoryMapper factoryMapper;
    private final DynamicEntityMapper entityMapper;

    public UdoService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.factoryMapper = new SessionFactoryMapper();
        this.entityMapper = new DynamicEntityMapper();
    }

    /**
     * WARNING - Only handles deploying a new configuration, does not update an existing def
     *
     * @param udoDef Definition
     */
    public void deployDefinition(ObjectDefinition udoDef) {
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE);
        factoryMapper.mapToSessionFactoryBuilder(udoDef, dataSource, settings)
                .build()    // Runs hibernate schema management tool
                .close();
    }

    public UserDefinedObject getUdoById(ObjectDefinition udoDef, int id) {
        return performSessionAction(session -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> objectMap = (Map<String, Object>) session.get(udoDef.name(), id);
            return entityMapper.mapToUdo(objectMap, udoDef);
        }, udoDef);
    }

    @SuppressWarnings("unchecked")
    public List<UserDefinedObject> listUdos(ObjectDefinition udoDef) {
        return performSessionAction(session -> session
                .createQuery("from " + udoDef.name(), Object.class)
                .getResultList().stream()
                .map(obj -> (Map<String, Object>) obj)
                .map(map -> entityMapper.mapToUdo(map, udoDef))
                .collect(Collectors.toList()), udoDef);
    }

    public void saveNewUdo(UserDefinedObject myUdo) {
        ObjectDefinition udoDef = myUdo.getDefinition();
        performTransaction(session -> {
            Map<String, Object> mapObject = entityMapper.mapToMap(myUdo);
            session.save(myUdo.getDefinition().name(), mapObject);
        }, udoDef);
    }

    public void updateUdo(UserDefinedObject myUdo) {
        ObjectDefinition udoDef = myUdo.getDefinition();
        performTransaction(session -> {
            Map<String, Object> mapObject = entityMapper.mapToMap(myUdo);
            session.update(myUdo.getDefinition().name(), mapObject);
        }, udoDef);
    }

    public void deleteUdo(ObjectDefinition udoDef, int id) {
        performTransaction(session -> {
            Map<String, Object> mapObject = new HashMap<>(1);
            mapObject.put("id", id);
            session.delete(udoDef.name(), mapObject);
        }, udoDef);
    }

    private void performTransaction(Consumer<Session> action, ObjectDefinition udoDef) {
        performSessionAction(session -> {
            Transaction transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
            return null;
        }, udoDef);
    }

    private <R> R performSessionAction(Function<Session, R> action, ObjectDefinition udoDef) {
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.DEFAULT_ENTITY_MODE, EntityMode.MAP.getExternalName());
        SessionFactory sessionFactory = factoryMapper.mapToSessionFactoryBuilder(udoDef, dataSource, settings).build();
        Session session = sessionFactory.openSession();
        R result = action.apply(session);
        session.close();
        return result;
    }
}
