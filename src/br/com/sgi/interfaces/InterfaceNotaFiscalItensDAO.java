package br.com.sgi.interfaces;


import br.com.sgi.bean.NotaFiscalItens;
import br.com.sgi.bean.SituacaoCliente;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface InterfaceNotaFiscalItensDAO<T> {

    NotaFiscalItens getNotaFiscalItem(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException;

    List<NotaFiscalItens> getNotaFiscalItens(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException;

    List<SituacaoCliente> getSituacoes(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException;

}
