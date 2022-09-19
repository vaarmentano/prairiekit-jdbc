package org.prairiekit.persist.jdbc;

import org.hibernate.SessionFactory;
import org.prairiekit.domain.definition.UdoDefinition;
import org.prairiekit.persist.jdbc.mapping.SessionFactoryMapper;
import org.prairiekit.service.UdoDefinitionRepository;
import org.prairiekit.service.UdoDefinitionService;
import org.prairiekit.service.UdoRepository;
import org.prairiekit.service.UdoService;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class UdoServiceJdbc implements UdoService {
    private final DataSource dataSource;
    private final SessionFactoryMapper factoryMapper;
    private final UdoDefinitionService defService;
    private final UdoDefinitionRepository defRepository;
    private final Map<String, UdoRepository> udoRepos = new HashMap<>();

    public UdoServiceJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
        this.factoryMapper = new SessionFactoryMapper();
        this.defService = new UdoDefinitionServiceJdbc(dataSource, factoryMapper);
        SessionFactory defRepoSessionFactory = factoryMapper.mapToSessionFactoryBuilder(dataSource).build();
        this.defRepository = new UdoDefinitionRepositoryJdbc(defRepoSessionFactory);
    }

    @Override
    public void deployDefinition(UdoDefinition udoDef) {
        defService.deployDefinition(udoDef);
        defRepository.saveNew(udoDef);
        udoRepos.put(udoDef.name(), new UdoRepositoryJdbc(dataSource, udoDef, factoryMapper));
    }

    @Override
    public UdoRepository getUdoRepository(String definitionId) {
        UdoRepository udoRepo = udoRepos.get(definitionId);
        if (udoRepo == null) {
            UdoDefinition udoDef = defRepository.findById(definitionId).orElseThrow();
            udoRepos.put(definitionId, new UdoRepositoryJdbc(dataSource, udoDef, factoryMapper));
        }
        return udoRepos.get(definitionId);
    }


}
