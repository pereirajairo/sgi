package br.com.sgi.interfaces;

import br.com.sgi.bean.GarantiaItens;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceGarantiaItensDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t, int contador, int registro) throws SQLException;

    List<GarantiaItens> getGarantiaItens(String PESQUISA_POR, String PESQUISA) throws SQLException;

    GarantiaItens getGarantiaItem(String PESQUISA_POR, String PESQUISA) throws SQLException;
}
