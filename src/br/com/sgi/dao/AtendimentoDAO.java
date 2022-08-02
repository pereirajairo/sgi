/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Atendimento;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Motivo;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceAtendimentoDAO;
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
public class AtendimentoDAO implements InterfaceAtendimentoDAO<Atendimento> {

    private Connection con;
    private UtilDatas utilDatas;

 

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    @Override
    public boolean remover(Atendimento t) throws SQLException {
        return true;
    }

    @Override
    public boolean alterar(Atendimento t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update  e085obs set \n"
                + "tipobs=?,obscli=?,obsusu=?, obsdat=?,        \n"
                + "obshor=?,solobs=?,solusu=?,soldat=?, solhor=?,\n"
                + "datprx=?,seqcto=?,sitobs=?,indexp=?, motobs=?,\n"
                + "solmot=?,datvis=?,seqvis=?,cliori=?, seqori=?,\n"
                + "empped=?,filped=?,numped=?,seqipd=?, varser=?,\n"
                + "usu_codemp=?,usu_codtpr=?, usu_obscli=?,usu_conate=?,usu_propas=?,\n"
                + "usu_obsate=?,usu_envema=?,usu_emapar=?, usu_sitcli=?, usu_autmot=? \n"
                + " where codcli=? \n"
                + "   and seqobs=? ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Atendimento alterado com sucesso",
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
    public boolean inserir(Atendimento t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into e085obs \n"
                + "(tipobs,obscli,obsusu, obsdat,\n"
                + "obshor,solobs,solusu,soldat, solhor,\n"
                + "datprx,seqcto,sitobs,indexp, motobs,\n"
                + "solmot,datvis,seqvis,cliori, seqori,\n"
                + "empped,filped,numped,seqipd, varser,\n"
                + "usu_codemp,usu_codtpr, usu_obscli,usu_conate,usu_propas,\n"
                + "usu_obsate,usu_envema,usu_emapar,usu_sitcli, usu_autmot,codcli, seqobs) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?,?,?,?,?,\n"
                + " ?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "SUCESSO: Atendimento registrado com sucesso",
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
    public List<Atendimento> getAtendimentos(String PESQUISAR_POR, String PESQUISA) throws SQLException {
        List<Atendimento> resultado = new ArrayList<Atendimento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select cli.nomcli, cli.cidcli, cli.sigufs, obs.*, upper(mot.desmot) desmot\n"
                + "  from e085cli cli, e085obs obs\n"
                + "  left join e021mot mot\n"
                + "    on (mot.codmot = obs.motobs)\n"
                + " where 1 = 1"
                + " and obs.obsdat >= '18/02/2021'\n "
                + " and obs.codcli = cli.codcli "
                + " and obs.usu_propas >0";
        sqlSelect += PESQUISA;

        sqlSelect += "order by seqobs desc";

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
    public Atendimento getAtendimento(String PESQUISAR_POR, String PESQUISA) throws SQLException {

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;
        String sqlSelect = " select cli.nomcli, cli.cidcli, cli.sigufs, obs.*, upper(mot.desmot) desmot \n"
                + "  from e085cli cli, e085obs obs\n"
                + "  left join e021mot mot\n"
                + "    on (mot.codmot = obs.motobs)\n"
                + " where 1 = 1"
                + " and obs.obsdat >= '01/06/2020'\n "
                + " and obs.codcli = cli.codcli";

        sqlSelect += PESQUISA;
        sqlSelect += " order by seqobs desc";
        
        System.out.println("br.com.recebimento.dao.AtendimentoDAO.getAtendimento()\n"+sqlSelect);
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            List<Atendimento> resultado = new ArrayList<Atendimento>();
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

    private void setPreparedStatement(Atendimento t,
            java.sql.PreparedStatement pst, String acao) throws SQLException {

        pst.setString(1, "M");
        pst.setString(2, t.getObservacao());
        pst.setInt(3, t.getLancamentousuario());

        pst.setDate(4, new Date(t.getLancamentodata().getTime()));
        pst.setInt(5, t.getLancamentohora());
        pst.setString(6, t.getSolucao());
        pst.setInt(7, t.getSolucaousuario());
        if (t.getSolucaodata() == null) {
            pst.setDate(8, null);
        } else {
            pst.setDate(8, new Date(t.getSolucaodata().getTime()));
        }
        pst.setInt(9, t.getSolucaohora());
        if (t.getDataproximavisita() == null) {
            pst.setDate(10, null);
        } else {
            pst.setDate(10, new Date(t.getDataproximavisita().getTime()));
        }
        pst.setInt(11, t.getSequenciacontato());
        pst.setString(12, t.getSituacaoobservacao());
        pst.setInt(13, t.getIndexp());

        pst.setInt(14, t.getMotivoobservacao());
        pst.setInt(15, t.getSolucaomotivo());
        if (t.getVisitadata() == null) {
            pst.setDate(16, null);
        } else {
            pst.setDate(16, new Date(t.getVisitadata().getTime()));
        }
        pst.setInt(17, t.getVisitasequencia());

        pst.setInt(18, t.getOrigemcliente());

        pst.setInt(19, t.getOrigemsequencia());
        pst.setInt(20, t.getEmpresacliente());
        pst.setInt(21, t.getPedidofilial());
        pst.setInt(22, t.getPedido());
        pst.setInt(23, t.getSequenciaitempedido());

        pst.setString(24, t.getVarser());
        pst.setInt(25, 0);
        pst.setInt(26, 0);
        pst.setString(27, t.getObservacaodetalhada());
        pst.setString(28, t.getContatoobservacao());

        pst.setInt(29, t.getProximopasso());
        pst.setString(30, t.getOutramarcas());
        pst.setString(31, t.getEnviarEmail());
        pst.setString(32, t.getEmail());
        if(t.getSituacao().equals("PRE-INATIVO")){
            t.setSituacao("PREINATIVO");
        }
        pst.setString(33, t.getSituacao());
        pst.setString(34, t.getAutomoto());
        
        pst.setInt(35, t.getCodigocliente());
        pst.setInt(36, t.getSequencialancamento());
    }

    private List<Atendimento> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Atendimento> resultado = new ArrayList<Atendimento>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Atendimento a = new Atendimento();
            a.setEnviarEmail(rs.getString("usu_envema"));
            a.setEmail(rs.getString("usu_emapar"));

            a.setCodigocliente(rs.getInt("codcli"));
            a.setTabelapreco(rs.getString("usu_codtpr"));
            a.setConcorrecia("");
            a.setContatoobservacao(rs.getString("usu_conate"));
            a.setDataproximavisita(rs.getDate("datprx"));
            if (a.getDataproximavisita() != null) {
                a.setDatavista(this.utilDatas.converterDateToStr(a.getDataproximavisita()));
            }

            a.setEmail(rs.getString("usu_emapar"));
            a.setEmpresacliente(rs.getInt("usu_codemp"));
            a.setEnviarEmail(rs.getString("usu_envema"));
            a.setLancamentodata(rs.getDate("obsdat"));
            if (a.getLancamentodata() != null) {
                a.setDatalancamento(this.utilDatas.converterDateToStr(a.getLancamentodata()));
            }
            a.setLancamentohora(rs.getInt("obshor"));
            a.setLancamentousuario(rs.getInt("obsusu"));
            a.setMotivoobservacao(rs.getInt("motobs"));
            a.setObservacao(rs.getString("obscli"));
            a.setObservacaodetalhada(rs.getString("usu_obscli"));
            a.setObservacaotipo(rs.getString("tipobs"));
            a.setOrigemcliente(rs.getInt("cliori"));
            a.setOrigemsequencia(rs.getInt("seqori"));
            a.setOutramarcas(rs.getString("usu_obsate"));
            a.setPedido(rs.getInt("numped"));
            a.setPedidoempresa(rs.getInt("empped"));
            a.setPedidofilial(rs.getInt("filped"));
            a.setProximopasso(rs.getInt("usu_propas"));
            a.setSequenciacontato(rs.getInt("seqcto"));
            a.setSequenciaitempedido(rs.getInt("seqipd"));
            a.setSequencialancamento(rs.getInt("seqobs"));
            a.setSituacaoobservacao(rs.getString("sitobs"));
            a.setSolucao(rs.getString("solobs"));
            a.setSolucaodata(rs.getDate("soldat"));
            a.setSolucaousuario(rs.getInt("solusu"));
            a.setSolucaomotivo(rs.getInt("solmot"));
            a.setVarser(rs.getString("varser"));
            a.setVisitadata(rs.getDate("datvis"));
            a.setVisitasequencia(rs.getInt("seqvis"));
            Motivo mot = new Motivo();
            mot.setCodigo(a.getMotivoobservacao());
            mot.setDescricao(rs.getString("desmot"));
            a.setMotivo(mot);

            Cliente cli = new Cliente();
            cli.setCodigo_cliente(a.getCodigocliente());
            cli.setNome(rs.getString("nomcli"));
            a.setSituacao(rs.getString("usu_sitcli"));
            a.setAutomoto(rs.getString("usu_autmot"));
            a.setCliente(cli);
            resultado.add(a);

        }
        return resultado;
    }

    public int proxCodCad(Integer cliente) throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(seqobs) + 1), 1) seqobs FROM e085obs where codcli=" + cliente;

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
    public boolean gravarSolucao(Atendimento t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "update  e085obs set\n"
                + " solobs=?,solusu=?,soldat=?, solhor=?, solmot=?, sitobs='R', numped=? \n"
                + " where codcli = ? \n"
                + " and seqobs = ?";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, t.getSolucao());
            pst.setInt(2, t.getSolucaousuario());
            pst.setDate(3, new Date(t.getSolucaodata().getTime()));
            pst.setInt(4, t.getSolucaohora());
            pst.setInt(5, t.getSolucaomotivo());
            pst.setInt(6, t.getPedido());
            pst.setInt(7, t.getCodigocliente());

            pst.setInt(8, t.getSequencialancamento());

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
}
