package org.varmentano.nocode_plugin.domain;

import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;

import java.util.HashMap;
import java.util.Map;

public class UserDefinedObject {
    private final ObjectDefinition schema;
    private int id;
    private final Map<String, Object> data = new HashMap<>();

    public UserDefinedObject(ObjectDefinition def) {
        this.schema = def;
    }

    public UserDefinedObject(ObjectDefinition def, int id) {
        this.schema = def;
        this.id = id;
    }

    public ObjectDefinition getDefinition() {
        return schema;
    }

    public int getId() {
        return id;
    }

    public Object getData(String fieldKey) {
        return data.get(fieldKey);
    }

    public void putData(String fieldKey, Object value) {
        data.put(fieldKey, value);
    }
}
