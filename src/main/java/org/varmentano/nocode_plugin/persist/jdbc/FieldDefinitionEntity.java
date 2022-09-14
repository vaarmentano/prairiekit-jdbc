package org.varmentano.nocode_plugin.persist.jdbc;

import org.varmentano.nocode_plugin.domain.definition.FieldType;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class FieldDefinitionEntity {
    @Enumerated(EnumType.STRING)
    private FieldType type;
    private String name;

    public FieldDefinitionEntity() {
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
