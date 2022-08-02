/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.interfaces.InterfaceSucataDAO;
import br.com.sgi.bean.Sucata;
import br.com.sgi.bean.SucataMovimento;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.FormatarPeso;
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
public class SucataDAO implements InterfaceSucataDAO<Sucata> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(Sucata e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setInt(1, e.getEmpresa());
        pst.setInt(2, e.getFilial());
        pst.setInt(3, e.getCliente());
        pst.setInt(4, e.getPedido());
        pst.setDate(5, new Date(e.getDatageracao().getTime()));
        pst.setDate(6, new Date(e.getDatamovimento().getTime()));
        pst.setString(7, e.getAutomoto());
        pst.setString(8, e.getDebitocredito());
        pst.setDouble(9, e.getPesosucata());
        pst.setInt(10, e.getUsuario());

        pst.setString(11, "SUCATA "+e.getAutomoto());
        pst.setString(12, "");
        pst.setString(13, e.getObservacaomovimento());
        pst.setDouble(14, e.getNotasaida());
        pst.setInt(15, e.getOrdemcompra());
        pst.setString(16, "");
        pst.setString(17, e.getEnviaremail());
        pst.setString(18, e.getEmail());
        pst.setDouble(19, e.getPesoordemcompra());
        pst.setDouble(20, e.getPercentualrendimento());

        pst.setDouble(21, e.getPesorecebido());
        pst.setDouble(22, e.getPesosaldo());
        pst.setString(23, e.getSituacao());
        pst.setInt(24, e.getCodigopeso());
        pst.setInt(25, e.getCodigominuta());
        pst.setString(26, e.getProduto());
        pst.setString(27, e.getSucata());
        pst.setString(28, "");
        pst.setDouble(29, e.getPesopedido());
        pst.setInt(30, e.getCodigolancamento());

    }

    private List<Sucata> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Sucata> resultado = new ArrayList<Sucata>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Sucata e = new Sucata();
            e.setEmpresa(rs.getInt("usu_codemp"));
            e.setFilial(rs.getInt("usu_codfil"));
            e.setPesosucata(rs.getDouble("usu_pessuc"));
            e.setPesosucataS(FormatarPeso.mascaraPorcentagem(e.getPesosucata(), FormatarPeso.PORCENTAGEM));
            e.setDebitocredito(rs.getString("usu_debcre"));
            e.setMes(rs.getInt("mes"));
            e.setAno(rs.getInt("ano"));
            e.setCliente(rs.getInt("usu_codcli"));
            e.setDatageracao(rs.getDate("usu_datger"));
            e.setDatageracao(rs.getDate("usu_datalt"));
            e.setCodigolancamento(rs.getInt("usu_codlan"));
            e.setPedido(rs.getInt("usu_numped"));
            e.setAutomoto(rs.getString("usu_autmot"));
            e.setDebitocredito(rs.getString("usu_debcre"));
            e.setUsuario(rs.getInt("usu_codusu"));
            Cliente cli = new Cliente();
            cli.setCodigo(e.getCliente());
            cli.setNome(rs.getString("nomcli"));
            e.setCaCliente(cli);

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(Sucata t) throws SQLException {
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
    public boolean alterar(Sucata t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE usu_sucmov SET \n"
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
    public boolean inserir(Sucata t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_tsuccab(\n"
                + "usu_codemp, \n"
                + "usu_codfil, \n"
                + "usu_codcli, \n"
                + "usu_numped, \n"
                + "usu_datger, \n"
                + "usu_datalt, \n"
                + "usu_autmot, \n"
                + "usu_debcre, \n"
                + "usu_pesuc, \n"
                + "usu_codusu, \n"
                + "usu_descon, \n"
                + "usu_tnspro, \n"
                + "usu_obssuc, \n"
                + "usu_numnfv, \n"
                + "usu_numocp, \n"
                + "usu_gerocp, \n"
                + "usu_envema, \n"
                + "usu_emapar, \n"
                + "usu_pesord, \n"
                + "usu_perren, \n"
                + "usu_pesrec, \n"
                + "usu_pessal, \n"
                + "usu_sitsuc, \n"
                + "usu_codpes, \n"
                + "usu_codmin, \n"
                + "usu_codpro, \n"
                + "usu_codsuc, \n"
                + "usu_sitped, \n"
                + "usu_pesped,"
                + "usu_codlan) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?\n)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
//
//            JOptionPane.showMessageDialog(null, "Registro de carga inserido com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

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
    public List<Sucata> getSucatasAgrupada(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Sucata> resultado = new ArrayList<Sucata>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(to_char(suc.usu_datger, 'yyyy')) ano,\n"
                + "       max(to_char(suc.usu_datger, 'mm')) mes,\n"
                + "       max(usu_codemp) usu_codemp,\n"
                + "       max(usu_codfil) usu_codfil,\n"
                + "       max(usu_codcli) usu_codcli,\n"
                + "       max(usu_codlan) usu_codlan,\n"
                + "       max(usu_datger) usu_datger,\n"
                + "       max(usu_datalt) usu_datalt,\n"
                + "       max(usu_numped) usu_numped,\n"
                + "       max(usu_debcre) usu_debcre,\n"
                + "       max(usu_autmot) usu_autmot,\n"
                + "       max(usu_codusu) usu_codusu,\n"
                + "       sum(usu_pesuc) usu_pessuc,\n"
                + "       max(cli.nomcli) nomcli,\n"
                + "       max(gre.nomgre) nomgre\n"
                + "  from usu_tsuccab suc, e085cli cli\n"
                + "  left join e069gre gre\n"
                + "    on (gre.codgre = cli.codgre)\n"
                + " where suc.usu_codemp > 0\n"
                + "   and suc.usu_codcli = cli.codcli \n"
                + "  \n";
        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }

        sqlSelect += "\n group by suc.usu_codcli, usu_debcre  \n"
                + "order by suc.usu_codcli asc";

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
    public Sucata getSucata(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Sucata> resultado = new ArrayList<Sucata>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "Select min.*, "
                + "  tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra\n"
                + "  from usu_tintmin min\n"
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
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) PROX_CODALAN FROM usu_tsuccab";

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

    @Override
    public List<Sucata> getSucatasSituacao(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Sucata> resultado = new ArrayList<Sucata>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select CASE (usu_debcre)\n"
                + "          WHEN 'C' THEN\n"
                + "           sum(usu_qtdmov)\n"
                + "          WHEN 'D' THEN\n"
                + "           sum(usu_qtdmov)\n"
                + "          else\n"
                + "            sum(usu_qtdmov)\n"
                + "        END AS peso,\n"
                + "        max(usu_debcre) debitocredito\n"
                + "   from USU_TMovSuc suc, e085cli cli\n"
                + "  where usu_codemp > 0\n"
                + "  and suc.usu_codcli =  cli.codcli ";
        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }

        sqlSelect += " group by usu_debcre";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            while (rs.next()) {
                Sucata e = new Sucata();
                e.setDebitocredito(rs.getString("debitocredito"));
                e.setPesosucata(rs.getDouble("peso"));
                e.setPesosucataS(FormatarPeso.mascaraPorcentagem(e.getPesosucata(), FormatarPeso.PORCENTAGEM));
                resultado.add(e);
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
        return resultado;
    }

    @Override
    public boolean gerarOrdem(SucataMovimento t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE usu_tsuccab SET \n"
                    + " usu_numocp=?,"
                    + " usu_debcre=?,"
                    + " usu_pesuc=?, \n"
                    + " usu_gerocp=?, \n"
                    + " usu_envema=?, \n"
                    + " usu_emapar=? \n"
                    + " where usu_codlan=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, 0);
            pst.setString(2, t.getDebitocredito());
            pst.setDouble(3, t.getPesosucata());
            pst.setString(4, t.getGerarordem());
            pst.setString(5, t.getEnviaremail());
            pst.setString(6, t.getEmail());

            pst.setInt(7, t.getCodigolancamento());

            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "O Sistema irá geração da Ordem de Compra",
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
    public boolean gerarNota(SucataMovimento t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE usu_tsuccab SET \n"
                    + " usu_debcre=?,"
                    + " usu_pesuc=?, \n"
                    + " usu_gerocp=?, \n"
                    + " usu_envema=?, \n"
                    + " usu_emapar=? \n"
                    + " where usu_codlan=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, t.getDebitocredito());
            pst.setDouble(2, t.getPesosucata());
            pst.setString(3, t.getGerarordem());
            pst.setString(4, t.getEnviaremail());
            pst.setString(5, t.getEmail());
            pst.setInt(6, t.getCodigolancamento());

            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "O Sistema irá geração da Ordem de Compra",
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

   

}
