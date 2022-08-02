package br.com.sgi.interfaces;

import br.com.sgi.bean.NotaEntrada;
import br.com.sgi.bean.SucataMovimento;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceSucataMovimentoDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean gerarOrdem(SucataMovimento t) throws SQLException;

    boolean gerarNota(SucataMovimento t) throws SQLException;

    boolean atualizarNota(SucataMovimento t) throws SQLException;

    boolean atualizarOrdemCompra(SucataMovimento t) throws SQLException;

    List<SucataMovimento> getSucatasMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<SucataMovimento> getSucatasMovimentoOcp(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<SucataMovimento> getContaCorrentesInd(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<SucataMovimento> getContaCorrentesAuto(String PESQUISA_POR, String PESQUISA) throws SQLException;

    SucataMovimento getContaCorrente(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<SucataMovimento> getSucatasMovimentoAgrupado(String PESQUISA_POR, String PESQUISA) throws SQLException;

    SucataMovimento getSucataMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException;

    NotaEntrada notaEntrada(String PESQUISA_POR, String PESQUISA) throws SQLException;

}
