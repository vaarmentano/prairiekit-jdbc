package org.prairiekit.service;

import org.prairiekit.domain.definition.UdoDefinition;

import java.util.Optional;

public interface UdoDefinitionRepository {
    Optional<UdoDefinition> findById(String id);

    void saveNew(UdoDefinition udoDef);
}
