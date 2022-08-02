package br.com.sgi.interfaces;

import br.com.sgi.bean.Permissao;
import br.com.sgi.bean.VinculoGrupo;
import java.sql.SQLException;
import java.util.List;

public interface InterfaceVinculoGrupoDAO<T> {

    boolean remover(T t) throws SQLException;

    boolean alterar(T t) throws SQLException;

    boolean inserir(T t) throws SQLException;

    List<VinculoGrupo> getVinculoGrupos(String PESQUISA_POR, String PESQUISA) throws SQLException;

    VinculoGrupo getVinculoGrupo(String PESQUISA_POR, String PESQUISA) throws SQLException;
    
    List<Permissao> getPermissaoAcesso(String PESQUISA_POR, String PESQUISA) throws SQLException;
    
}
