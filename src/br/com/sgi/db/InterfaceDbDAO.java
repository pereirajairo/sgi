package br.com.sgi.db;

import java.sql.SQLException;

public interface InterfaceDbDAO<T> {


    Params_banco_app getParams_banco_app() throws SQLException;
}
