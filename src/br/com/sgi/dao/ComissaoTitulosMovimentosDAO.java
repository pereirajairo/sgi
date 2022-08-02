/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.ComissaoTitulos;
import br.com.sgi.bean.ComissaoTitulosMovimentos;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceComissaoTitulosMovimentosDAO;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class ComissaoTitulosMovimentosDAO implements InterfaceComissaoTitulosMovimentosDAO<ComissaoTitulos> {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public List<ComissaoTitulosMovimentos> getTitulos(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        if (utilDatas == null) {
            this.utilDatas = new UtilDatas();
        }

        String sqlSelect = "SELECT E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC,\n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP, \n"
                + "                E301TCR.NUMPED,E140NFV.NUMNFV,E140NFV.DATEMI, E301MCR.VLRBCO-E301MCR.VLRMOV AS DIFBCO, E301MCR.USU_COMPAG \n"
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E140NFV, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND\n"
                + "                E301TCR.CODREP=E090REP.CODREP AND\n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.NUMNFV<>0 AND \n"
                + "                E301TCR.CODEMP=E140NFV.CODEMP AND E301TCR.CODFIL=E140NFV.CODFIL AND E301TCR.CODSNF=E140NFV.CODSNF AND E301TCR.NUMNFV=E140NFV.NUMNFV AND \n   "
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC,E301TCR.PERCOM,\n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP, \n "
                + "                E301TCR.NUMPED,E140NFV.NUMNFV,E140NFV.DATEMI \n"
                + "           UNION \n"
                + "         SELECT E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC, \n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP, \n"
                + "                0 AS NUMPED,0 AS NUMNFV, NULL AS DATEMI, E301MCR.VLRBCO-E301MCR.VLRMOV AS DIFBCO, E301MCR.USU_COMPAG \n"
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND \n"
                + "                E301TCR.CODREP=E090REP.CODREP AND \n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND  \n"
                + "                E301TCR.NUMNFV=0 AND \n"
                + "                E301MCR.USU_NUMRLC<>'0' AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC,E301TCR.PERCOM,\n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP,0,0,NULL,NULL \n";
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

    public List<ComissaoTitulosMovimentos> getTitulosEmpresa(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT CASE WHEN E301TCR.CODTPT = 'DPG' THEN 99 \n"
                + "                     WHEN E301TCR.USU_EMPCOM=1 AND E301TCR.USU_FILCOM=42 THEN 42 \n"
                + "                     ELSE E301TCR.USU_EMPCOM \n"
                + "                     END AS USU_EMPCOM, \n"
                + "            sum(E301MCR.VLRMOV) AS VLRMOV, sum(E301MCR.VLRBCO) AS VLRBCO, \n"
                + "            sum(E301MCR.VLRCOM) AS VLRCOM,count(E301MCR.NUMTIT) AS QTDTIT, E301TCR.CODTPT \n"
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND\n"
                + "                E301TCR.CODREP=E090REP.CODREP AND\n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301MCR.CODEMP,"
                + "              CASE WHEN E301TCR.CODTPT = 'DPG' THEN 99  \n"
                + "                   WHEN E301TCR.USU_EMPCOM=1 AND E301TCR.USU_FILCOM=42 THEN 42 \n"
                + "                   ELSE E301TCR.USU_EMPCOM \n"
                + "              END, "
                + "              E301TCR.CODTPT \n";
        //sqlSelect += "    ORDER BY E301MCR.CODEMP,decode(E301TCR.CODTPT,'DPG',99,E301TCR.USU_EMPCOM),E301TCR.USU_EMPCOM desc \n";
        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            resultado = getListaEmpresa(rs);
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

    public List<ComissaoTitulosMovimentos> getTitulosEmpresaOC(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E301TCR.CODREP, E090REP.REPFOR AS CODFOR,E095HFO.CODCPG, E028CPG.DESCPG, \n"
                + "                CASE WHEN E301TCR.CODTPT = 'DPG' THEN 99 \n"
                + "                     WHEN E301TCR.USU_EMPCOM=1 AND E301TCR.USU_FILCOM=42 THEN 42 \n"
                + "                     ELSE E301TCR.USU_EMPCOM \n"
                + "                     END AS USU_EMPCOM, \n"
                + "                SUM(E301MCR.VLRCOM) AS VLRCOM,     \n"
                + "                CASE WHEN E095FOR.CODRTR='3' AND E095FOR.RETIRF='S' AND E095FOR.RETOUR='S' THEN SUM((E301MCR.VLRCOM)*1.5)/100\n"
                + "                     ELSE 0 \n"
                + "                     END AS VLRIRF,              \n"
                + "                CASE WHEN E095FOR.CODRTR='3' AND E095FOR.RETIRF='S' AND E095FOR.RETOUR='S' THEN (SUM((E301MCR.VLRCOM))-SUM((E301MCR.VLRCOM)*1.5)/100)\n"
                + "                     ELSE SUM(E301MCR.VLRCOM)\n"
                + "                     END AS VLRLIQ,                          \n"
                + "            E301TCR.CODTPT,\n"
                + "                CASE WHEN E095FOR.CODRTR='1' THEN '1 - SIMPLES NACIONAL'\n"
                + "                     WHEN E095FOR.CODRTR='3' THEN '2 - SIMPLES NACIONAL C/ EXC.'\n"
                + "                     WHEN E095FOR.CODRTR='3' THEN '3 - REGIME NORMAL'                     \n"
                + "                     ELSE ' ' \n"
                + "                     END AS RETIRF,  \n"
                + "                     0 as NUMOCP, \n"
                + "            E090REP.INTNET, E095FOR.RETIRF, E095FOR.RETOUR, \n"
                + "            (SELECT MAX(IDEEXT)+1 AS IDEEXT FROM E000RIP WHERE CODEMP =1 AND CODFIL =1 AND IDEINT=64 AND CODINT =4) AS IDEEXT \n"
                + "           FROM E301TCR, E085CLI, E090REP, E095HFO, E095FOR, E028CPG, E301MCR, E001TNS, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND\n"
                + "                E301TCR.CODREP=E090REP.CODREP AND\n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E090REP.REPFOR=E095FOR.CODFOR AND                \n"
                + "                E090REP.REPFOR=E095HFO.CODFOR AND\n"
                + "                E301TCR.CODEMP=E095HFO.CODEMP AND\n"
                + "                E095HFO.CODFIL=1 AND\n"
                + "                E095HFO.CODEMP=E028CPG.CODEMP AND                \n"
                + "                E095HFO.CODCPG=E028CPG.CODCPG AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "    NOT EXISTS (SELECT * FROM USU_T420OCP WHERE USU_T420OCP.USU_CODEMP=1 AND USU_T420OCP.USU_CODFIL=1 AND E301TCR.USU_EMPCOM=USU_T420OCP.USU_EMPDES AND \n"
                + "                E301TCR.CODREP = USU_T420OCP.USU_CODREP AND USU_T420OCP.USU_CODFOR=E095FOR.CODFOR AND USU_T420OCP.USU_DATBAS=' \n";
        sqlSelect += PESQUISAR_POR + "') AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S'  \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301TCR.CODREP ,E095FOR.CODRTR , E095FOR.RETIRF, E095FOR.RETOUR, E301TCR.CODTPT, E090REP.REPFOR, \n"
                + "                E095HFO.CODCPG, E028CPG.DESCPG, E301MCR.CODEMP, E090REP.INTNET, \n"
                + "              CASE WHEN E301TCR.CODTPT = 'DPG' THEN 99  \n"
                + "                   WHEN E301TCR.USU_EMPCOM=1 AND E301TCR.USU_FILCOM=42 THEN 42 \n"
                + "                   ELSE E301TCR.USU_EMPCOM  \n"
                + "              END";
        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            resultado = getListaEmpresaOC(rs);
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

    public List<ComissaoTitulosMovimentos> getTitulosEmpresaOcItens(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT CASE WHEN E301TCR.CODTPT = 'DPG' THEN 99 \n"
                + "                     WHEN E301TCR.USU_EMPCOM=1 AND E301TCR.USU_FILCOM=42 THEN 42 \n"
                + "                     ELSE E301TCR.USU_EMPCOM \n"
                + "                     END AS USU_EMPCOM,           \n"
                + "                CASE WHEN E301TCR.CODCRP = 'AUT' THEN 'SVSVE0005'\n"
                + "                     WHEN E301TCR.CODCRP = 'MOT' THEN 'SVSVE0001'\n"
                + "                     ELSE E301TCR.CODCRP \n"
                + "                     END AS CODSER,                                                     \n"
                + "                CASE WHEN E301TCR.CODCRP = 'AUT' THEN 'COMISSAO SOBRE VENDAS - AUTO'\n"
                + "                     WHEN E301TCR.CODCRP = 'MOT' THEN 'COMISSAO SOBRE VENDAS - MOTO'\n"
                + "                     ELSE E301TCR.CODCRP \n"
                + "                     END AS CPLISO,\n"
                + "            E095HFO.CODFOR, E301TCR.CODCRP,SUM(E301MCR.VLRCOM) AS VLRCOM,E301TCR.CODTPT\n"
                + "           FROM E301TCR, E085CLI, E090REP, E095HFO,E028CPG, E301MCR, E001TNS, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND\n"
                + "                E301TCR.CODREP=E090REP.CODREP AND\n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E090REP.REPFOR=E095HFO.CODFOR AND\n"
                + "                E301TCR.CODEMP=E095HFO.CODEMP AND\n"
                + "                E095HFO.CODFIL=1 AND\n"
                + "                E095HFO.CODEMP=E028CPG.CODEMP AND                \n"
                + "                E095HFO.CODCPG=E028CPG.CODCPG AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S'  \n";
        sqlSelect += PESQUISA;
        sqlSelect += "   GROUP BY E301TCR.CODREP ,E301TCR.CODTPT, E301TCR.CODCRP, E090REP.REPFOR ,E095HFO.CODFOR, E095HFO.CODCPG, E028CPG.DESCPG, E301MCR.CODEMP, E090REP.INTNET,             \n"
                + "              CASE WHEN E301TCR.CODTPT = 'DPG' THEN 99  \n"
                + "                   WHEN E301TCR.USU_EMPCOM=1 AND E301TCR.USU_FILCOM=42 THEN 42 \n"
                + "                   ELSE E301TCR.USU_EMPCOM \n"
                + "              END \n";
//        sqlSelect += "   ORDER BY E301TCR.CODREP ,E301TCR.CODTPT \n";       
        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            resultado = getListaEmpresaOcItens(rs);
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

    public List<ComissaoTitulosMovimentos> getTitulosTipo(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E301MCR.CODTPT,E301TCR.CODCRP,sum(E301MCR.VLRMOV) AS VLRMOV, sum(E301MCR.VLRBCO) AS VLRBCO, \n"
                + "            sum(E301MCR.VLRCOM) AS VLRCOM,count(E301MCR.CODTPT) AS QTDTIT \n"
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND\n"
                + "                E301TCR.CODREP=E090REP.CODREP AND\n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301MCR.CODEMP,E301TCR.CODCRP,E301MCR.CODTPT \n";
        sqlSelect += "    ORDER BY E301MCR.CODEMP,E301TCR.CODCRP,E301MCR.CODTPT desc \n";
        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            resultado = getListaTipo(rs);
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

    public List<ComissaoTitulosMovimentos> getTitulosPercentual(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT  E301TCR.CODCRP,SUM(E301MCR.VLRMOV) AS VLRMOV, SUM(E301MCR.VLRBCO) AS VLRBCO, SUM(E301MCR.VLRCOM) AS VLRCOM, \n"
                + "           ROUND(E301TCR.PERCOM,2) AS PERCOM, COUNT(round(E301TCR.PERCOM,2)) AS QTDTIT \n"
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND\n"
                + "                E301TCR.CODREP=E090REP.CODREP AND\n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301TCR.CODEMP,ROUND(E301TCR.PERCOM,2),E301TCR.CODCRP \n";
        sqlSelect += "    ORDER BY E301TCR.CODEMP,E301TCR.CODCRP,ROUND(E301TCR.PERCOM,2) \n";
        System.out.print("  SQL " + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            resultado = getListaPerc(rs);
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
    public ComissaoTitulosMovimentos getTitulo(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = "SELECT E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC,E069GRE.NOMGRE\n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP, E301MCR.VLRBCO-E301MCR.VLRMOV AS DIFBCO\n"
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E002TPT, E069GRE \n"
                + "          WHERE \n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND\n"
                + "                E301TCR.CODREP=E090REP.CODREP AND\n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E085CLI.CODGRE=E069GRE.CODGRE AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.NUMNFV<>0 AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,E069GRE.NOMGRE,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC,E301TCR.PERCOM,\n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP \n "
                + "           UNION \n"
                + "         SELECT E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,E069GRE.NOMGRE,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC,\n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP\n"
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E002TPT \n"
                + "          WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND \n"
                + "                E301TCR.CODREP=E090REP.CODREP AND \n"
                + "                E090REP.USU_CALCOM = 'S' AND\n"
                + "                E085CLI.CODGRE=E069GRE.CODGRE AND\n"
                + "                E301TCR.CODTPT=E002TPT.CODTPT AND\n"
                + "                E002TPT.USU_CALCOM = 'S' AND\n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND  \n"
                + "                E301MCR.USU_NUMRLC<>'0' AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301MCR.CODEMP,E301MCR.CODFIL,E301MCR.NUMTIT,E301MCR.CODTPT,\n"
                + "                E301MCR.SEQMOV,E301MCR.CODTNS,E301MCR.DATMOV,E301MCR.VCTPRO,\n"
                + "                E301TCR.PERCOM,E301MCR.VLRABE,E301MCR.DATPGT,E301MCR.CODFPG,\n"
                + "                E301MCR.DATLIB,E301MCR.VLRMOV,E301MCR.VLRDSC,E301MCR.VLRODE,\n"
                + "                E301MCR.VLRJRS,E301MCR.VLRMUL,E301MCR.VLRENC,E301MCR.VLRCOR,\n"
                + "                E301MCR.VLROAC,E301MCR.VLRLIQ,E301MCR.VLRBCO,E301MCR.VLRCOM,\n"
                + "                E301MCR.PERJRS,E301MCR.ULTPGT,E301MCR.FILRLC,E301MCR.NUMRLC,E069GRE.NOMGRE,\n"
                + "                E301MCR.TPTRLC,E301MCR.USU_COMPAG,E301MCR.USU_NUMRLC,E301TCR.PERCOM,\n"
                + "                E301TCR.CODCRP,E301MCR.USU_FILRLC,E301MCR.USU_TPTRLC,E090REP.USU_CATREP\n";
        System.out.print("  SQL " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();
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

    private List<ComissaoTitulosMovimentos> getLista(ResultSet rs) throws SQLException, ParseException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();
        while (rs.next()) {
            ComissaoTitulosMovimentos e = new ComissaoTitulosMovimentos();
            e.setEmpresa(rs.getInt("codemp"));
            e.setFilial(rs.getInt("codfil"));
            e.setNumeroTitulo(rs.getString("numtit"));
            e.setTipoTitulo(rs.getString("codtpt"));
            e.setSequencia(rs.getInt("seqmov"));
            e.setTransacaoMov(rs.getString("codtns"));
            e.setDataLiberacao(rs.getDate("datmov"));
            if (e.getDataLiberacao() != null) {
                e.setDataLiberacaoS(this.utilDatas.converterDateToStr(e.getDataLiberacao()));
            }
            e.setDataEmissao(rs.getDate("datemi"));
            if (e.getDataEmissao() != null) {
                e.setDataEmissaoS(this.utilDatas.converterDateToStr(e.getDataEmissao()));
            }
            e.setVencimentoAtual(rs.getDate("vctpro"));
            if (e.getVencimentoAtual() != null) {
                e.setVencimentoAtualS(this.utilDatas.converterDateToStr(e.getVencimentoAtual()));
            }

            e.setValorAberto(rs.getDouble("vlrabe"));
            e.setDataPagamento(rs.getDate("datpgt"));
            if (e.getDataPagamento() != null) {
                e.setDataPagamentoS(this.utilDatas.converterDateToStr(e.getDataPagamento()));
            }
            e.setFormaPag(rs.getInt("codfpg"));
            e.setValorMov(rs.getDouble("vlrmov"));
            e.setValorDesc(rs.getDouble("vlrdsc"));
            e.setValorOutros(rs.getDouble("vlrode"));
            e.setJuros(rs.getDouble("vlrjrs"));
            e.setEncargos(rs.getDouble("vlrenc"));
            e.setMultas(rs.getDouble("vlrmul"));
            e.setPedido(rs.getInt("numped"));
            e.setNotaFiscal(rs.getInt("numnfv"));
            e.setValorLiquido(rs.getDouble("vlrliq"));
            e.setFilialRelacionado(rs.getInt("filrlc"));
            e.setTipoTituloRelacionado(rs.getString("tptrlc"));
            e.setTituloRelacionado(rs.getString("numrlc"));
            e.setFilialSubRelacionado(rs.getInt("usu_filrlc"));
            e.setTipoTituloSubRelacionado(rs.getString("usu_tptrlc"));
            e.setTituloSubRelacionado(rs.getString("usu_numrlc"));
            e.setValorBase(rs.getDouble("vlrbco"));
            e.setValorComissao(rs.getDouble("vlrcom"));
            e.setPercentualComissao(rs.getDouble("percom"));
            e.setIndicativoPago(rs.getString("USU_COMPAG"));
            e.setCategoriaRep(rs.getString("usu_catrep"));
            e.setLinha(rs.getString("CODCRP"));
            e.setDiferencaBase(rs.getDouble("DifBco"));
            e.setComissaoValidada(rs.getString("usu_compag"));
            resultado.add(e);
        }
        return resultado;
    }

    private List<ComissaoTitulosMovimentos> getListaEmpresa(ResultSet rs) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();
        while (rs.next()) {
            ComissaoTitulosMovimentos e = new ComissaoTitulosMovimentos();
            e.setEmpresaComissao(rs.getInt("usu_empcom"));
            e.setValorLiquido(rs.getDouble("vlrmov"));
            e.setValorBase(rs.getDouble("vlrbco"));
            e.setValorComissao(rs.getDouble("vlrcom"));
            e.setQuantidade(rs.getInt("QtdTit"));
            e.setTipoTitulo(rs.getString("codtpt"));
            resultado.add(e);
        }
        return resultado;
    }

    private List<ComissaoTitulosMovimentos> getListaEmpresaOC(ResultSet rs) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();
        while (rs.next()) {
            ComissaoTitulosMovimentos e = new ComissaoTitulosMovimentos();
            e.setRepOc(rs.getInt("codrep"));
            e.setFornecedor(rs.getInt("codfor"));
            e.setCondPagOc(rs.getString("codcpg"));
            e.setDescCondPagOc(rs.getString("descpg"));
            e.setEmpresaComissao(rs.getInt("usu_empcom"));
            e.setValorComissao(rs.getDouble("vlrcom"));
            e.setValorIrf(rs.getDouble("vlrirf"));
            e.setValorLiq(rs.getDouble("vlrliq"));
            e.setTipoTitulo(rs.getString("codtpt"));
            e.setEmailRep(rs.getString("intnet"));
            e.setRegimeTrib(rs.getString("retirf"));
            e.setIdeExt(rs.getInt("ideext"));
            e.setNumeroOC(rs.getInt("numocp"));

            resultado.add(e);
        }
        return resultado;
    }

    private List<ComissaoTitulosMovimentos> getListaEmpresaOcItens(ResultSet rs) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();
        while (rs.next()) {
            ComissaoTitulosMovimentos e = new ComissaoTitulosMovimentos();
            e.setEmpresaComissao(rs.getInt("usu_empcom"));
            e.setFornecedor(rs.getInt("codfor"));
            e.setServico(rs.getString("codser"));
            e.setComplementoServ(rs.getString("cpliso"));
            e.setLinha(rs.getString("CODCRP"));
            e.setValorComissaoItem(rs.getDouble("vlrcom"));
            e.setTipoTitulo(rs.getString("codtpt"));
            resultado.add(e);
        }
        return resultado;
    }

    private List<ComissaoTitulosMovimentos> getListaTipo(ResultSet rs) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();
        while (rs.next()) {
            ComissaoTitulosMovimentos e = new ComissaoTitulosMovimentos();
            e.setTipoTitulo(rs.getString("codtpt"));
            e.setValorLiquido(rs.getDouble("vlrmov"));
            e.setValorBase(rs.getDouble("vlrbco"));
            e.setValorComissao(rs.getDouble("vlrcom"));
            e.setLinha(rs.getString("CODCRP"));
            e.setQuantidade(rs.getInt("QtdTit"));
            resultado.add(e);
        }
        return resultado;
    }

    private List<ComissaoTitulosMovimentos> getListaPerc(ResultSet rs) throws SQLException {
        List<ComissaoTitulosMovimentos> resultado = new ArrayList<ComissaoTitulosMovimentos>();
        while (rs.next()) {
            ComissaoTitulosMovimentos e = new ComissaoTitulosMovimentos();
            e.setLinha(rs.getString("CODCRP"));
            e.setPercentualComissao(rs.getDouble("percom"));
            e.setQuantidade(rs.getInt("QtdTit"));
            e.setValorLiquido(rs.getDouble("vlrmov"));
            e.setValorBase(rs.getDouble("vlrbco"));
            e.setValorComissao(rs.getDouble("vlrcom"));
            resultado.add(e);
        }
        return resultado;
    }

    public boolean alterar(ComissaoTitulosMovimentos t) throws SQLException {
        PreparedStatement pst = null;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement("Update E301MCR SET USU_ComPag=? "
                    + "WHERE  codemp = ? "
                    + "and codfil=? "
                    + "and numtit=? "
                    + "and codtpt=? "
                    + "and seqmov=? ");

            pst.setString(1, t.getComissaoValidada());
            pst.setInt(2, t.getEmpresa());
            pst.setInt(3, t.getFilial());
            pst.setString(4, t.getNumeroTitulo());
            pst.setString(5, t.getTipoTitulo());
            pst.setInt(6, t.getSequencia());
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro " + e,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
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
