package org.varmentano.nocode_plugin.jdbc.domain.definition;

public class FieldDefinition {
    private String type;
    private String name;
    private boolean id;

    public FieldDefinition(String type, String name) {
        this(type, name, false);
    }
    public FieldDefinition(String type, String name, boolean id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    public boolean isId() {
        return id;
    }
}
