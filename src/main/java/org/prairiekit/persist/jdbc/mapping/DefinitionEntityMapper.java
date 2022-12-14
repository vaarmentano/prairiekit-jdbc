package org.prairiekit.persist.jdbc.mapping;

import org.prairiekit.domain.definition.FieldDefinition;
import org.prairiekit.domain.definition.UdoDefinition;
import org.prairiekit.persist.jdbc.FieldDefinitionEntity;
import org.prairiekit.persist.jdbc.UdoDefinitionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefinitionEntityMapper {
    public UdoDefinitionEntity mapDefinitionToEntity(UdoDefinition def) {
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

    public UdoDefinition mapDefinitionFromEntity(UdoDefinitionEntity definitionEntity) {
        return new UdoDefinition(
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
