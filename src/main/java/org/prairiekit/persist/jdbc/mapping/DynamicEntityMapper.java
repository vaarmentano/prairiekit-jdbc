package org.prairiekit.persist.jdbc.mapping;

import org.prairiekit.domain.UserDefinedObject;
import org.prairiekit.domain.definition.UdoDefinition;

import java.util.HashMap;
import java.util.Map;

public class DynamicEntityMapper {
    public static final String ID_COL_NAME = "id";

    public UserDefinedObject mapToUdo(Map<String, Object> map, UdoDefinition udoDef) {
        UserDefinedObject udo = new UserDefinedObject(udoDef, ((int) map.get(ID_COL_NAME)));
        map.entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("$"))
                .filter(entry -> !entry.getKey().equals(ID_COL_NAME))
                .forEach(entry -> udo.putData(entry.getKey(), entry.getValue()));
        return udo;
    }

    public Map<String, Object> mapToMap(UserDefinedObject udo) {
        HashMap<String, Object> mapObject = new HashMap<>();
        mapObject.put(ID_COL_NAME, udo.getId());
        udo.getDefinition().fieldDefinitions()
                .forEach(fieldDef -> mapObject.put(fieldDef.name(), udo.getData(fieldDef.name())));
        return mapObject;
    }
}
