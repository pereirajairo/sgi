package br.com.sgi.interfaces;

import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceSucataDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean gerarOrdem(SucataMovimento t) throws SQLException;

    boolean gerarNota(SucataMovimento t) throws SQLException;

 

    List<Sucata> getSucatasAgrupada(String PESQUISA_POR, String PESQUISA) throws SQLException;

    List<Sucata> getSucatasSituacao(String PESQUISA_POR, String PESQUISA) throws SQLException;

    Sucata getSucata(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
