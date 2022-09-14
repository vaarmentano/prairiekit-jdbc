package org.varmentano.nocode_plugin.service;

import org.varmentano.nocode_plugin.domain.definition.UdoDefinition;

public interface UdoDefinitionService {
    /**
     * WARNING - Only handles deploying a new configuration, does not update an existing def
     *
     * @param udoDef Definition
     */
    void deployDefinition(UdoDefinition udoDef);
}
