/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.NotaSerie;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.frame.FrmHubTransferencia;
import br.com.sgi.interfaces.InterfaceNotaSerieDAO;
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
 * @author jairosilva Teste Jairo
 */
public class NotaSerieDAO implements InterfaceNotaSerieDAO<NotaSerie> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private List<NotaSerie> getLista(ResultSet rs) throws SQLException, ParseException {
        List<NotaSerie> resultado = new ArrayList<>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            NotaSerie e = new NotaSerie();
            e.setEmpresa(rs.getInt("codemp"));
            e.setFilial(rs.getInt("codfil"));
            e.setClientecidade(rs.getString("cidcli"));
            e.setCliente(rs.getInt("codcli"));
            e.setClienteestado(rs.getString("sigufs"));
            e.setNota(rs.getInt("numnfv"));
            e.setNotaemissao(rs.getDate("datemi"));
            e.setProducaodata(rs.getDate("datprd"));
            e.setPedido(rs.getInt("numped"));
            e.setProdutodescricao(rs.getString("despro"));
            e.setProduto(rs.getString("codpro"));
            e.setOrigem(rs.getString("codori"));
            e.setProdutoderivacao(rs.getString("codder"));
            e.setId(0);
            e.setPedido(rs.getInt("numped"));
            e.setQuantidade(1.0);
            e.setSerie(rs.getString("numsep"));
            e.setClientenome(rs.getString("apecli"));
            e.setNotaserie(rs.getString("codsnf"));
            e.setRepresentante_id(rs.getInt("codrep"));

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<NotaSerie> getNotaSeries(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<NotaSerie> resultado = new ArrayList<NotaSerie>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT E140NFV.CODEMP,\n"
                + "       E140NFV.CODFIL,\n"
                + "       E140NFV.CODSNF,\n"
                + "       E140NFV.TNSPRO,\n"
                + "       E140NFV.NUMNFV,\n"
                + "       E140IPV.QTDFAT,\n"
                + "       E140IPV.NUMPED,\n"
                + "       E140IPV.CODPRO,\n"
                + "       E075PRO.DESPRO,\n"
                + "       E075PRO.CODORI,\n"
                + "       E140IPV.CODDER,\n"
                + "       E140DLS.NUMSEP,\n"
                + "       E140NFV.DATEMI,\n"
                + "       sysdate  as DATPRD,\n"
                + "       E140NFV.CODCLI,\n"
                + "       E085CLI.APECLI,\n"
                + "       E085CLI.SIGUFS,\n"
                + "       E085CLI.CIDCLI,\n"
                + "       E140NFV.CODREP\n"
                + "  FROM E140DLS\n"
                + "  LEFT JOIN E140IPV\n"
                + "    ON E140IPV.CODEMP = E140DLS.CODEMP\n"
                + "   AND E140IPV.CODFIL = E140DLS.CODFIL\n"
                + "   AND E140IPV.CODSNF = E140DLS.CODSNF\n"
                + "   AND E140IPV.NUMNFV = E140DLS.NUMNFV\n"
                + "   AND E140IPV.SEQIPV = E140DLS.SEQIPV\n"
                + "  LEFT JOIN E140NFV\n"
                + "    ON E140NFV.CODEMP = E140DLS.CODEMP\n"
                + "   AND E140NFV.CODFIL = E140DLS.CODFIL\n"
                + "   AND E140NFV.CODSNF = E140DLS.CODSNF\n"
                + "   AND E140NFV.NUMNFV = E140DLS.NUMNFV\n"
                + "  LEFT JOIN E085CLI\n"
                + "    ON E085CLI.CODCLI = E140NFV.CODCLI\n"
                + "  LEFT JOIN E001TNS\n"
                + "    ON E001TNS.CODEMP = E140NFV.CODEMP\n"
                + "   AND E001TNS.CODTNS = E140NFV.TNSPRO\n"
                + "  LEFT JOIN E075PRO\n"
                + "    ON E075PRO.CODEMP = E140IPV.CODEMP\n"
                + "   AND E075PRO.CODPRO = E140IPV.CODPRO   \n"
                + "  LEFT JOIN E120PED\n"
                + "    ON E120PED.CODEMP = E140IPV.CODEMP\n"
                + "   AND E120PED.CODFIL = E140IPV.CODFIL      \n"
                + "   AND E120PED.NUMPED = E140IPV.NUMPED         \n"
                + " WHERE E140NFV.SITNFV = 2\n"
                + "   AND E001TNS.VENFAT = 'N'\n"
                + "   AND E140NFV.DATEMI >= '01/07/2021'\n"
                //  + "   AND E140NFV.USU_NFVINT = 'S'\n"
                + "   AND E120PED.TNSPRO = '901HB'\n"
                //   + "   AND E140NFV.CODCLI = 10469\n"
                //  + "  and E140NFV.NUMNFV IN (10574, 11274) "
                + "  ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }

        System.out.println("br.com.integrador.dao.NotaSerieDAO.getNotaSeries()\n" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getLista(rs);

            pst.close();
            rs.close();

        } catch (Exception ex) {
            System.out.println("br.com.integrador.dao.NotaSerieDAO.getNotaSeries()\n" + ex.toString());
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
    public boolean integrarNotaSerieApp(List<NotaSerie> lista) throws SQLException {
        int cont = 0;

        PreparedStatement pst = null;

        String sqlInsert = " insert into notaserie \n"
                + "(empresa, filial, nota, notaserie, notaemissao, \n"
                + "cliente, clientenome, clienteestado, clientecidade, produto, \n"
                + "produtoderivacao, produtodescricao, serie, quantidade, producaodata, \n"
                + "pedido, representante_id, linha, id)\n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?)";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);

            for (NotaSerie p : lista) {

                //empresa,filial,nota,notaserie,notaemissao,
                pst.setInt(1, p.getEmpresa());
                pst.setInt(2, p.getFilial());
                pst.setInt(3, p.getNota());
                pst.setString(4, p.getNotaserie());
                pst.setDate(5, new java.sql.Date(p.getNotaemissao().getTime()));

                //cliente,clientenome,clienteestado,clientecidade,produto, ,
                pst.setInt(6, p.getCliente());
                pst.setString(7, p.getClientenome());
                pst.setString(8, p.getClienteestado());
                pst.setString(9, p.getClientecidade());
                pst.setString(10, p.getProduto());
                // produtoderivacao,produtodescricao,serie, quantidade, producaodata,
                pst.setString(11, p.getProdutoderivacao());
                pst.setString(12, p.getProdutodescricao());
                pst.setString(13, p.getSerie());
                pst.setDouble(14, p.getQuantidade());
                pst.setDate(15, new java.sql.Date(p.getProducaodata().getTime()));

                //pedido,representante_id, linha, id
                pst.setInt(16, p.getPedido());
                pst.setInt(17, p.getRepresentante_id());
                pst.setString(18, p.getOrigem());
                pst.setInt(19, p.getId());
                pst.executeUpdate();
                cont++;
                System.out.print(" \nSerie Integrada  " + p.getSerie() + "  - " + cont);
                FrmHubTransferencia.cadastro(" --> " + cont);

                // atualizar o pedido
                atualizarNotaSerieIntegrada(p);
            }
            pst.close();

            JOptionPane.showMessageDialog(null, "SUCESSO: Nota de transferencia  registrado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            System.out.print(" erro " + ex.toString());
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
    public boolean atualizarNotaSerieIntegrada(NotaSerie ph) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update e140nfv set USU_NFVINT = ? \n"
                + "where codemp = ? and codfil = ? and numnfv = ? and codcli = ?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, "N");
            pst.setInt(2, ph.getEmpresa());
            pst.setInt(3, ph.getFilial());
            pst.setInt(4, ph.getNota());
            pst.setInt(5, ph.getCliente());

            pst.executeUpdate();
            System.out.println("br.com.integrador.dao.NotaSerieDAO.atualizarNotaSerieIntegrada()\n Nota integrada no APP");

            pst.close();

            return true;
        } catch (Exception ex) {
            System.out.print(" erro " + ex.toString());
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    public List<NotaSerie> getNotaSeriesHub(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<NotaSerie> resultado = new ArrayList<NotaSerie>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select * from notaserie "
                + "  ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }

        System.out.println("br.com.integrador.dao.NotaSerieDAO.getNotaSeries()\n" + sqlSelect);

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {
                NotaSerie nf = new NotaSerie();
                nf.setProduto(rs.getString("produto"));
                buscarOrigem(nf);

            }

            pst.close();
            rs.close();

        } catch (Exception ex) {
            System.out.println("br.com.integrador.dao.NotaSerieDAO.getNotaSeries()\n" + ex.toString());
            return null;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return resultado;
    }

    public NotaSerie buscarOrigem(NotaSerie ph) throws SQLException {
        NotaSerie nf = new NotaSerie();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select codori, codpro from e075pro where codemp = 1 and codpro = ?"
                + "  ";

        System.out.println("br.com.integrador.dao.NotaSerieDAO.getNotaSeries()\n" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            pst.setString(1, ph.getProduto());

            rs = pst.executeQuery();

            if (rs.next()) {
                nf.setProduto(rs.getString("codpro"));
                nf.setOrigem(rs.getString("codori"));

                atualizarOrigemHub(nf);
            }

            pst.close();
            rs.close();

        } catch (Exception ex) {
            System.out.println("br.com.integrador.dao.NotaSerieDAO.getNotaSeries()\n" + ex.toString());

        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return nf;
    }

    public boolean atualizarOrigemHub(NotaSerie ph) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update notaserie set linha = ? \n"
                + " where produto = ? ";

        try {
            openConnectionMySql();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, ph.getOrigem());
            pst.setString(2, ph.getProduto());

            pst.executeUpdate();
            System.out.println("br.com.integrador.dao.NotaSerieDAO.atualizarNotaSerieIntegrada()\n Nota integrada no APP");

            pst.close();

            return true;
        } catch (Exception ex) {
            System.out.print(" erro " + ex.toString());
            JOptionPane.showMessageDialog(null, "ERRO " + ex,
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

}
