package br.com.sgi.interfaces;

import br.com.sgi.bean.SucataAnalises;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceSucataAnalisesDAO<T> {

    List<SucataAnalises> getSucataAnalisesCd(String PESQUISA_POR, String PESQUISA) throws SQLException;
    
    List<SucataAnalises> getSucataAnalisess(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<SucataAnalises> getSucataAnalisessMoto(String PESQUISA_POR, String PESQUISA) throws SQLException;

    SucataAnalises getSucataAnalises(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<SucataAnalises> getSucataAnalisesGeral(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<SucataAnalises> getSucataAnalisesEntrada(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
