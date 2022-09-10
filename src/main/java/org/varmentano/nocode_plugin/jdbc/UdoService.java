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
import java.util.Map;

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
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.DEFAULT_ENTITY_MODE, EntityMode.MAP.getExternalName());
        SessionFactory sessionFactory = factoryMapper.mapToSessionFactoryBuilder(udoDef, dataSource, settings).build();

        Session session = sessionFactory.openSession();
        @SuppressWarnings("unchecked")
        Map<String, Object> objectMap = (Map<String, Object>) session.get(udoDef.name(), id);
        UserDefinedObject udo = entityMapper.mapToUdo(objectMap, udoDef);
        session.close();
        return udo;
    }

    public void saveNewUdo(UserDefinedObject myUdo) {
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.DEFAULT_ENTITY_MODE, EntityMode.MAP.getExternalName());
        SessionFactory sessionFactory =
                factoryMapper.mapToSessionFactoryBuilder(myUdo.getDefinition(), dataSource, settings).build();

        Map<String, Object> mapObject = entityMapper.mapToMap(myUdo);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(myUdo.getDefinition().name(), mapObject);
        transaction.commit();
        session.close();
    }
}
