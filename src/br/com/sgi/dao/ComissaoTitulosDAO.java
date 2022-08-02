/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.ClienteGrupo;
import br.com.sgi.bean.ComissaoTitulos;
import br.com.sgi.bean.Representante;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceComissaoTitulosDAO;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class ComissaoTitulosDAO implements InterfaceComissaoTitulosDAO<ComissaoTitulos> {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public List<ComissaoTitulos> getTitulos(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<ComissaoTitulos> resultado = new ArrayList<ComissaoTitulos>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E301TCR.CODEMP, E301TCR.CODFIL, E301TCR.NUMTIT, E301TCR.CODTPT, E301TCR.CODTNS,\n"
                + "                E301TCR.SITTIT, E301TCR.DATEMI, E301TCR.DATENT, E301TCR.CODCLI, E301TCR.CODREP,\n"
                + "                E301TCR.OBSTCR, E301TCR.VCTORI, E301TCR.VLRORI, E301TCR.CODFPG, E301TCR.VLRABE,\n"
                + "                E301TCR.PERCOM, E301TCR.VLRBCO, E301TCR.VLRCOM, E301TCR.VLRORI, E301TCR.CODSNF,\n"
                + "                E301TCR.NUMNFV, E301TCR.NUMPED, E085CLI.NOMCLI, E090REP.NOMREP, E301TCR.CODCRP, \n"
                + "                E085CLI.CODGRE, E069GRE.NOMGRE,E090REP.USU_CATREP AS CATREP \n   "
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E069GRE \n"
                + "           WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND \n"
                + "                E301TCR.CODREP=E090REP.CODREP AND \n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.NUMNFV<>0 AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' AND \n"
                + "                E085CLI.CODGRE=E069GRE.CODGRE \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301TCR.CODEMP, E301TCR.CODFIL, E301TCR.NUMTIT, E301TCR.CODTPT, E301TCR.CODTNS, \n"
                + "                E301TCR.SITTIT, E301TCR.DATEMI, E301TCR.DATENT, E301TCR.CODCLI, E301TCR.CODREP, \n"
                + "                E301TCR.OBSTCR, E301TCR.VCTORI, E301TCR.VLRORI, E301TCR.CODFPG, E301TCR.VLRABE, \n"
                + "                E301TCR.PERCOM, E301TCR.VLRBCO, E301TCR.VLRCOM, E301TCR.VLRORI, E301TCR.CODSNF, \n"
                + "                E301TCR.NUMNFV, E301TCR.NUMPED, E085CLI.NOMCLI, E090REP.NOMREP, E301TCR.CODCRP, \n"
                + "                E085CLI.CODGRE, E069GRE.NOMGRE, E090REP.USU_CATREP \n";
//                + "           UNION \n"
//                + "         SELECT E301TCR.CODEMP, E301TCR.CODFIL, E301TCR.NUMTIT, E301TCR.CODTPT, E301TCR.CODTNS, \n"
//                + "                E301TCR.SITTIT, E301TCR.DATEMI, E301TCR.DATENT, E301TCR.CODCLI, E301TCR.CODREP, \n"
//                + "                E301TCR.OBSTCR, E301TCR.VCTORI, E301TCR.VLRORI, E301TCR.CODFPG, E301TCR.VLRABE, \n"
//                + "                E301TCR.PERCOM, E301TCR.VLRBCO, E301TCR.VLRCOM, E301TCR.VLRORI, E301TCR.CODSNF, \n"
//                + "                E301TCR.NUMNFV, E301TCR.NUMPED, E085CLI.NOMCLI, E090REP.NOMREP, E301TCR.CODCRP, \n"
//                + "                E090REP.USU_CATREP AS CATREP \n"
//                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS \n"
//                + "          WHERE \n"
//                + "                0=0 AND \n"
//                + "                E301TCR.CODCLI=E085CLI.CODCLI AND \n"
//                + "                E301TCR.CODREP=E090REP.CODREP AND \n"
//                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
//                + "                E301MCR.USU_NUMRLC<>'0' AND \n"
//                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
//                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S'";
//        sqlSelect += PESQUISA;
//        sqlSelect +="     GROUP BY E301TCR.CODEMP, E301TCR.CODFIL, E301TCR.NUMTIT, E301TCR.CODTPT, E301TCR.CODTNS, \n"
//                + "                E301TCR.SITTIT, E301TCR.DATEMI, E301TCR.DATENT, E301TCR.CODCLI, E301TCR.CODREP, \n"
//                + "                E301TCR.OBSTCR, E301TCR.VCTORI, E301TCR.VLRORI, E301TCR.CODFPG, E301TCR.VLRABE, \n"
//                + "                E301TCR.PERCOM, E301TCR.VLRBCO, E301TCR.VLRCOM, E301TCR.VLRORI, E301TCR.CODSNF, \n"
//                + "                E301TCR.NUMNFV, E301TCR.NUMPED, E085CLI.NOMCLI, E090REP.NOMREP, E301TCR.CODCRP, \n"
//                + "                E090REP.USU_CATREP";        
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
    public ComissaoTitulos getTitulo(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = "SELECT E301TCR.CODEMP, E301TCR.CODFIL, E301TCR.NUMTIT, E301TCR.CODTPT, E301TCR.CODTNS,\n"
                + "                E301TCR.SITTIT, E301TCR.DATEMI, E301TCR.DATENT, E301TCR.CODCLI, E301TCR.CODREP,\n"
                + "                E301TCR.OBSTCR, E301TCR.VCTORI, E301TCR.VLRORI, E301TCR.CODFPG, E301TCR.VLRABE,\n"
                + "                E301TCR.PERCOM, E301TCR.VLRBCO, E301TCR.VLRCOM, E301TCR.VLRORI, E301TCR.CODSNF,\n"
                + "                E301TCR.NUMNFV, E301TCR.NUMPED, E085CLI.NOMCLI, E090REP.NOMREP, E301TCR.CODCRP, \n"
                + "                E085CLI.CODGRE, E069GRE.NOMGRE,E090REP.USU_CATREP AS CATREP \n   "
                + "           FROM E301TCR, E085CLI, E090REP, E301MCR, E001TNS, E069GRE \n"
                + "           WHERE\n"
                + "                0=0 AND \n"
                + "                E301TCR.CODCLI=E085CLI.CODCLI AND \n"
                + "                E301TCR.CODREP=E090REP.CODREP AND \n"
                + "                E301TCR.SITTIT NOT IN ('CA') AND \n"
                + "                E301TCR.NUMNFV<>0 AND \n"
                + "                E301TCR.CODEMP=E301MCR.CODEMP AND E301TCR.CODFIL=E301MCR.CODFIL AND E301TCR.NUMTIT=E301MCR.NUMTIT AND E301TCR.CODTPT=E301MCR.CODTPT AND \n"
                + "                E001TNS.CODEMP=E301MCR.CODEMP AND E001TNS.CODTNS=E301MCR.CODTNS AND E001TNS.USU_CALCOM = 'S' AND \n"
                + "                E085CLI.CODGRE=E069GRE.CODGRE \n";
        sqlSelect += PESQUISA;
        sqlSelect += "    GROUP BY E301TCR.CODEMP, E301TCR.CODFIL, E301TCR.NUMTIT, E301TCR.CODTPT, E301TCR.CODTNS, \n"
                + "                E301TCR.SITTIT, E301TCR.DATEMI, E301TCR.DATENT, E301TCR.CODCLI, E301TCR.CODREP, \n"
                + "                E301TCR.OBSTCR, E301TCR.VCTORI, E301TCR.VLRORI, E301TCR.CODFPG, E301TCR.VLRABE, \n"
                + "                E301TCR.PERCOM, E301TCR.VLRBCO, E301TCR.VLRCOM, E301TCR.VLRORI, E301TCR.CODSNF, \n"
                + "                E301TCR.NUMNFV, E301TCR.NUMPED, E085CLI.NOMCLI, E090REP.NOMREP, E301TCR.CODCRP, \n"
                + "                E085CLI.CODGRE, E069GRE.NOMGRE, E090REP.USU_CATREP \n";
        sqlSelect += " ORDER BY E301TCR.CODEMP, E301TCR.CODFIL, E301TCR.NUMTIT desc \n";
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            List<ComissaoTitulos> resultado = new ArrayList<ComissaoTitulos>();
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

    private List<ComissaoTitulos> getLista(ResultSet rs) throws SQLException {
        List<ComissaoTitulos> resultado = new ArrayList<ComissaoTitulos>();
        while (rs.next()) {
            ComissaoTitulos e = new ComissaoTitulos();
            e.setEmpresa(rs.getInt("codemp"));
            e.setFilial(rs.getInt("codfil"));
            e.setNumeroTitulo(rs.getString("numtit"));
            e.setTipoTitulo(rs.getString("codtpt"));
            e.setTransacao(rs.getString("codtns"));
            e.setSituacao(rs.getString("sittit"));
            e.setDataEmissao(rs.getDate("datemi"));
            e.setDataEntrada(rs.getDate("datent"));
            e.setCliente(rs.getInt("codcli"));
            e.setRepresentante(rs.getInt("codrep"));
            e.setObservacoes(rs.getString("obstcr"));
            e.setVencimentoOriginal(rs.getDate("vctori"));
            e.setValorOriginal(rs.getDouble("vlrori"));
            e.setFormaPagamento(rs.getInt("codfpg"));
            e.setValorAberto(rs.getDouble("vlrabe"));
            e.setBaseComissao(rs.getDouble("vlrbco"));
            e.setValorComissao(rs.getDouble("vlrcom"));
            e.setPercentualComissao(rs.getDouble("percom"));
            e.setSerieNota(rs.getString("codsnf"));
            e.setNumeroNota(rs.getInt("numnfv"));
            e.setPedido(rs.getInt("numped"));
            e.setGrupoContasRec(rs.getString("codcrp"));
            e.setCategoria("catrep");

            //classe cliente
            e.setCadCliente(new Cliente(e.getCliente(), rs.getString("nomcli"), ""));
            Representante rep = new Representante();
            rep.setNome(rs.getString("nomrep"));
            e.setCadRepresentante(rep);

            //classe cliente
            e.setCadClienteGrupo(new ClienteGrupo());
            ClienteGrupo cligru = new ClienteGrupo();
            cligru.setCodgrp(rs.getString("codgre"));
            cligru.setNome(rs.getString("nomgre"));
            e.setCadClienteGrupo(cligru);            
            resultado.add(e);
        }
        return resultado;
    }
}
