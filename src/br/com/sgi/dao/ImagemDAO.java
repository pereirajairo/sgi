/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Imagem;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.interfaces.InterfaceImagemDAO;
import br.com.sgi.util.Mensagem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

/**
 *
 * @author jairosilva
 */
public class ImagemDAO implements InterfaceImagemDAO<Imagem> {

    private Connection con;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void setPreparedStatement(Imagem i,
            java.sql.PreparedStatement pst) throws SQLException {

        pst.setString(1, i.getTipoImagem());
        pst.setString(2, i.getNomeImagem());
        pst.setTimestamp(3, new java.sql.Timestamp(i.getDataInclusao().getTime()));
        pst.setBlob(4, new SerialBlob(i.getFileBlob()));

        pst.setString(5, i.getProcesso());
        pst.setInt(6, i.getRegistro_id());
        pst.setInt(7, i.getSequencia());
        pst.setString(8, i.getDescricao());

        pst.setInt(9, i.getId()); // iD - cHAVE
    }

    private List<Imagem> getListaImagens(ResultSet rs) throws SQLException {
        List<Imagem> resultado = new ArrayList<>();

        while (rs.next()) {
            Imagem e = new Imagem();
            e.setId(rs.getInt("id"));
            e.setSequencia(rs.getInt("sequencia"));
            e.setTipoImagem(rs.getString("tipo_imagem"));
            e.setNomeImagem(rs.getString("nome_imagem"));
            e.setDataInclusao(rs.getTimestamp("data_inclusao"));
            e.setFileBlob(rs.getBytes("file"));
            e.setRegistro_id(rs.getInt("registro_id"));
            e.setProcesso(rs.getString("processo"));
            e.setDescricao(rs.getString("descricao"));
            e.setCaminho(rs.getString("caminho"));
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(Imagem t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "DELETE FROM imagem WHERE id = ? ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlExcluir);
            pst.setLong(1, t.getId());
            pst.executeUpdate();
            pst.close();
            retorno = true;

        } catch (Exception ex) {

            retorno = false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return retorno;

    }

    @Override
    public boolean alterar(Imagem t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "UPDATE  imagem SET  \n"
                + "	tipo_imagem=?, nome_imagem=?, data_inclusao=?, file=?, processo=upper(?), registro_id=?, sequencia=? "
                + "     where id =  ?";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst);
            pst.executeUpdate();
            pst.close();

            return true;
        } catch (Exception ex) {
            Mensagem.mensagem("ERROR", ex.toString());
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
        return true;
    }

    @Override
    public boolean inserir(Imagem t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO imagem \n"
                + "   ( tipo_imagem, nome_imagem, data_inclusao, file, processo, registro_id, sequencia, descricao, id)\n"
                + "	VALUES\n"
                + "	(?,?,?,?,?,?,?,?,?)";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst);
            pst.executeUpdate();
            pst.close();

            return true;
        } catch (Exception ex) {

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<Imagem> getImagens(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Imagem> resultado = new ArrayList<>();

        java.sql.PreparedStatement pst = null;

        String sqlSelect = "SELECT ima.id,\n"
                + "       ima.tipo_imagem,\n"
                + "       ima.nome_imagem,\n"
                + "       ima.descricao,\n"
                + "       ima.caminho,\n"
                + "       ima.data_inclusao,\n"
                + "       NULL AS FILE,\n"
                + "       ima.processo,\n"
                + "       ima.registro_id,\n"
                + "       ima.sequencia\n"
                + "  FROM imagem ima, pedidohub hub\n"
                + " WHERE 0 = 0   \n"
                + "   AND hub.id = ima.registro_id \n";
        if (!PESQUISAR_POR.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        System.out.println(" sql imgagem " + sqlSelect);
        ResultSet rs = null;

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();

            resultado = getListaImagens(rs);

            rs.close();
            pst.close();
        } catch (Exception ex) {
            Mensagem.mensagem("ERROR", ex.toString());
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
        return resultado;
    }

    @Override
    public Imagem getImagem(Long id) throws SQLException {
        List<Imagem> resultado = new ArrayList<>();

        java.sql.PreparedStatement pst = null;

        String sqlSelect = "SELECT * FROM imagem where id=" + id + " \n";

        ResultSet rs = null;

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();

            resultado = getListaImagens(rs);
            if (resultado.size() > 0) {
                return resultado.get(0);
            }

            rs.close();
            pst.close();
        } catch (Exception ex) {
            Mensagem.mensagem("ERROR", ex.toString());
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
        return null;

    }

    @Override
    public List<Imagem> getImagensGarantias(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Imagem> resultado = new ArrayList<>();

        java.sql.PreparedStatement pst = null;

        String sqlSelect = "SELECT ima.id,\n"
                + "       ima.tipo_imagem,\n"
                + "       ima.nome_imagem,\n"
                + "       ima.descricao,\n"
                + "       ima.caminho,\n"
                + "       ima.data_inclusao,\n"
                + "       NULL AS FILE,\n"
                + "       ima.processo,\n"
                + "       ima.registro_id,\n"
                + "       ima.sequencia\n"
                + "  FROM imagem ima, garantia gar\n"
                + " WHERE 0 = 0   \n"
                + "   AND gar.id = ima.registro_id\n"
                + "   AND ima.processo = 'GAR'  \n";
        if (!PESQUISAR_POR.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        System.out.println(" sql imgagem " + sqlSelect);
        ResultSet rs = null;

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();

            resultado = getListaImagens(rs);

            rs.close();
            pst.close();
        } catch (Exception ex) {
            Mensagem.mensagem("ERROR", ex.toString());
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
        return resultado;
    }

}
