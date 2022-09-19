package org.prairiekit.service;

import org.prairiekit.domain.definition.UdoDefinition;

public interface UdoService {

    void deployDefinition(UdoDefinition udoDef);

    UdoRepository getUdoRepository(String definitionId);
}
