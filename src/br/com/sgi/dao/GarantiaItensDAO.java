/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.GarantiaItens;
import br.com.sgi.bean.Produto;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceGarantiaItensDAO;
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
public class GarantiaItensDAO implements InterfaceGarantiaItensDAO<GarantiaItens> {

    private Connection con;
    private UtilDatas utilDatas;
    private final String PREFIXO = "usu_";

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(GarantiaItens t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "delete  usu_tcte \n"
                + "where  usu_codlan=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            //  pst.setInt(1, t.getUsu_codlan());
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: GarantiaItens alterado com sucesso",
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
    public boolean alterar(GarantiaItens t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = " update usu_t440ipc set"
                + " usu_sitgar = ?, "
                + " usu_obsipc = upper(?)"
                + " where usu_empdes = ? "
                + " and usu_numnfc = ?"
                + " and usu_seqite = ?"
                + " and usu_codfil = ? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, t.getUsu_sitgar());
            pst.setString(2, t.getUsu_obsipc());

            pst.setInt(3, t.getUsu_empdes());           
            pst.setInt(4, t.getUsu_numnfc());
            pst.setInt(5, t.getUsu_seqite());
            pst.setInt(6, t.getUsu_codfil());

            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: GarantiaItens alterado com sucesso",
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
    public boolean inserir(GarantiaItens t, int contador, int registro) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_t440ipc\n"
                + "(usu_codpro,usu_codemp,usu_excarq,usu_numsep,usu_itepro, \n"
                + "usu_preuni,usu_codder,usu_codusu,usu_codmot,usu_codsnf, \n"
                + "usu_forgar,usu_numocr,usu_insaut,usu_motant,usu_sitgar, \n"
                + "usu_tmpgar,usu_datemi,usu_datlim,usu_tipnfc,usu_codcli, \n"
                + "usu_tipnfs,usu_numnfv,usu_desprb,usu_dessol,usu_tmpfat, \n"
                + "usu_tmptot,usu_empemi,usu_obsipc,usu_certif,usu_datcer, \n"
                + "usu_datrom,usu_numoat,usu_numrom,usu_itediv,usu_codfor, \n"
                + "usu_empdes,usu_numnfc,usu_seqite,usu_codfil)\n"
                + "values\n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            if (contador == registro) {
                JOptionPane.showMessageDialog(null, "SUCESSO: GarantiaItens registrado com sucesso",
                        "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }

            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERRO " + ex + "\n\r " + t.getUsu_numsep(),
                    "Atenção: ", JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<GarantiaItens> getGarantiaItens(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<GarantiaItens> resultado = new ArrayList<GarantiaItens>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select ipc.*, pro.despro, cli.nomcli\n"
                + "  from usu_t440ipc ipc\n"
                + "  left join e075pro pro\n"
                + "    on pro.codemp = ipc.usu_empdes\n"
                + "   and pro.codpro = ipc.usu_codpro\n"
                + "  left join e085cli cli\n"
                + "    on cli.codcli = ipc.usu_codcli\n"
                + " where 0 = 0 \n";
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
    public GarantiaItens getGarantiaItem(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;
        ResultSet rs = null;

        String sqlSelect = "select usu_t440nfc.*, fo.codfor, fo.nomfor, fo.cgccpf\n"
                + "  from usu_t440nfc\n"
                + "  left join e095for fo\n"
                + "    on (fo.codfor = usu_t440nfc.usu_codfor)\n"
                + " where 0 = 0  \n";
        sqlSelect += PESQUISA;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<GarantiaItens> resultado = new ArrayList<GarantiaItens>();
            resultado = getLista(rs);
            if (resultado.size() > 0) {
                return resultado.get(0);
            } else {

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

    private void setPreparedStatement(GarantiaItens t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        //usu_codpro,usu_codemp,usu_excarq,usu_numsep,usu_itepro, 
        pst.setString(1, t.getUsu_codpro());
        pst.setInt(2, t.getUsu_codemp());
        pst.setString(3, t.getUsu_excarq());
        pst.setString(4, t.getUsu_numsep());
        pst.setString(5, t.getUsu_itepro());

        //usu_preuni,usu_codder,usu_codusu,usu_codmot,usu_codsnf 
        pst.setDouble(6, t.getUsu_preuni());
        pst.setString(7, t.getUsu_codder());
        pst.setInt(8, t.getUsu_codusu());
        pst.setInt(9, t.getUsu_codmot());
        pst.setString(10, t.getUsu_codsnf());
        //usu_forgar,usu_numocr,usu_insaut,usu_motant,usu_sitgar 
        pst.setString(11, t.getUsu_forgar());
        pst.setInt(12, t.getUsu_numocr());
        pst.setString(13, t.getUsu_insaut());
        pst.setInt(14, t.getUsu_motant());
        pst.setString(15, t.getUsu_sitgar());
        //usu_tmpgar,usu_datemi,usu_datlim,usu_tipnfc,usu_codcli,
        pst.setString(16, t.getUsu_tmpgar());
        pst.setDate(17, new Date(t.getUsu_datemi().getTime()));
        pst.setDate(18, new Date(t.getUsu_datlim().getTime()));
        pst.setString(19, t.getUsu_tipnfc());
        pst.setInt(20, t.getUsu_codcli());
        //usu_tipnfs,usu_numnfv,usu_desprb,usu_dessol,usu_tmpfat, 
        pst.setString(21, t.getUsu_tipnfs());
        pst.setInt(22, t.getUsu_numnfv());
        pst.setString(23, t.getUsu_desprb());
        pst.setString(24, t.getUsu_dessol());
        pst.setString(25, t.getUsu_tmpfat());
        //usu_tmptot,usu_empemi,usu_obsipc,usu_certif,usu_datcer, 
        pst.setString(26, t.getUsu_tmptot());
        pst.setInt(27, t.getUsu_empemi());
        pst.setString(28, t.getUsu_obsipc());
        pst.setInt(29, t.getUsu_certif());
        if (t.getUsu_datcer() == null) {
            pst.setDate(30, null);
        } else {
            pst.setDate(30, new Date(t.getUsu_datcer().getTime()));
        }

        //usu_datrom,usu_numoat,usu_numrom,usu_itediv, 
        if (t.getUsu_datrom() == null) {
            pst.setDate(31, null);
        } else {
            pst.setDate(31, new Date(t.getUsu_datrom().getTime()));
        }

        pst.setInt(32, t.getUsu_numoat());
        pst.setInt(33, t.getUsu_numrom());
        pst.setString(34, t.getUsu_itediv());

        //usu_codfor, usu_empdes, usu_numnfc,,usu_seqite,usu_codfil
        pst.setInt(35, t.getUsu_codfor());

        //Chave
        pst.setInt(36, t.getUsu_empdes());
        pst.setInt(37, t.getUsu_numnfc());
        pst.setInt(38, t.getUsu_seqite());
        pst.setInt(39, t.getUsu_codfil());

    }

    private List<GarantiaItens> getLista(ResultSet rs) throws SQLException, ParseException {
        List<GarantiaItens> resultado = new ArrayList<GarantiaItens>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            GarantiaItens a = new GarantiaItens();
            a.setUsu_codfil(rs.getInt(PREFIXO + "codfil"));
            a.setUsu_codfor(rs.getInt(PREFIXO + "codfor"));
            a.setUsu_codpro(rs.getString(PREFIXO + "codpro"));
            a.setUsu_codder(rs.getString(PREFIXO + "codder"));
            a.setUsu_codsnf(rs.getString(PREFIXO + "codsnf"));

            a.setUsu_numnfv(rs.getInt(PREFIXO + "numnfv"));

            a.setUsu_empdes(rs.getInt(PREFIXO + "empdes"));
            a.setUsu_numnfc(rs.getInt(PREFIXO + "numnfc"));
            a.setUsu_tipnfc(rs.getString(PREFIXO + "tipnfc"));
            a.setUsu_codcli(rs.getInt(PREFIXO + "codcli"));

            a.setUsu_certif(rs.getInt(PREFIXO + "certif"));
            a.setUsu_codemp(rs.getInt(PREFIXO + "codemp"));
            a.setUsu_codmot(rs.getInt(PREFIXO + "codmot"));

            a.setUsu_codsnf(rs.getString(PREFIXO + "codsnf"));
            a.setUsu_codusu(rs.getInt(PREFIXO + "codusu"));
            a.setUsu_datcer(rs.getDate(PREFIXO + "datcer"));
            a.setUsu_datemi(rs.getDate(PREFIXO + "datemi"));
            a.setUsu_datlim(rs.getDate(PREFIXO + "datlim"));
            a.setUsu_datrom(rs.getDate(PREFIXO + "datrom"));

            a.setUsu_desprb(rs.getString(PREFIXO + "desprb"));
            a.setUsu_dessol(rs.getString(PREFIXO + "dessol"));
            a.setUsu_empemi(rs.getInt(PREFIXO + "empemi"));
            a.setUsu_excarq(rs.getString(PREFIXO + "excarq"));

            a.setUsu_forgar(rs.getString(PREFIXO + "forgar"));
            a.setUsu_insaut(rs.getString(PREFIXO + "insaut"));
            a.setUsu_itediv(rs.getString(PREFIXO + "itediv"));
            a.setUsu_itepro(rs.getString(PREFIXO + "itepro"));

            a.setUsu_motant(rs.getInt(PREFIXO + "motant"));
            a.setUsu_numoat(rs.getInt(PREFIXO + "numoat"));
            a.setUsu_numocr(rs.getInt(PREFIXO + "numocr"));
            a.setUsu_numrom(rs.getInt(PREFIXO + "numrom"));
            a.setUsu_numsep(rs.getString(PREFIXO + "numsep"));

            a.setUsu_obsipc(rs.getString(PREFIXO + "obsipc"));
            a.setUsu_preuni(rs.getDouble(PREFIXO + "preuni"));
            a.setUsu_seqite(rs.getInt(PREFIXO + "seqite"));
            a.setUsu_sitgar(rs.getString(PREFIXO + "sitgar"));
            a.setUsu_tipnfc(rs.getString(PREFIXO + "tipnfc"));
            a.setUsu_tipnfs(rs.getString(PREFIXO + "tipnfs"));
            a.setUsu_tmpfat(rs.getString(PREFIXO + "tmpfat"));
            a.setUsu_tmpgar(rs.getString(PREFIXO + "tmpgar"));
            a.setUsu_tmptot(rs.getString(PREFIXO + "tmptot"));

            a.setCadProduto(new Produto(a.getUsu_codemp(), a.getUsu_codpro(), rs.getString("despro")));
            a.setCadCliente(new Cliente(a.getUsu_codcli(), rs.getString("nomcli"), ""));

            resultado.add(a);

        }
        return resultado;
    }

    public int proxCodCad(Integer cliente) throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_codlan) + 1), 1) codlan FROM usu_tcte ";

        Integer codlct = 0;
        try {

            ConnectionOracleSap();

            st = con.createStatement();

            ResultSet rs = st.executeQuery(strSql);

            if (rs.next()) {
                codlct = rs.getInt("codlan");
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
