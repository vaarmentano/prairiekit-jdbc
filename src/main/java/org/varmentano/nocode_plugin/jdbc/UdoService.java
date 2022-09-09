package org.varmentano.nocode_plugin.jdbc;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.varmentano.nocode_plugin.jdbc.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.jdbc.domain.definition.ObjectDefinition;
import org.varmentano.nocode_plugin.jdbc.mapping.HibernateMapper;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UdoService {
    private final DataSource dataSource;

    public UdoService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * WARNING - Only handles deploying a new configuration, does not update an existing def
     * @param udoDef Definition
     */
    public void deployDefinition(ObjectDefinition udoDef) {
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE);
        new HibernateMapper()
                .mapToSessionFactoryBuilder(udoDef, dataSource, settings)
                .build()    // Runs hibernate schema management tool
                .close();
    }

    public void saveNewUdo(UserDefinedObject myUdo) {
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.DEFAULT_ENTITY_MODE, EntityMode.MAP.getExternalName());
        SessionFactory sessionFactory = new HibernateMapper()
                .mapToSessionFactoryBuilder(myUdo.getDefinition(), dataSource, settings)
                .build();

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        HashMap<String, Object> mapObject = new HashMap<>();
        myUdo.getDefinition().getFieldDefinitions()
                .forEach(fieldDef -> mapObject.put(fieldDef.getName(), myUdo.getData(fieldDef.getName())));
        session.save(myUdo.getDefinition().getName(), mapObject);
        transaction.commit();
        session.close();
    }

}
