package br.com.sgi.interfaces;

import br.com.sgi.bean.CameraParametro;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceCameraParametroDAO<T> {

    List<CameraParametro> getCameraParametros(String PESQUISA_POR, String PESQUISA) throws SQLException;
  
    CameraParametro getCameraParametro() throws SQLException;
    
}
