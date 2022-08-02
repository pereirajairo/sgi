package br.com.sgi.interfaces;


import br.com.sgi.bean.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceUsuarioErpDAO<T> {

    Usuario getUsuario(String login) throws SQLException;
    
    Usuario getAbrangencia(String login) throws SQLException;

   
}
