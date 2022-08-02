/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.CargaRegistro;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.MinutaNota;
import br.com.sgi.bean.MinutaNota;
import br.com.sgi.interfaces.InterfaceMinutaNotaDAO;
import br.com.sgi.bean.MinutaNota;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.ConversaoHoras;
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
public class MinutaNotaDAO implements InterfaceMinutaNotaDAO<MinutaNota> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(MinutaNota e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setInt(1, e.getEmpresa());
        pst.setInt(2, e.getFilial());
        pst.setInt(3, e.getCodigominuta());
        pst.setInt(4, e.getCodigoCliente());
        pst.setInt(5, e.getNotafiscal());

        pst.setDouble(6, e.getPeso());
        pst.setDate(7, new Date(e.getEmissao().getTime()));
        pst.setDate(8, new Date(e.getEmbarque().getTime()));
        pst.setString(9, e.getSituacao());
        pst.setString(10, e.getEnviaremail());
        pst.setInt(11, 0);

        pst.setDouble(12, e.getQuantidade());
        pst.setDouble(13, e.getQuantidadeVolume());
        pst.setString(14, e.getObservacao());

        pst.setInt(15, e.getCodigolancamento());

    }

    private List<MinutaNota> getLista(ResultSet rs) throws SQLException, ParseException {
        List<MinutaNota> resultado = new ArrayList<MinutaNota>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            MinutaNota e = new MinutaNota();
            e.setEmpresa(rs.getInt("usu_codemp"));
            e.setFilial(rs.getInt("usu_codfil"));
            e.setCodigolancamento(rs.getInt("usu_codlan"));
            e.setCodigoCliente(rs.getInt("usu_codcli"));
            e.setCodigominuta(rs.getInt("usu_codmin"));
            e.setNotafiscal(rs.getInt("usu_numnfv"));
            e.setEmissao(rs.getDate("usu_datemi"));
            e.setEmbarque(rs.getDate("usu_datemi"));
            e.setEnviaremail(rs.getString("usu_envema"));
            e.setPeso(rs.getDouble("usu_pesliq"));
            e.setSituacao(rs.getString("usu_sitmin"));
            e.setUsuario(rs.getInt("usu_codusu"));
            e.setQuantidade(rs.getDouble("usu_qtdfat"));
            e.setQuantidadeVolume(rs.getDouble("usu_qtdvol"));
            e.setObservacao(rs.getString("usu_obsmin"));
            Cliente cli = new Cliente();
            cli.setCodigo(e.getCodigoCliente());
            cli.setNome(rs.getString("nomcli"));
            cli.setCidade(rs.getString("cidcli"));
            cli.setEstado(rs.getString("sigufs"));
            e.setCadCliente(cli);
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(MinutaNota t) throws SQLException {
        PreparedStatement pst = null;

        try {
            openConnectionMySql();
            pst = con.prepareStatement("UPDATE usuario  SET situacao='Inativo' "
                    + "WHERE  id = ? ");

            pst.setInt(1, t.getCodigolancamento());
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {

            return false;
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
    public boolean alterar(MinutaNota t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE usu_tintminnot SET \n"
                    + "usu_codemp=?, \n"
                    + "usu_codfil=?, \n"
                    + "usu_nrocar=?, \n"
                    + "usu_codtra=?, \n"
                    + "usu_pesliq=?, \n"
                    + "usu_datemi=?, \n"
                    + "usu_datemb=?, \n"
                    + "usu_sitmin=?, \n"
                    + "usu_envema=?, \n"
                    + "usu_codusu=?, \n"
                    + "usu_qtdfat=?,  \n"
                    + "usu_qtdvol=?, \n"
                    + "usu_obsmin=? \n"
                    + " where usu_codlan=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "A");
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Registro alterado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
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
    public boolean inserir(MinutaNota t, Integer qtdreg, Integer contador) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_tintminnot(\n"
                + "usu_codemp, \n"
                + "usu_codfil, \n"
                + "usu_codmin, \n"
                + "usu_codcli, \n"
                + "usu_numnfv, \n"
                + "usu_pesliq, \n"
                + "usu_datemi, \n"
                + "usu_datemb, \n"
                + "usu_sitmin, \n"
                + "usu_envema, \n"
                + "usu_codusu, \n"
                + "usu_qtdfat,  \n"
                + "usu_qtdvol, \n"
                + "usu_obsmin, \n "
                + "usu_codlan) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            if (qtdreg == contador) {
                JOptionPane.showMessageDialog(null, "Registro notas inserido com sucesso",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);

            }

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO" + ex,
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<MinutaNota> getMinutaNotas(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<MinutaNota> resultado = new ArrayList<MinutaNota>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "Select min.*, cli.nomcli, cli.nomcli, cli.cidcli, cli.sigufs\n"
                + "  from usu_tintminnot min, e085cli cli\n"
                + " where min.usu_codemp > 0\n"
                + "   and min.usu_codcli = cli.codcli ";
        sqlSelect += PESQUISA;

        sqlSelect += "order by min.usu_codlan desc";

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
    public MinutaNota getMinutaNota(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<MinutaNota> resultado = new ArrayList<MinutaNota>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "Select min.*, "
                + "  tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra\n"
                + "  from usu_tintminnot min\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = min.usu_codtra)\n"
                + " where min.usu_codemp > 0 \n ";
        sqlSelect += PESQUISA;

        sqlSelect += "order by min.usu_codlan desc";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

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

    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) PROX_CODALAN FROM usu_tintminnot";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("PROX_CODALAN");
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

}
