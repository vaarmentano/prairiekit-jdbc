package org.varmentano.nocode_plugin.jdbc.domain;

import org.varmentano.nocode_plugin.jdbc.domain.definition.ObjectDefinition;

import java.util.HashMap;
import java.util.Map;

public class UserDefinedObject {
    private final ObjectDefinition schema;
    private final Map<String, Object> data = new HashMap<>();

    public UserDefinedObject(ObjectDefinition def) {
        this.schema = def;
    }

    public ObjectDefinition getDefinition() {
        return schema;
    }

    public Object getData(String fieldKey) {
        return data.get(fieldKey);
    }

    public void putData(String fieldKey, Object value) {
        data.put(fieldKey, value);
    }
}
