package org.varmentano.nocode_plugin.persist.jdbc;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.varmentano.nocode_plugin.domain.definition.UdoDefinition;
import org.varmentano.nocode_plugin.persist.jdbc.mapping.SessionFactoryMapper;
import org.varmentano.nocode_plugin.service.UdoDefinitionService;

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
