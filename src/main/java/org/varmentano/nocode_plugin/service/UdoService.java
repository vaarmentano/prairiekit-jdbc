package org.varmentano.nocode_plugin.service;

import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;

public interface UdoService {

    void deployDefinition(ObjectDefinition udoDef);

    UdoRepository getUdoRepository(String definitionId);
}
