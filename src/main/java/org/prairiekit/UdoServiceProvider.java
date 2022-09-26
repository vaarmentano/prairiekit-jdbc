package org.prairiekit;

import org.prairiekit.persist.jdbc.UdoServiceJdbc;
import org.prairiekit.service.UdoService;

import javax.sql.DataSource;

public class UdoServiceProvider {
    public static UdoService getJdbcService(DataSource dataSource) {
        return new UdoServiceJdbc(dataSource);
    }
}
