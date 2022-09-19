package org.prairiekit.persist.jdbc;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.prairiekit.domain.definition.UdoDefinition;
import org.prairiekit.persist.jdbc.mapping.SessionFactoryMapper;
import org.prairiekit.service.UdoDefinitionService;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

public class UdoDefinitionServiceJdbc implements UdoDefinitionService {
    private final DataSource dataSource;
    private final SessionFactoryMapper factoryMapper;

    public UdoDefinitionServiceJdbc(DataSource dataSource, SessionFactoryMapper factoryMapper) {
        this.dataSource = dataSource;
        this.factoryMapper = factoryMapper;
    }

    public void deployDefinition(UdoDefinition udoDef) {
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE);
        factoryMapper.mapToSessionFactoryBuilder(dataSource, settings, udoDef)
                .build()    // Runs hibernate schema management tool
                .close();
    }
}
