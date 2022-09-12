package org.varmentano.nocode_plugin.persist.jdbc.mapping;

import org.varmentano.nocode_plugin.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;

import java.util.HashMap;
import java.util.Map;

public class DynamicEntityMapper {
    public UserDefinedObject mapToUdo(Map<String, Object> map, ObjectDefinition udoDef) {
        UserDefinedObject udo = new UserDefinedObject(udoDef);
        map.entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("$"))
                .forEach(entry -> udo.putData(entry.getKey(), entry.getValue()));
        return udo;
    }

    public Map<String, Object> mapToMap(UserDefinedObject udo) {
        HashMap<String, Object> mapObject = new HashMap<>();
        udo.getDefinition().fieldDefinitions()
                .forEach(fieldDef -> mapObject.put(fieldDef.name(), udo.getData(fieldDef.name())));
        return mapObject;
    }
}
