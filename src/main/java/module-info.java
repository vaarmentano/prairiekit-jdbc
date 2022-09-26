module prairiekit.jdbc {
    requires transitive java.sql;
    // Still need to understand why inspection flags this 'requires' as redundant
    //noinspection Java9RedundantRequiresStatement
    requires java.naming;
    requires org.hibernate.orm.core;

    exports org.prairiekit;
    exports org.prairiekit.domain;
    exports org.prairiekit.domain.definition;
    exports org.prairiekit.service;
}