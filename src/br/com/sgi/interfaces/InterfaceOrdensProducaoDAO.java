package br.com.sgi.interfaces;


import br.com.sgi.bean.BalancaLancamento;
import br.com.sgi.bean.OrdensProducao;
import br.com.sgi.bean.Produto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceOrdensProducaoDAO {

    List<OrdensProducao> getOrdensProducaos(String PESQUISA_POR, String PESQUISA) throws SQLException;

    OrdensProducao getOrdensProducao(String PESQUISA_POR, String PESQUISA) throws SQLException;


}
