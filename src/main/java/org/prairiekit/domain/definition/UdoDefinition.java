package org.prairiekit.domain.definition;

import java.util.Collections;
import java.util.List;

public record UdoDefinition(
        String name,
        List<FieldDefinition> fieldDefinitions) {

    @Override
    public List<FieldDefinition> fieldDefinitions() {
        return Collections.unmodifiableList(fieldDefinitions);
    }

}
