/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.AtendimentoLigacao;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Motivo;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceAtendimentoLigacaoDAO;
import br.com.sgi.util.ConversaoHoras;
import static br.com.sgi.util.ConversaoHoras.converterMinutosHora;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class AtendimentoLigacaoDAO implements InterfaceAtendimentoLigacaoDAO<AtendimentoLigacao> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(AtendimentoLigacao t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "delete from  usu_t085lig  "
                + "where usu_codlan=? \n"
                + "and usu_codmot=? \n"
                + "and usu_codcli=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setInt(1, t.getCodigolancamento());
            pst.setInt(2, t.getCodigoatendimento());
            pst.setInt(3, t.getCodigocliente());

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Ligacao excluido com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean alterar(AtendimentoLigacao t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update usu_t085lig set "
                + "usu_deslig=? \n"
                + "where usu_codlan=? \n"
                + "and usu_codmot=? \n"
                + "and usu_codcli=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, t.getDescricaoligacao());
            pst.setInt(2, t.getCodigolancamento());
            pst.setInt(3, t.getCodigoatendimento());
            pst.setInt(4, t.getCodigocliente());

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Ligacao alterado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean inserir(AtendimentoLigacao t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = " insert into usu_t085lig\n"
                + "(usu_datlig, \n"
                + "usu_deslig, \n"
                + "usu_codusu, \n"
                + "usu_horlig, \n"
                + "usu_tipcon, \n"
                + "usu_conven, \n"
                + "usu_numped, \n"
                + "usu_datcon, "
                + "usu_codlan, \n"
                + "usu_codmot, \n"
                + "usu_codcli) "
                + " values \n"
                + " (?,?,?,?,?,\n"
                + "  ?,?,?,?,?,\n"
                + "  ? )";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Ligação registrado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<AtendimentoLigacao> getAtendimentoLigacaos(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<AtendimentoLigacao> resultado = new ArrayList<AtendimentoLigacao>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select * from usu_t085lig \n"
                + " where 0=0  ";
        sqlSelect += PESQUISA;

        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs);

            pst.close();
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
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
    public AtendimentoLigacao getAtendimentoLigacao(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = "select * from usu_t085lig \n"
                + "where 0=0  ";

        sqlSelect += PESQUISA;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<AtendimentoLigacao> resultado = new ArrayList<AtendimentoLigacao>();
            resultado = getLista(rs);
            if (resultado.size() > 0) {
                return resultado.get(0);
            }
            pst.close();
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return null;
    }

    private void setPreparedStatement(AtendimentoLigacao t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setDate(1, new Date(t.getDataligacao().getTime()));
        pst.setString(2, t.getDescricaoligacao());
        pst.setInt(3, t.getUsuario());
        pst.setInt(4, t.getHoraligacao());
        pst.setString(5, t.getTipoconta());
        pst.setString(6, t.getConvertido());
        pst.setInt(7, t.getPedido());
        pst.setDate(8, new Date(t.getDataconversao().getTime()));

        pst.setInt(9, t.getCodigolancamento());
        pst.setInt(10, t.getCodigoatendimento());
        pst.setInt(11, t.getCodigocliente());

    }

    private List<AtendimentoLigacao> getLista(ResultSet rs) throws SQLException, ParseException {
        List<AtendimentoLigacao> resultado = new ArrayList<AtendimentoLigacao>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            AtendimentoLigacao a = new AtendimentoLigacao();
            a.setCodigoatendimento(rs.getInt("usu_codmot"));
            a.setCodigolancamento(rs.getInt("usu_codlan"));
            a.setCodigocliente(rs.getInt("usu_codcli"));
            a.setDataligacao(rs.getDate("usu_datlig"));
            a.setDataconversao(rs.getDate("usu_datcon"));
            a.setDescricaoligacao(rs.getString("usu_deslig"));
            a.setUsuario(rs.getInt("usu_codusu"));
            a.setHoraligacao(rs.getInt("usu_horlig"));
            a.setTipoconta(rs.getString("usu_tipcon"));
            a.setConvertido(rs.getString("usu_conven"));
            a.setPedido(rs.getInt("usu_numped"));
            a.setDataligcaoS(this.utilDatas.converterDateToStr(a.getDataligacao()));
            a.setHoraligacaoS(ConversaoHoras.converterMinutosHora(a.getHoraligacao()));

            resultado.add(a);

        }
        return resultado;
    }

    public int proxCodCad(Integer cliente) throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) seqobs FROM usu_t085lig";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("seqobs");
            }

            return codlct;
        } catch (SQLException e) {

            return -1;
        } catch (Exception ex) {

            return -1;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

    public int proxCodCadLead(Integer cliente) throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) seqobs FROM usu_t085lig";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("seqobs");
            }

            return codlct;
        } catch (SQLException e) {

            return -1;
        } catch (Exception ex) {

            return -1;
        } finally {
            if (st != null) {
                st.close();
            }
            con.close();
        }
    }

    @Override
    public boolean deletar(Integer id, String tipocaonta) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "delete  usu_t085lig \n"
                + "where usu_codcli=? \n"
                + "and usu_tipcon=?  ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setInt(1, id);
            pst.setString(2, tipocaonta);

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Ligacões deletadas com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

}
