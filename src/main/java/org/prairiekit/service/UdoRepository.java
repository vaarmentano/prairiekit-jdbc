package org.prairiekit.service;

import org.prairiekit.domain.UserDefinedObject;

import java.util.Optional;

public interface UdoRepository {
    Optional<UserDefinedObject> findById(int id);

    Iterable<UserDefinedObject> findAll();

    UserDefinedObject saveNew(UserDefinedObject myUdo);

    UserDefinedObject saveUpdate(UserDefinedObject myUdo);

    void deleteById(int id);
}
