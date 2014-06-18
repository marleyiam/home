package com.sismics.home.core.dao.dbi.mapper;

import com.sismics.home.core.model.dbi.RoleBaseFunction;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Album result set mapper.
 *
 * @author jtremeaux
 */
public class RoleBaseFunctionMapper implements ResultSetMapper<RoleBaseFunction> {
    @Override
    public RoleBaseFunction map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new RoleBaseFunction(
                r.getString("RBF_ID_C"),
                r.getString("RBF_IDROLE_C"),
                r.getString("RBF_IDBASEFUNCTION_C"),
                r.getTimestamp("RBF_CREATEDATE_D"),
                r.getTimestamp("RBF_DELETEDATE_D"));
    }
}
