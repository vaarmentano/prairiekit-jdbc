package org.varmentano.nocode_plugin.service;

import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;

import java.util.Optional;

public interface UdoDefinitionRepository {
    Optional<ObjectDefinition> findById(String id);

    void saveNew(ObjectDefinition udoDef);
}
