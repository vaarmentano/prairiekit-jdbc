package org.prairiekit.domain;

import org.prairiekit.domain.definition.UdoDefinition;

import java.util.HashMap;
import java.util.Map;

public class UserDefinedObject {
    private final UdoDefinition schema;
    private int id;
    private final Map<String, Object> data = new HashMap<>();

    public UserDefinedObject(UdoDefinition def) {
        this.schema = def;
    }

    public UserDefinedObject(UdoDefinition def, int id) {
        this.schema = def;
        this.id = id;
    }

    public UdoDefinition getDefinition() {
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
