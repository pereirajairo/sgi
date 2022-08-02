package br.com.sgi.interfaces;

import br.com.sgi.bean.NotaFiscal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface InterfaceNotaFiscalDAO<T> {

    boolean alterarNotaMinuta(T t, int qtdreg, int contador) throws SQLException;

    List<NotaFiscal> getNotaFiscais(String PESQUISA_POR, String PESQUISA) throws SQLException;

    NotaFiscal getNotaFiscal(String PESQUISA_POR, String PESQUISA) throws SQLException;

    NotaFiscal getNotaFiscalCarteira(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException;

    NotaFiscal getNotaFiscalCte(String PESQUISA_POR, String PESQUISA, Date DATA) throws SQLException;

}
