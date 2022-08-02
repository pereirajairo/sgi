/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Contas;
import br.com.sgi.bean.Motivo;
import br.com.sgi.bean.RamoAtividade;
import br.com.sgi.bean.Representante;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceContasDAO;
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
public class ContasDAO implements InterfaceContasDAO<Contas> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(Contas t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "delete from   usu_t085cli \n"
                + " where  usu_codcli=?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getUsu_codcli());
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Contas excluida com sucesso",
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
    public boolean alterar(Contas t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update  usu_t085cli set \n"
                + "usu_segatu=?,usu_nomcli=?,usu_apecli=?,usu_cgccpf=?,usu_endcli=?, \n"
                + "usu_cidcli=?,usu_baicli=?,usu_concli=?,usu_sigufs=?,usu_telcli=?, \n"
                + "usu_emacli=?,usu_tipcon=?,usu_obscli=?,usu_sitcon=?,usu_coderp=?, \n"
                + "usu_codusu=?,usu_datcad=?,usu_motobs=?,usu_horcad=?,usu_solobs=?, \n"
                + "usu_codram=?,usu_numend=?,usu_codven=?,usu_codrep=?,usu_emaenv=?, \n"
                + "usu_emapar=?,usu_datdis=?,usu_insest=? \n"
                + " where  usu_codcli=?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Contas alterado com sucesso",
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
    public boolean inserir(Contas t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_t085cli \n"
                + "(usu_segatu,usu_nomcli,usu_apecli,usu_cgccpf,usu_endcli, \n"
                + "usu_cidcli,usu_baicli,usu_concli,usu_sigufs,usu_telcli, \n"
                + "usu_emacli,usu_tipcon,usu_obscli,usu_sitcon,usu_coderp, \n"
                + "usu_codusu,usu_datcad,usu_motobs,usu_horcad,usu_solobs, \n"
                + "usu_codram,usu_numend,usu_codven,usu_codrep,usu_emaenv, \n"
                + "usu_emapar,usu_datdis,usu_insest,usu_codcli) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,"
                + " ?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Contas registrado com sucesso",
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
    public List<Contas> getContas(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Contas> resultado = new ArrayList<Contas>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select cli.*,\n"
                + "       upper(mot.desmot) desmot,\n"
                + "       upper(ram.desram) desram,\n"
                + "       upper(rep.nomrep) nomrep,\n"
                + "       upper(rep.nomrep) nomven,\n"
                + "       upper(rep.intnet) intnet\n"
                + "  from usu_t085cli cli\n"
                + "  left join e021mot mot\n"
                + "    on (mot.codmot = cli.usu_motobs)\n"
                + "  left join e026ram ram\n"
                + "    on (ram.codram = cli.usu_codram)\n"
                + "  left join e090rep rep\n"
                + "    on (rep.codrep = cli.usu_codven)\n"
                + " where 1 = 1\n";
        sqlSelect += PESQUISA;

        sqlSelect += " order by  cli.usu_codven";

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
    public Contas getConta(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = "select cli.*,\n"
                + "       upper(mot.desmot) desmot,\n"
                + "       upper(ram.desram) desram,\n"
                + "       upper(rep.nomrep) nomrep,\n"
                + "       upper(rep.nomrep) nomven,\n"
                + "       upper(rep.intnet) intnet\n"
                + "  from usu_t085cli cli\n"
                + "  left join e021mot mot\n"
                + "    on (mot.codmot = cli.usu_motobs)\n"
                + "  left join e026ram ram\n"
                + "    on (ram.codram = cli.usu_codram)\n"
                + "  left join e090rep rep\n"
                + "    on (rep.codrep = cli.usu_codven)\n"
                + " where 1 = 1";

        sqlSelect += PESQUISA;
        sqlSelect += " order by usu_codcli desc";
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<Contas> resultado = new ArrayList<Contas>();
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

    private void setPreparedStatement(Contas t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        //usu_segatu,usu_nomcli,usu_apecli,usu_cgccpf,usu_endcli
        pst.setString(1, t.getUsu_segatu());
        pst.setString(2, t.getUsu_nomcli());
        pst.setString(3, t.getUsu_apecli());
        pst.setString(4, t.getUsu_cgccpf());
        pst.setString(5, t.getUsu_endcli());

        //usu_cidcli,usu_baicli,usu_concli,usu_sigufs,usu_telcli
        pst.setString(6, t.getUsu_cidcli());
        pst.setString(7, t.getUsu_baicli());
        pst.setString(8, t.getUsu_concli());
        pst.setString(9, t.getUsu_sigufs());
        pst.setString(10, t.getUsu_telcli());

        //usu_emacli,usu_tipcon,usu_obscli,usu_sitcon,usu_coderp
        pst.setString(11, t.getUsu_emacli());
        pst.setString(12, t.getUsu_tipcon());
        pst.setString(13, t.getUsu_obscli());
        pst.setString(14, t.getUsu_sitcon());
        pst.setInt(15, t.getUsu_coderp());

        //usu_codusu,usu_datcad,usu_motobs,usu_horcad,usu_solobs
        pst.setInt(16, t.getUsu_codusu());
        pst.setDate(17, new Date(t.getUsu_datcad().getTime()));
        pst.setInt(18, t.getUsu_motobs());
        pst.setInt(19, t.getUsu_horcad());
        pst.setString(20, t.getUsu_solobs());

        //usu_codram,usu_numend,usu_codven,usu_codrep,usu_emaenv
        pst.setString(21, t.getUsu_codram());
        pst.setString(22, t.getUsu_numend());
        pst.setInt(23, t.getUsu_codven());
        pst.setInt(24, t.getUsu_codrep());
        pst.setString(25, t.getUsu_envema());

        //usu_emapar,usu_datdis, usu_insest, usu_codcli
        pst.setString(26, t.getUsu_emapar());
        if (t.getUsu_datdis() != null) {
            pst.setDate(27, new Date(t.getUsu_datdis().getTime()));
        } else {
            pst.setDate(27, null);
        }
        pst.setString(28, t.getUsu_insest());
        pst.setInt(29, t.getUsu_codcli());

    }

    private List<Contas> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Contas> resultado = new ArrayList<Contas>();
        this.utilDatas = new UtilDatas();

        while (rs.next()) {
            Contas a = new Contas();

            a.setUsu_codcli(rs.getInt("usu_codcli"));
            a.setUsu_nomcli(rs.getString("usu_nomcli"));
            a.setUsu_apecli(rs.getString("usu_apecli"));
            a.setUsu_cgccpf(rs.getString("usu_cgccpf"));
            a.setUsu_endcli(rs.getString("usu_endcli"));

            a.setUsu_cidcli(rs.getString("usu_cidcli"));
            a.setUsu_baicli(rs.getString("usu_baicli"));
            a.setUsu_concli(rs.getString("usu_concli"));
            a.setUsu_sigufs(rs.getString("usu_sigufs"));
            a.setUsu_telcli(rs.getString("usu_telcli"));

            a.setUsu_emacli(rs.getString("usu_emacli"));
            a.setUsu_tipcon(rs.getString("usu_tipcon"));
            a.setUsu_obscli(rs.getString("usu_obscli"));
            a.setUsu_sitcon(rs.getString("usu_sitcon"));
            a.setUsu_coderp(rs.getInt("usu_coderp"));

            a.setUsu_codusu(rs.getInt("usu_codusu"));
            a.setUsu_datcad(rs.getDate("usu_datcad"));
            a.setUsu_datcadS(this.utilDatas.converterDateToStr(a.getUsu_datcad()));
            a.setUsu_motobs(rs.getInt("usu_motobs"));
            a.setUsu_horcad(rs.getInt("usu_horcad"));
            a.setUsu_solobs(rs.getString("usu_solobs"));

            a.setUsu_codram(rs.getString("usu_codram"));

            a.setUsu_codcli(rs.getInt("usu_codcli"));
            a.setUsu_numend(rs.getString("usu_numend"));
            a.setUsu_segatu(rs.getString("usu_segatu"));

            a.setUsu_codven(rs.getInt("usu_codven"));
            a.setUsu_codrep(rs.getInt("usu_codrep"));
            a.setUsu_envema(rs.getString("usu_emaenv"));
            a.setUsu_emapar(rs.getString("usu_emapar"));

            a.setUsu_insest(rs.getString("usu_insest"));

            a.setUsu_datdis(rs.getDate("usu_datdis"));
            if (a.getUsu_datdis() != null) {
                a.setUsu_datdisS(this.utilDatas.converterDateToStr(a.getUsu_datdis()));
            }

            Motivo mot = new Motivo();
            mot.setCodigo(a.getUsu_motobs());
            mot.setDescricao(rs.getString("desmot"));
            a.setCadMotivo(mot);
            RamoAtividade ram = new RamoAtividade();
            ram.setCodigo(a.getUsu_codram());
            ram.setDescricao(rs.getString("desram"));
            a.setCadramoAtividade(ram);

            Representante rep = new Representante();
            rep.setCodigo(a.getUsu_codrep());
            rep.setNome(rs.getString("nomrep"));
            rep.setEmail(rs.getString("intnet"));
            a.setCadRepresentante(rep);

            resultado.add(a);

        }
        return resultado;
    }

    public int proxCodCad(Integer cliente) throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codcli) + 1), 1) codcli FROM usu_t085cli";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("codcli");
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
    public boolean gravarSolucao(Contas t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update  usu_codcli set\n"
                + " usu_solobs=?, usu_sitobs='R' \n"
                + " where usu_codcli = ?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, t.getUsu_solobs());

            pst.setInt(2, t.getUsu_codcli());

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Solução registrado com sucesso",
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
    public boolean exportarERP(Contas t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update usu_t085cli set "
                + " usu_nomcli=?,usu_apecli=?,usu_cgccpf=?,usu_endcli=?, \n"
                + " usu_cidcli=?,usu_baicli=?,usu_sigufs=?,usu_numend=?, usu_insest=?,"
                + " usu_experp=? \n"
                + " where  usu_codcli=?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, t.getUsu_nomcli());
            pst.setString(2, t.getUsu_apecli());
            pst.setString(3, t.getUsu_cgccpf());
            pst.setString(4, t.getUsu_endcli());

            pst.setString(5, t.getUsu_cidcli());
            pst.setString(6, t.getUsu_baicli());
            pst.setString(7, t.getUsu_sigufs());
            pst.setString(8, t.getUsu_numend());
            pst.setString(9, t.getUsu_insest());

            pst.setString(10, "S");

            pst.setInt(11, t.getUsu_codcli());

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Contas assinalada para integrar com o ERP",
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
