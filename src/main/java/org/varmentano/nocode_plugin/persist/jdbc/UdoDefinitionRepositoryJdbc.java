package org.varmentano.nocode_plugin.persist.jdbc;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.varmentano.nocode_plugin.domain.definition.UdoDefinition;
import org.varmentano.nocode_plugin.persist.jdbc.mapping.DefinitionEntityMapper;
import org.varmentano.nocode_plugin.service.UdoDefinitionRepository;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class UdoDefinitionRepositoryJdbc implements UdoDefinitionRepository {
    private final SessionFactory sessionFactory;
    private final DefinitionEntityMapper definitionMapper;

    public UdoDefinitionRepositoryJdbc(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.definitionMapper = new DefinitionEntityMapper();
    }

    @Override
    public Optional<UdoDefinition> findById(String id) {
        return performSessionAction(session -> {
            UdoDefinitionEntity definitionEntity = session.get(UdoDefinitionEntity.class, id);
            UdoDefinition definition = definitionMapper.mapDefinitionFromEntity(definitionEntity);
            return Optional.ofNullable(definition);
        });
    }

    @Override
    public void saveNew(UdoDefinition udoDef) {
        performTransaction(session -> session.save(definitionMapper.mapDefinitionToEntity(udoDef)));
    }

    private void performTransaction(Consumer<Session> action) {
        performSessionAction(session -> {
            Transaction transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
            return null;
        });
    }

    private <R> R performSessionAction(Function<Session, R> action) {
        Session session = sessionFactory.openSession();
        R result = action.apply(session);
        session.close();
        return result;
    }
}
