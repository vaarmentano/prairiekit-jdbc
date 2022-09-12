package org.varmentano.nocode_plugin.service;

import org.varmentano.nocode_plugin.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;

import java.util.List;

public interface UdoService {

    UdoDefinitionService getDefinitionService();

    UserDefinedObject getUdoById(ObjectDefinition udoDef, int id);

    List<UserDefinedObject> listUdos(ObjectDefinition udoDef);

    void saveNewUdo(UserDefinedObject myUdo);

    void updateUdo(UserDefinedObject myUdo);

    void deleteUdo(ObjectDefinition udoDef, int id);

}
