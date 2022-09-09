package org.varmentano.nocode_plugin.jdbc.domain.definition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ObjectDefinition {
    private String name;
    private List<FieldDefinition> fieldDefinitions;

    public ObjectDefinition(String name, List<FieldDefinition> fieldDefinitions) {
        this.name = name;
        this.fieldDefinitions = fieldDefinitions;
    }

    public String getName() {
        return name;
    }

    public List<FieldDefinition> getFieldDefinitions() {
        return Collections.unmodifiableList(fieldDefinitions);
    }

}
