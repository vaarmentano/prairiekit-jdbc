package org.varmentano.nocode_plugin.persist.jdbc;

import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;
import org.varmentano.nocode_plugin.persist.jdbc.mapping.SessionFactoryMapper;
import org.varmentano.nocode_plugin.service.UdoDefinitionService;
import org.varmentano.nocode_plugin.service.UdoRepository;
import org.varmentano.nocode_plugin.service.UdoService;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class UdoServiceJdbc implements UdoService {
    private final DataSource dataSource;
    private final SessionFactoryMapper factoryMapper;
    private final UdoDefinitionService defService;
    private final Map<String, UdoRepository> udoRepos = new HashMap<>();

    public UdoServiceJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
        this.factoryMapper = new SessionFactoryMapper();
        this.defService = new UdoDefinitionServiceJdbc(dataSource, factoryMapper);
    }

    @Override
    public void deployDefinition(ObjectDefinition udoDef) {
        defService.deployDefinition(udoDef);
        UdoRepositoryJdbc udoRepo = new UdoRepositoryJdbc(dataSource, udoDef, factoryMapper);
        udoRepos.put(udoDef.name(), udoRepo);
    }

    @Override
    public UdoRepository getUdoRepository(String definitionId) {
        return udoRepos.get(definitionId);
    }


}
