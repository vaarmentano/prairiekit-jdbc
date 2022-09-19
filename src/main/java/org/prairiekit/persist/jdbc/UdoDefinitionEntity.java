package org.prairiekit.persist.jdbc;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity(name = "UdoDefinition")
public class UdoDefinitionEntity {
    @Id
    private String name;
    @ElementCollection
    private List<FieldDefinitionEntity> fieldDefinitions;

    public UdoDefinitionEntity() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldDefinitionEntity> getFieldDefinitions() {
        return fieldDefinitions;
    }

    public void setFieldDefinitions(List<FieldDefinitionEntity> fieldDefinitions) {
        this.fieldDefinitions = fieldDefinitions;
    }
}
