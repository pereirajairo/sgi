package br.com.sgi.interfaces;

import br.com.sgi.bean.CargaAbertura;
import br.com.sgi.bean.CargaRegistro;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceCargaAberturaDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;
    
    boolean gravarEmail(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    boolean atualizarPesoLiquido(T t) throws SQLException;

    boolean atualizarPesoEntrada(T t) throws SQLException;

    boolean atualizarPesoSaída(T t) throws SQLException;

    boolean atualizarPesoDesconto(T t) throws SQLException;

    boolean atualizarPesoEmbalagem(T t) throws SQLException;

    boolean inserirCarga(CargaAbertura t) throws SQLException;

    List<CargaRegistro> getCargasRegistro(String PESQUISA_POR, String PESQUISA) throws SQLException;

    CargaRegistro getCargaRegistro(String PESQUISA_POR, String PESQUISA) throws SQLException;

    int proxCodCad() throws SQLException;
}
