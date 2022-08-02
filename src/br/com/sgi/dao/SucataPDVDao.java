/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.SucataPDV;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceSucataPDVDao;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.Date;
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
public class SucataPDVDao implements InterfaceSucataPDVDao<SucataPDV> {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(SucataPDV t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(SucataPDV t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean inserir(SucataPDV t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "INSERT INTO USU_TPDVSUC (USU_CODEMP,USU_CODFIL,USU_NUMNFV,USU_CODSNF,USU_USUFEC,USU_PESLIQ,USU_DATLAN,USU_HORLAN,USU_INDSUC,USU_PRESUC,USU_PESFAT,USU_PREAJU) \n "
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Registro de sucata inserido com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

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

    private void setPreparedStatement(SucataPDV e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setInt(1, e.getEmpresa());
        pst.setInt(2, e.getFilial());
        pst.setInt(3, e.getNota());
        pst.setString(4, e.getSerie());
        pst.setInt(5, e.getUsuarioLancamento());
        pst.setDouble(6, e.getPesoNota());
        pst.setDate(7, new Date(e.getDataLancamento().getTime()));
        pst.setInt(8, e.getHoraLancamento());
        pst.setString(9, e.getIndicativoSucata());
        pst.setDouble(10, e.getPrecoSucata());
        pst.setDouble(11, e.getPesoSucata());
        pst.setDouble(12, e.getPrecoTotalSucata());        

    }

    @Override
    public List<SucataPDV> getSucataPDVs(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<SucataPDV> resultado = new ArrayList<SucataPDV>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT  CASE WHEN (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_INDSUC='S' ) = 1 THEN 'PESADO' \n"
                + " WHEN (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP \n"
                + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_INDSUC='N' ) = 1 THEN 'PAGA' \n"
                + " ELSE 'NAOPESADO' END  AS PESADO ,E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODSnf , E140NFV.NUMNFV, E140NFV.CODCLI , E085CLI.NOMCLI, \n"
                + "  NVL((SELECT (USU_VLRSUC) FROM E070FIL WHERE CODEMP =E140NFV.CODEMP AND CODFIL = E140NFV.CODFIL),0) AS VLRSUC, \n"
                + "  NVL((SELECT SUM(PESLIQ) FROM E140IPV WHERE CODEMP =E140NFV.CODEMP AND CODFIL = E140NFV.CODFIL AND CODSNF = E140NFV.CODSNF AND NUMNFV = E140NFV.NUMNFV),0) AS PESLIQ \n"
                + " , E140NFV.DATEMI , E140NFV.HORSAI,  usu_Tpdvsuc.USU_PreSuc, usu_Tpdvsuc.USU_PreAju, \n"
                + "     NVL(usu_Tpdvsuc.USU_PESLIQ,0) AS PESOSUCATA , NVL(usu_Tpdvsuc.USU_DATLAN,'01/01/1900') AS USU_DATLAN,   NVL(usu_Tpdvsuc.USU_HORLAN,0) AS USU_HORLAN \n"
                + "FROM E140NFV  LEFT JOIN E085CLI ON E140NFV.CODCLI = E085CLI.CODCLI \n"
                + " LEFT JOIN usu_Tpdvsuc ON USU_TPDVSUC.USU_CODEMP = E140NFV.CODEMP AND USU_TPDVSUC.USU_CODFIL = E140NFV.CODFIL AND USU_TPDVSUC.USU_CODSNF = E140NFV.CODSNF AND USU_TPDVSUC.USU_NUMNFV = E140NFV.NUMNFV \n"
                + "WHERE 0 =0  AND \n"
                + "E140NFV.USU_INDSUC='S' ";
        sqlSelect += PESQUISA;
        sqlSelect += "ORDER BY DATEMI DESC ";

      System.out.println("  SQL " + sqlSelect);

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
    
    public List<SucataPDV> getPagaSucataPDVs(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<SucataPDV> resultado = new ArrayList<SucataPDV>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT  CASE WHEN (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_INDSUC='S') = 1 THEN 'PESADO' \n"
                + " WHEN (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP \n"
                + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_INDSUC='N') = 1 THEN 'PAGA' \n"
                + " ELSE 'NAOPESADO' END  AS PESADO ,E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODSnf , E140NFV.NUMNFV, E140NFV.CODCLI , E085CLI.NOMCLI, \n"
                + "  NVL((SELECT (USU_VLRSUC) FROM E070FIL WHERE CODEMP =E140NFV.CODEMP AND CODFIL = E140NFV.CODFIL),0) AS VLRSUC, \n"
                + " nvl((SELECT SUM(PESLIQ) FROM E140IPV WHERE CODEMP =E140NFV.CODEMP AND CODFIL = E140NFV.CODFIL AND CODSNF = E140NFV.CODSNF AND NUMNFV = E140NFV.NUMNFV),0) AS PESLIQ \n"
                + " , E140NFV.DATEMI , E140NFV.HORSAI,  usu_Tpdvsuc.USU_PreSuc, usu_Tpdvsuc.USU_PreAju, \n"
                + "     NVL(usu_Tpdvsuc.USU_PESLIQ,0) AS PESOSUCATA , NVL(usu_Tpdvsuc.USU_DATLAN,'01/01/1900') AS USU_DATLAN,   NVL(usu_Tpdvsuc.USU_HORLAN,0) AS USU_HORLAN \n"
                + "FROM E140NFV  LEFT JOIN E085CLI ON E140NFV.CODCLI = E085CLI.CODCLI \n"
                + " LEFT JOIN usu_Tpdvsuc ON USU_TPDVSUC.USU_CODEMP = E140NFV.CODEMP AND USU_TPDVSUC.USU_CODFIL = E140NFV.CODFIL AND USU_TPDVSUC.USU_CODSNF = E140NFV.CODSNF AND USU_TPDVSUC.USU_NUMNFV = E140NFV.NUMNFV \n"
                + "WHERE 0 =0  AND \n"
                + "E140NFV.USU_INDSUC='S' AND usu_Tpdvsuc.USU_INDSUC='S'";
        sqlSelect += PESQUISA;
        sqlSelect += "ORDER BY DATEMI DESC ";

      System.out.println("  SQL " + sqlSelect);

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
    public SucataPDV getSucataPDV(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = "SELECT  CASE WHEN (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP  \n"
                + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_INDSUC='S') = 1 THEN 'PESADO' \n"
                + " WHEN (SELECT 1 FROM USU_TPDVSUC WHERE USU_CODEMP =E140NFV.CODEMP \n"
                + " AND USU_CODFIL = E140NFV.CODFIL AND USU_CODSNF = E140NFV.CODSnf AND USU_NUMNFV =  E140NFV.NUMNFV and USU_TPDVSUC.USU_INDSUC='N') = 1 THEN 'PAGA' \n"
                + " ELSE 'NAOPESADO' END  AS PESADO ,E140NFV.CODEMP, E140NFV.CODFIL, E140NFV.CODSnf , E140NFV.NUMNFV, E140NFV.CODCLI , E085CLI.NOMCLI, \n"
                + "  NVL((SELECT (USU_VLRSUC) FROM E070FIL WHERE CODEMP =E140NFV.CODEMP AND CODFIL = E140NFV.CODFIL),0) AS VLRSUC, \n"
                + "  NVL((SELECT SUM(PESLIQ) FROM E140IPV WHERE CODEMP =E140NFV.CODEMP AND CODFIL = E140NFV.CODFIL AND CODSNF = E140NFV.CODSNF AND NUMNFV = E140NFV.NUMNFV),0) AS PESLIQ \n"
                + " , E140NFV.DATEMI , E140NFV.HORSAI, usu_Tpdvsuc.USU_PreSuc, usu_Tpdvsuc.USU_PreAju, \n"
                + "     NVL(usu_Tpdvsuc.USU_PESLIQ,0) AS PESOSUCATA ,  NVL(usu_Tpdvsuc.USU_DATLAN,'01/01/1900') AS USU_DATLAN,   NVL(usu_Tpdvsuc.USU_HORLAN,0) AS USU_HORLAN  \n"
                + "FROM E140NFV  LEFT JOIN E085CLI ON E140NFV.CODCLI = E085CLI.CODCLI \n"
               + " LEFT JOIN usu_Tpdvsuc ON USU_TPDVSUC.USU_CODEMP = E140NFV.CODEMP AND USU_TPDVSUC.USU_CODFIL = E140NFV.CODFIL AND USU_TPDVSUC.USU_CODSNF = E140NFV.CODSNF AND USU_TPDVSUC.USU_NUMNFV = E140NFV.NUMNFV \n"
                + "WHERE 0 =0  ";

        sqlSelect += PESQUISA;
          sqlSelect += "ORDER BY DATEMI DESC  ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<SucataPDV> resultado = new ArrayList<SucataPDV>();
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

    private List<SucataPDV> getLista(ResultSet rs) throws SQLException, ParseException {
        List<SucataPDV> resultado = new ArrayList<SucataPDV>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            SucataPDV a = new SucataPDV();
            
            a.setPesado(rs.getString("PESADO"));
           
            a.setEmpresa(rs.getInt("CODEMP"));
            a.setFilial(rs.getInt("CODFIL"));
            a.setSerie(rs.getString("CODSNF"));
            a.setNota(rs.getInt("NUMNFV"));
            a.setCliente(rs.getInt("CODCLI"));
            a.setNomeCliente(rs.getString("NOMCLI"));
            a.setPesoNota(rs.getDouble("PESLIQ"));
            a.setPrecoSucata(rs.getDouble("USU_PreSuc"));
            a.setPrecoTotalSucata(rs.getDouble("USU_PreAju"));            
            a.setDataSaida(rs.getDate("DATEMI"));
            a.setHoraSaida(rs.getInt("HORSAI"));
            a.setPesoSucata(rs.getDouble("PESOSUCATA"));
            a.setDataLancamento(rs.getDate("USU_DATLAN"));
            a.setHoraLancamento(rs.getInt("USU_HORLAN"));
            resultado.add(a);

        }
        return resultado;
    }

}
