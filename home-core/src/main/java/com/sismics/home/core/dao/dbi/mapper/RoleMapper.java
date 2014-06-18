package com.sismics.home.core.dao.dbi.mapper;

import com.sismics.home.core.model.dbi.Role;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Role result set mapper.
 *
 * @author jtremeaux
 */
public class RoleMapper implements ResultSetMapper<Role> {
    @Override
    public Role map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Role(
                r.getString("ROL_ID_C"),
                r.getString("ROL_NAME_C"),
                r.getDate("ROL_CREATEDATE_D"),
                r.getDate("ROL_DELETEDATE_D"));
    }
}
