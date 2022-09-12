package org.varmentano.nocode_plugin.domain.definition;

import java.util.Collections;
import java.util.List;

public record ObjectDefinition(String name, List<FieldDefinition> fieldDefinitions) {

    @Override
    public List<FieldDefinition> fieldDefinitions() {
        return Collections.unmodifiableList(fieldDefinitions);
    }

}
