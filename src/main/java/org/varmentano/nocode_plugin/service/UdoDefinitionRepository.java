package org.varmentano.nocode_plugin.service;

import org.varmentano.nocode_plugin.domain.definition.UdoDefinition;

import java.util.Optional;

public interface UdoDefinitionRepository {
    Optional<UdoDefinition> findById(String id);

    void saveNew(UdoDefinition udoDef);
}
