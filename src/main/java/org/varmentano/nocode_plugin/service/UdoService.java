package org.varmentano.nocode_plugin.service;

import org.varmentano.nocode_plugin.domain.definition.UdoDefinition;

public interface UdoService {

    void deployDefinition(UdoDefinition udoDef);

    UdoRepository getUdoRepository(String definitionId);
}
