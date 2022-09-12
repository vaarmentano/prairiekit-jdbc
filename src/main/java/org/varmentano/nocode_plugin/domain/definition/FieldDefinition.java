package org.varmentano.nocode_plugin.domain.definition;

public record FieldDefinition(String type, String name, boolean id) {
    public FieldDefinition(String type, String name) {
        this(type, name, false);
    }
}