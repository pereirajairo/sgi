package br.com.sgi.interfaces;

import br.com.sgi.bean.CondicaoPagamento;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceCondicaoPagamentoDAO<T> {

      
    List<CondicaoPagamento> getCondicaoPagamentos(String PESQUISA_POR, String PESQUISA) throws SQLException;

    CondicaoPagamento getCondicaoPagamento(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
