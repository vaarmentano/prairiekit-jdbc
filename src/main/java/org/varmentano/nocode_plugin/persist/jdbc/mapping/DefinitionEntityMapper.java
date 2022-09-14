package org.varmentano.nocode_plugin.persist.jdbc.mapping;

import org.varmentano.nocode_plugin.domain.definition.FieldDefinition;
import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;
import org.varmentano.nocode_plugin.persist.jdbc.FieldDefinitionEntity;
import org.varmentano.nocode_plugin.persist.jdbc.UdoDefinitionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefinitionEntityMapper {
    public UdoDefinitionEntity mapDefinitionToEntity(ObjectDefinition def) {
        UdoDefinitionEntity entity = new UdoDefinitionEntity();
        entity.setName(def.name());
        entity.setFieldDefinitions(mapFieldDefsToEntity(def.fieldDefinitions()));
        return entity;
    }

    private List<FieldDefinitionEntity> mapFieldDefsToEntity(List<FieldDefinition> fieldDefinitions) {
        return fieldDefinitions.stream()
                .map(this::mapFieldDefToEntity)
                .collect(Collectors.toList());
    }

    private FieldDefinitionEntity mapFieldDefToEntity(FieldDefinition def) {
        FieldDefinitionEntity entity = new FieldDefinitionEntity();
        entity.setName(def.name());
        entity.setType(def.type());
        return entity;
    }

    public ObjectDefinition mapDefinitionFromEntity(UdoDefinitionEntity definitionEntity) {
        return new ObjectDefinition(
                definitionEntity.getName(),
                mapFieldDefsFromEntity(definitionEntity.getFieldDefinitions()));
    }

    private List<FieldDefinition> mapFieldDefsFromEntity(List<FieldDefinitionEntity> fieldDefinitions) {
        return fieldDefinitions.stream()
                .map(this::mapFieldDefFromEntity)
                .collect(Collectors.toList());
    }

    private FieldDefinition mapFieldDefFromEntity(FieldDefinitionEntity def) {
        return new FieldDefinition(def.getType(), def.getName());
    }
}
