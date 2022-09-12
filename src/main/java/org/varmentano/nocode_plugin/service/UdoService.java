package org.varmentano.nocode_plugin.service;

import org.varmentano.nocode_plugin.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;

import java.util.Optional;

public interface UdoService {

    UdoDefinitionService getDefinitionService();

    Optional<UserDefinedObject> findById(ObjectDefinition udoDef, int id);

    Iterable<UserDefinedObject> findAll(ObjectDefinition udoDef);

    UserDefinedObject saveNew(UserDefinedObject myUdo);

    UserDefinedObject saveUpdate(UserDefinedObject myUdo);

    void deleteById(ObjectDefinition udoDef, int id);

}
