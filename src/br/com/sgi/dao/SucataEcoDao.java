/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.SucataEco;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Motivo;
import br.com.sgi.bean.SucataEco;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceSucataEcoDao;
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
public class SucataEcoDao implements InterfaceSucataEcoDao<SucataEco> {

    private Connection con;
    private UtilDatas utilDatas;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(SucataEco t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deletar(SucataEco t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean alterar(SucataEco t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean inserir(SucataEco t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = " ";
        // String sqlInsert = "INSERT INTO USU_TPDVSUC (USU_CODEMP,USU_CODFIL,USU_NUMNFV,USU_CODSNF,USU_USUFEC,USU_PESLIQ,USU_DATLAN,USU_HORLAN) \n "
        //       + " VALUES (?,?,?,?,?,?,?,?)";

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

    private void setPreparedStatement(SucataEco e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setInt(1, e.getEmpresa());
        pst.setInt(2, e.getFilial());
        pst.setInt(3, e.getNota());
        pst.setString(4, e.getSerie());
        pst.setInt(5, e.getUsuarioLancamento());
        pst.setDouble(6, e.getPesoNota());
        pst.setDate(7, new Date(e.getDataLancamento().getTime()));
        pst.setInt(8, e.getHoraLancamento());

    }

    @Override
    public List<SucataEco> getSucataEcos(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<SucataEco> resultado = new ArrayList<SucataEco>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "  SELECT \n"
                + "CASE WHEN 1 = (SELECT 1 FROM USU_TSUCMOV a WHERE a.usu_debcre ='4 - CREDITO' AND a.USU_AUTMOT NOT IN ('IND')\n"
                + " AND a.USU_CODCLI = USU_TSUCMOV.USU_CODCLI AND a.USU_NUMPED =  USU_TSUCMOV.USU_NUMPED AND a.USU_numocp =  USU_TSUCMOV.USU_NUMOCP  AND ROWNUM =1 ) THEN 'PESADO'\n"
                + "ELSE 'NAOPESADO' END AS PESADO, \n"
                + "USU_CODEMP, USU_CODFILSUC,USU_CODSNF,USU_NUMNFV,USU_CODCLI, NOMCLI, \n"
                + "CASE WHEN  USU_DEBCRE ='3 - DEBITO' THEN USU_PESFAT ELSE (SELECT USU_PESFAT FROM USU_TSUCMOV A WHERE  A.USU_DEBCRE ='3 - DEBITO' AND A.USU_NUMNFV = USU_TSUCMOV.USU_NUMNFV AND A.USU_NUMOCP =USU_TSUCMOV.USU_NUMOCP  AND A.USU_NUMPED =USU_TSUCMOV.USU_NUMPED AND ROWNUM =1) END AS  USU_PESFAT ,\n"
                + "CASE WHEN  USU_DEBCRE ='3 - DEBITO' THEN USU_DATGER"
                + " ELSE (SELECT USU_DATGER FROM USU_TSUCMOV A WHERE  A.USU_DEBCRE ='3 - DEBITO' AND A.USU_NUMNFV = USU_TSUCMOV.USU_NUMNFV AND A.USU_NUMOCP =USU_TSUCMOV.USU_NUMOCP  AND A.USU_NUMPED =USU_TSUCMOV.USU_NUMPED AND ROWNUM =1) END AS  USU_DATGER, \n"
                + "CASE WHEN  USU_DEBCRE ='3 - DEBITO' THEN USU_HORGER "
                + "ELSE (SELECT USU_HORGER FROM USU_TSUCMOV A WHERE  A.USU_DEBCRE ='3 - DEBITO' AND A.USU_NUMNFV = USU_TSUCMOV.USU_NUMNFV AND A.USU_NUMOCP =USU_TSUCMOV.USU_NUMOCP  AND A.USU_NUMPED =USU_TSUCMOV.USU_NUMPED  AND ROWNUM =1) END AS USU_HORGER, \n"
                + "USU_PESREC,\n"
                + "CASE WHEN  USU_DEBCRE ='4 - CREDITO' THEN USU_DATGER ELSE (TO_DATE ('01/01/1900', 'MM/DD/YYYY')) END AS USU_DATLAN, \n"
                + "CASE WHEN  USU_DEBCRE ='4 - CREDITO' THEN USU_HORGER ELSE 0 END AS USU_HORLAN,\n"
                + "USU_NUMOCP,USU_CODSUC, USU_NUMNFC ,\n"
                + "USU_NUMPED,USU_PESORD \n"
                + "FROM USU_TSUCMOV \n"
                + "LEFT JOIN E085CLI ON  USU_TSUCMOV.USU_CODCLI = E085CLI.CODCLI\n"
                + "WHERE  0 = 0";
        sqlSelect += PESQUISA;
        sqlSelect += "ORDER BY USU_DATGER DESC ";

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
    public SucataEco getSucataEco(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = "SELECT \n"
                + "CASE WHEN 1 = (SELECT 1 FROM USU_TSUCMOV a WHERE a.usu_debcre ='4 - CREDITO' AND a.USU_AUTMOT NOT IN ('IND')\n"
                + " AND a.USU_CODCLI = USU_TSUCMOV.USU_CODCLI AND a.USU_NUMPED =  USU_TSUCMOV.USU_NUMPED AND a.USU_numocp =  USU_TSUCMOV.USU_NUMOCP  AND ROWNUM =1 ) THEN 'PESADO'\n"
                + "ELSE 'NAOPESADO' END AS PESADO, \n"
                + "USU_CODEMP, USU_CODFILSUC,USU_CODSNF,USU_NUMNFV,USU_CODCLI, NOMCLI, \n"
                + "CASE WHEN  USU_DEBCRE ='3 - DEBITO' THEN USU_PESFAT ELSE (SELECT USU_PESFAT FROM USU_TSUCMOV A WHERE  A.USU_DEBCRE ='3 - DEBITO' AND A.USU_NUMNFV = USU_TSUCMOV.USU_NUMNFV AND A.USU_CODSNF = USU_TSUCMOV.USU_CODSNF  AND ROWNUM =1) END AS  USU_PESFAT, \n"
                + "CASE WHEN  USU_DEBCRE ='3 - DEBITO' THEN USU_DATGER "
                + "ELSE (SELECT USU_DATGER FROM USU_TSUCMOV A WHERE  A.USU_DEBCRE ='3 - DEBITO' AND A.USU_NUMNFV = USU_TSUCMOV.USU_NUMNFV AND A.USU_CODSNF = USU_TSUCMOV.USU_CODSNF  AND ROWNUM =1) END AS  USU_DATGER, \n"
                + "CASE WHEN  USU_DEBCRE ='3 - DEBITO' THEN USU_HORGER "
                + "ELSE (SELECT USU_HORGER FROM USU_TSUCMOV A WHERE  A.USU_DEBCRE ='3 - DEBITO' AND A.USU_NUMNFV = USU_TSUCMOV.USU_NUMNFV AND A.USU_CODSNF = USU_TSUCMOV.USU_CODSNF  AND ROWNUM =1) END AS USU_HORGER,\n"
                + "USU_PESREC,\n"
                + "CASE WHEN  USU_DEBCRE ='4 - CREDITO' THEN USU_DATGER ELSE (TO_DATE ('01/01/1900', 'MM/DD/YYYY')) END AS USU_DATLAN, \n"
                + "CASE WHEN  USU_DEBCRE ='4 - CREDITO' THEN USU_HORGER ELSE (SELECT USU_HORGER FROM USU_TSUCMOV A WHERE  A.USU_DEBCRE ='3 - DEBITO' AND A.USU_NUMNFV = USU_TSUCMOV.USU_NUMNFV AND A.USU_CODSNF = USU_TSUCMOV.USU_CODSNF  AND ROWNUM =1) END AS USU_HORLAN,\n"
                + "USU_NUMOCP,USU_CODSUC, USU_NUMNFC ,\n"
                + "USU_NUMPED,USU_PESORD \n"
                + "FROM USU_TSUCMOV \n"
                + "LEFT JOIN E085CLI ON  USU_TSUCMOV.USU_CODCLI = E085CLI.CODCLI\n"
                + "WHERE  0 = 0";

        sqlSelect += PESQUISA;
        sqlSelect += "ORDER BY USU_DATGER DESC  ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<SucataEco> resultado = new ArrayList<SucataEco>();
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

    private List<SucataEco> getLista(ResultSet rs) throws SQLException, ParseException {
        List<SucataEco> resultado = new ArrayList<SucataEco>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            SucataEco a = new SucataEco();
            a.setPesado(rs.getString("PESADO"));
            a.setEmpresa(rs.getInt("USU_CODEMP"));
            a.setFilial(rs.getInt("USU_CODFILSUC"));
            a.setSerie(rs.getString("USU_CODSNF"));
            a.setNota(rs.getInt("USU_NUMNFV"));
            a.setCliente(rs.getInt("USU_CODCLI"));
            a.setNomeCliente(rs.getString("NOMCLI"));
            a.setPesoNota(rs.getDouble("USU_PESFAT"));
            a.setDataSaida(rs.getDate("USU_DATGER"));
            a.setHoraSaida(rs.getInt("USU_HORGER"));
            a.setPesoSucata(rs.getDouble("USU_PESREC"));
            a.setDataLancamento(rs.getDate("USU_DATLAN"));
            a.setHoraLancamento(rs.getInt("USU_HORLAN"));
            a.setNumeroOC(rs.getInt("USU_NUMOCP"));
            a.setCodigoSucata(rs.getString("USU_CODSUC"));
            a.setNotaEntrada(rs.getInt("USU_NUMNFC"));
            a.setPedidoSucata(rs.getInt("USU_NUMPED"));
            a.setPesoOC(rs.getDouble("USU_PESORD"));
            resultado.add(a);

        }
        return resultado;
    }

    @Override
    public SucataEcoParametros getSucataEcoParamentrosOC(String Filial, String OrdemCompra, String Fornecedor) throws SQLException {
        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = "SELECT E420IPO.CODEMP, E420IPO.CODFIL ,E420IPO.NUMOCP ,E420IPO.CODPRO,E420IPO.CODDER\n"
                + ",E420IPO.CODDEP ,E009PTE.TNSPRO,E420IPO.CODCCU,\n"
                + "(SELECT USU_VLRSUC FROM E070FIL WHERE CODEMP =E420IPO.CODEMP  AND CODFIL= " + Filial + ")  AS USU_VLRSUC \n"
                + "  ,E420IPO.SEQIPO,E420IPO.CTAFIN,0 as IDEEXT \n "
                + " FROM E420OCP  \n"
                + " LEFT JOIN E420IPO ON  E420OCP.CODEMP = E420IPO.CODEMP AND E420OCP.CODFIL = E420IPO.CODFIL AND E420OCP.NUMOCP = E420IPO.NUMOCP\n"
                + " LEFT JOIN E095FOR ON  E420OCP.CODFOR = E095FOR.CODFOR\n"
                + " LEFT JOIN E009PTE ON  E420IPO.CODEMP = E009PTE.CODEMP AND E420IPO.TNSPRO = E009PTE.TNSBPO   AND E095FOR.SIGUFS = E009PTE.SIGUFS \n"
                + "                   AND E009PTE.CODFIL = (SELECT   MAX(CODFIL)  FROM E009PTE  A WHERE A.CODEMP =E420IPO.CODEMP  AND A.TNSBPO = E420IPO.TNSPRO \n"
                + "                   AND A.SIGUFS =E009PTE.SIGUFS AND (A.CODFIL =0 OR A.CODFIL = " + Filial + "))\n"
                + " WHERE E420OCP.CODEMP =1 AND (E420OCP.CODFIL =1 OR E420OCP.CODFIL = " + Filial + ")\n"
                + "                            AND E420OCP.NUMOCP =  " + OrdemCompra + " AND E420OCP.codfor=  " + Fornecedor + "";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<SucataEcoParametros> resultado = new ArrayList<SucataEcoParametros>();
            resultado = getListaParametros(rs);
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

    private List<SucataEcoParametros> getListaParametros(ResultSet rs) throws SQLException, ParseException {
        List<SucataEcoParametros> resultado = new ArrayList<SucataEcoParametros>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            SucataEcoParametros a = new SucataEcoParametros();
            a.setEmpresa(rs.getInt("CODEMP"));
            a.setFilial(rs.getInt("CODFIL"));
            a.setOrdemCompras(rs.getInt("NUMOCP"));
            a.setProduto(rs.getString("CODPRO"));
            a.setDerivacao(rs.getString("CODDER"));
            a.setDeposito(rs.getString("CODDEP"));
            a.setTransacao(rs.getString("TNSPRO"));
            a.setCentroCustos(rs.getString("CODCCU"));
            a.setValorSucata(rs.getDouble("USU_VLRSUC"));
            a.setSeqIpo(rs.getInt("SEQIPO"));
            a.setContaFinanceira(rs.getInt("CTAFIN"));
            a.setIdeExt(rs.getInt("IDEEXT"));
            resultado.add(a);

        }
        return resultado;
    }

     @Override
    public SucataEcoParametros getSucataEcoParamentros(String Filial, String Empresa) throws SQLException {
        java.sql.PreparedStatement pst = null;
        ResultSet rs = null;
        String sqlSelect = "SELECT  CODEMP, CODFIL, 0 AS NUMOCP, USU_PROSUC AS CODPRO, ' ' AS CODDER, USU_DEPSUC AS CODDEP, USU_TNSECO AS TNSPRO, '95' as CODCCU ,USU_VLRSUC, \n"
                + "1 AS SEQIPO ,1102 as CTAFIN ,\n"
                + "(SELECT MAX(IDEEXT)+1 AS IDEEXT FROM E000RIP WHERE CODEMP =E070FIL.CODEMP AND CODFIL =E070FIL.CODFIL AND IDEINT =64 AND CODINT =4) AS IDEEXT\n"
                + " FROM E070FIL WHERE CODEMP = "+Empresa+" AND CODFIL ="+Filial+" ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<SucataEcoParametros> resultado = new ArrayList<SucataEcoParametros>();
            resultado = getListaParametros(rs);
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

}
