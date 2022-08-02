/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.Pedido;
import br.com.sgi.bean.Produto;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.sgi.util.UtilDatas;
import br.com.sgi.interfaces.InterfacePedidoDAO;
import br.com.sgi.util.FormatarNumeros;
import java.sql.PreparedStatement;
import java.text.ParseException;

/**
 *
 * @author jairosilva
 */
public class PedidoDAO implements InterfacePedidoDAO<Pedido> {

    private Connection con;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private UtilDatas utilDatas;

    @Override
    public List<Pedido> getPedidos(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select ped.*,\n"
                + "       case usu_datlib\n"
                + "         when to_date('31/12/1900', 'dd/mm/yyyy') then\n"
                + "          to_date(sysdate, 'dd/mm/yyyy') - to_date(usu_datblo, 'dd/mm/yyyy')\n"
                + "         else\n"
                + "          to_date(usu_datlib, 'dd/mm/yyyy') -\n"
                + "          to_date(usu_datblo, 'dd/mm/yyyy')\n"
                + "       end as dias_logistica,\n"
                + "       cli.nomcli,\n"
                + "       cli.sigufs,\n"
                + "       cli.cidcli,\n"
                + "       pedsis.datemi,\n"
                + "       pedsis.datfec,\n"
                + "       pedsis.usu_datage,\n"
                + "       case datfec\n"
                + "         when to_date('31/12/1900', 'dd/mm/yyyy') then\n"
                + "          0\n"
                + "         else\n"
                + "          to_date(datfec, 'dd/mm/yyyy') - to_date(datemi, 'dd/mm/yyyy')\n"
                + "       end as dias_fechamento,\n"
                + "       pedsis.sitped,\n"
                + "       pedsis.codtra,\n"
                + "       pedsis.tnspro,\n"
                + "       pedsis.usu_libmin,\n"
                + "       pedsis.usu_lansuc,\n"
                + "       tra.nomtra, "
                + "(select max(numnfv)\n"
                + "          from e140ipv ipv\n"
                + "         where ipv.codemp = ped.usu_codemp\n"
                + "           and ipv.codfil = ped.usu_codfil\n"
                + "           and ipv.numped = ped.usu_numped) as numnfv"
                + "  from e085cli cli, usu_tpedbol ped\n"
                + "          left join e120ped pedsis\n"
                + "            on (pedsis.codemp = ped.usu_codemp and\n"
                + "               pedsis.codfil = ped.usu_codlan and\n"
                + "               pedsis.numped = ped.usu_numped and\n"
                + "               pedsis.codcli = ped.usu_codcli)\n"
                + "  left join e073tra tra\n"
                + "    on tra.codtra = pedsis.codtra\n"
                + "         where 0 = 0\n"
                + "           and ped.usu_codcli = cli.codcli\n"
                + "  \n";

        sqlSelect = "select ped.*,\n"
                + "       case usu_datlib\n"
                + "         when to_date('31/12/1900', 'dd/mm/yyyy') then\n"
                + "          to_date(sysdate, 'dd/mm/yyyy') - to_date(usu_datblo, 'dd/mm/yyyy')\n"
                + "         else\n"
                + "          to_date(usu_datlib, 'dd/mm/yyyy') -\n"
                + "          to_date(usu_datblo, 'dd/mm/yyyy')\n"
                + "       end as dias_logistica,\n"
                + "       cli.nomcli,\n"
                + "       cli.sigufs,\n"
                + "       cli.cidcli,\n"
                + "       pedsis.datemi,\n"
                + "       pedsis.datfec,\n"
                + "       pedsis.usu_datage,\n"
                + "       case datfec\n"
                + "         when to_date('31/12/1900', 'dd/mm/yyyy') then\n"
                + "          0\n"
                + "         else\n"
                + "          to_date(datfec, 'dd/mm/yyyy') - to_date(datemi, 'dd/mm/yyyy')\n"
                + "       end as dias_fechamento,\n"
                + "       pedsis.sitped,\n"
                + "       pedsis.codtra,\n"
                + "       pedsis.tnspro,\n"
                + "       pedsis.usu_libmin,\n"
                + "       pedsis.usu_lansuc,\n"
                + "       tra.nomtra, "
                + "(select max(numnfv)\n"
                + "          from e140ipv ipv\n"
                + "         where ipv.codemp = ped.usu_codemp\n"
                + "           and ipv.codfil = ped.usu_codfil\n"
                + "           and ipv.numped = ped.usu_numped) as numnfv"
                + "  from e085cli cli, usu_tpedbol ped\n"
                + "          left join e120ped pedsis\n"
                + "            on (pedsis.codemp = ped.usu_codemp and pedsis.codfil = ped.usu_codlan and\n"
                + "       pedsis.numped = ped.usu_numped and pedsis.codcli = ped.usu_codcli)\n"
                + "  left join e073tra tra\n"
                + "    on tra.codtra = pedsis.codtra\n"
                + "         where 0 = 0\n"
                + "           and ped.usu_codcli = cli.codcli\n"
                + "  \n";

        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }

        System.out.print(" " + sqlSelect);
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
    public Pedido getPedido(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select ped.*,\n"
                + "       case usu_datlib\n"
                + "         when to_date('31/12/1900', 'dd/mm/yyyy') then\n"
                + "          to_date(sysdate, 'dd/mm/yyyy') - to_date(usu_datblo, 'dd/mm/yyyy')\n"
                + "         else\n"
                + "          to_date(usu_datlib, 'dd/mm/yyyy') -\n"
                + "          to_date(usu_datblo, 'dd/mm/yyyy')\n"
                + "       end as dias_logistica,\n"
                + "       cli.nomcli,\n"
                + "       cli.sigufs,\n"
                + "       cli.cidcli,\n"
                + "       pedsis.datemi,\n"
                + "       pedsis.datfec,\n"
                + "       pedsis.usu_datage,\n"
                + "       pedsis.usu_libmin,\n"
                + "       pedsis.usu_lansuc,\n"
                + "       case datfec\n"
                + "         when to_date('31/12/1900', 'dd/mm/yyyy') then\n"
                + "          0\n"
                + "         else\n"
                + "          to_date(datfec, 'dd/mm/yyyy') - to_date(datemi, 'dd/mm/yyyy')\n"
                + "       end as dias_fechamento,\n"
                + "     pedsis.codtra,\n"
                + "       pedsis.tnspro,\n"
                + "     pedsis.sitped,\n"
                + "       tra.nomtra,"
                + "(select max(numnfv)\n"
                + "          from e140ipv ipv\n"
                + "         where ipv.codemp = ped.usu_codemp\n"
                + "           and ipv.codfil = ped.usu_codfil\n"
                + "           and ipv.numped = ped.usu_numped) as numnfv"
                + "  from e085cli cli, usu_tpedbol ped\n"
                + "          left join e120ped pedsis\n"
                + "            on (pedsis.codemp = ped.usu_codemp and\n"
                + "               pedsis.codfil = ped.usu_codfil and\n"
                + "               pedsis.numped = ped.usu_numped and\n"
                + "               pedsis.codcli = ped.usu_codcli)\n"
                + " left join e073tra tra\n"
                + "    on tra.codtra = pedsis.codtra"
                + "         where 0 = 0\n"
                + "           and ped.usu_codcli = cli.codcli\n"
                + "  \n";
        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }

        System.out.print(" " + sqlSelect);

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

    private List<Pedido> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Pedido e = new Pedido();
            e.setEmpresa(rs.getInt("usu_codemp"));
            e.setFilial(rs.getInt("usu_codfil"));
            e.setCliente(rs.getInt("usu_codcli"));
            e.setPedido(rs.getInt("usu_numped"));
            e.setEmissao(null);
            e.setNota(rs.getInt("numnfv"));
            if (e.getNota() == null) {
                e.setNota(0);
            }
            e.setSucata_id(rs.getInt("usu_lansuc"));
            e.setSituacaoPedido(rs.getString("sitped"));

            if (e.getSituacaoPedido() != null) {

                switch (e.getSituacaoPedido()) {
                    case "1":
                        e.setSituacaoPedido("NÃO FATURADO");
                        break;
                    case "4":
                        e.setSituacaoPedido("FATURADO");
                        break;
                    case "5":
                        e.setSituacaoPedido("CANCELADO");
                        break;
                    default:
                        e.setSituacaoPedido("OUTROS");
                        break;
                }
            } else {
                e.setSituacaoPedido("OUTROS");
            }
            e.setLiberarMinuta(rs.getString("usu_libmin"));
            if (e.getLiberarMinuta() == null) {
                e.setLiberarMinuta("N");
            }

            e.setDataliberacao(rs.getDate("usu_datlib"));
            if (e.getDataliberacao() != null) {
                e.setDataliberacaoS(this.utilDatas.converterDateToStr(e.getDataliberacao()));
            }

            e.setTransacao(rs.getString("tnspro"));
            e.setDatabloqueio(rs.getDate("usu_datblo"));
            e.setDatabloqueioS(this.utilDatas.converterDateToStr(e.getDatabloqueio()));
            e.setSituacaoLogistica(rs.getString("usu_sitlib"));
            e.setObservacaobloqueio(rs.getString("usu_obsblo"));
            e.setObservacaoliberacao(rs.getString("usu_obslib"));
            e.setUsuario(rs.getInt("usu_usublo"));
            e.setUsuarioliberacao(rs.getInt("usu_usulib"));
            e.setLinha(rs.getString("usu_autmot"));
            e.setPeso(rs.getDouble("usu_pesped"));
            e.setQuantidade(rs.getDouble("usu_qtdped"));
            e.setEmailpara(rs.getString("usu_emapar"));
            e.setEnviaremail(rs.getString("usu_envema"));
            e.setQuantidadedias(rs.getDouble("dias_logistica"));
            e.setCadCliente(new Cliente(e.getCliente(), rs.getString("nomcli"), rs.getString("sigufs"), rs.getString("cidcli")));

            e.setDataAgendamento(rs.getDate("usu_datage"));
            if (e.getDataAgendamento() != null) {
                e.setDataAgendamentoS(this.utilDatas.converterDateToStr(e.getDataAgendamento()));
            }
            e.setEmissao(rs.getDate("datemi"));
            if (e.getEmissao() != null) {
                e.setEmissaoS(this.utilDatas.converterDateToStr(e.getEmissao()));
            }
            e.setQuantidadediascomercial(rs.getDouble("dias_fechamento"));
            e.setCodigoTransportadora(rs.getInt("codtra"));
            e.setCadTransportadora(new Transportadora(e.getCodigoTransportadora(), rs.getString("nomtra")));

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean liberarPedido(Pedido t) throws SQLException {
        PreparedStatement pst = null;

        try {

//            String sqlInsert = "UPDATE usu_tpedbol  SET   \n"
//                    + " usu_sitlib=?,  \n"
//                    + " usu_datlib=?,  \n"
//                    + " usu_obsblo=?,"
//                    + " usu_usulib=?    \n"
//                    + " WHERE  usu_codemp= ? "
//                    + " AND    usu_codfil= ?"
//                    + " AND    usu_numped=?";
            String sqlInsert = "UPDATE usu_tpedbol  SET   \n"
                    + " usu_sitlib=?,  \n"
                    + " usu_datlib=?,  \n"
                    + " usu_obsblo=?,"
                    + " usu_usulib=?    \n"
                    + " WHERE  usu_codemp= ? "
                    + " AND    usu_codcli= ?"
                    + " AND    usu_numped=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, t.getSituacaoLogistica());
            if (t.getDataliberacao() != null) {
                pst.setDate(2, new java.sql.Date(t.getDataliberacao().getTime()));
            } else {
                pst.setDate(2, null);
            }

            pst.setString(3, t.getObservacaobloqueio());
            pst.setInt(4, t.getUsuario());
            pst.setInt(5, t.getEmpresa());
            pst.setInt(6, t.getCliente());
            pst.setInt(7, t.getPedido());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro alterado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }

    }

    @Override
    public List<Pedido> getPedidosExpedicao(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT MAX(E135PES.DATPRP) AS DATPRP,\n"
                + "       MAX(FIL.usu_diatra) AS DIA_TRANSPORTE,\n"
                + "       MAX(E135PES.DATPRP) + (MAX(FIL.usu_diatra)+1) AS DATA_PARA_FATURAR,\n"
                + "       SYSDATE - MAX(E135PES.DATPRP) AS QTDDIA_SEPARACAO_HOJE,\n"
                + "       MAX(E135PES.DATPRP) - MAX(E120PED.DATEMI) AS QTDDIA_SEPARACAO_EMISSAO,\n"
                + "       SYSDATE -MAX(E120PED.DATEMI) AS QTDDIA_EMISSAO_HOJE,\n"
                + "       SYSDATE -(MAX(E135PES.DATPRP) +(MAX(FIL.usu_diatra)+1)) AS QTDDIA_ATRAZO,\n"
                + "       SUM(E135PES.PESLIQ) AS PESLIQ,\n"
                + "       SUM(E135PES.QTDPED) AS QTDPED,\n"
                + "       MAX(E120PED.CODEMP) AS CODEMP,\n"
                + "       MAX(E120PED.CODFIL) AS CODFIL,\n"
                + "       MAX(E120PED.NUMPED) AS NUMPED,\n"
                + "       MAX(E120PED.CODCLI) AS CODCLI,\n"
                + "       MAX(E120PED.TNSPRO) AS TNSPRO,\n"
                + "       MAX(E120PED.SITPED) AS SITPED,\n"
                + "       MAX(E120PED.PEDCLI) AS PEDCLI,\n"
                + "       MAX(E120PED.DATEMI) AS DATEMI,\n"
                + "       MAX(E135PES.SITPES) AS SITPES,\n"
                + "       MAX(E135PES.NUMPFA) AS NUMPFA,\n"
                + "       MAX(E135PES.NUMANE) AS NUMANE,\n"
                + "       MAX(E120PED.PEDBLO) AS PEDBLO,\n"
                + "       MAX(E120PED.CODTRA) AS CODTRA,\n"
                + "        MAX(E120PED.USU_DATAGE) AS DATAGE,\n"
                + "       MAX(CLI.NOMCLI) AS NOMCLI,\n"
                + "       MAX(CLI.SIGUFS) AS SIGUFS,\n"
                + "       MAX(CLI.CIDCLI) AS CIDCLI,\n"
                + "       MAX(TRA.NOMTRA) AS NOMTRA,\n"
                + "       MAX(E075PRO.CODORI) AS CODORI,"
                + "        MAX(E120PED.USU_LIBMIN) AS LIBMIN\n"
                + "\n"
                + "  FROM E120PED\n"
                + "  LEFT JOIN E135PES\n"
                + "    ON E135PES.CODEMP = E120PED.CODEMP\n"
                + "   AND E135PES.CODFIL = E120PED.CODFIL\n"
                + "   AND E135PES.NUMPED = E120PED.NUMPED\n"
                + "  LEFT JOIN E085CLI CLI\n"
                + "    ON (CLI.CODCLI = E120PED.CODCLI)\n"
                + "  LEFT JOIN E073TRA TRA\n"
                + "    ON (TRA.CODTRA = E120PED.CODTRA)\n"
                + "  LEFT JOIN E070FIL FIL\n"
                + "    ON (FIL.CODEMP = E120PED.CODEMP AND FIL.CODFIL = E120PED.CODFIL)\n"
                + "  LEFT JOIN E075PRO\n"
                + "    ON E135PES.CODEMP = E075PRO.CODEMP\n"
                + "   AND E075PRO.CODPRO = E135PES.CODPRO\n"
                + " WHERE E135PES.SITPES in (2,3)\n"
                + "   AND E120PED.CODFIL NOT IN (10, 11, 12, 13, 14, 15,90)\n"
                + "   AND E135PES.DATPRP >= '01/01/2021'\n";

        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }
        sqlSelect += "\nGROUP BY E120PED.CODCLI, E120PED.NUMPED \n ORDER BY E120PED.CODCLI, E120PED.NUMPED";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSapiens(rs);

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

    private List<Pedido> getListaSapiens(ResultSet rs) throws SQLException, ParseException {
        List<Pedido> resultado = new ArrayList<Pedido>();
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            Pedido e = new Pedido();
            e.setPedidobloqueado(rs.getString("pedblo"));
            e.setEmpresa(rs.getInt("codemp"));
            e.setFilial(rs.getInt("codfil"));
            e.setCliente(rs.getInt("codcli"));
            e.setPedido(rs.getInt("numped"));
            e.setEmissao(rs.getDate("datemi"));
            e.setPeso(rs.getDouble("pesliq"));
            e.setQuantidade(rs.getDouble("qtdped"));
            e.setCadCliente(new Cliente(e.getCliente(), rs.getString("nomcli"), rs.getString("sigufs"), rs.getString("cidcli")));
            e.setCodigoTransportadora(rs.getInt("codtra"));
            e.setCadTransportadora(new Transportadora(e.getCodigoTransportadora(), rs.getString("nomtra")));
            e.setLinha(rs.getString("codori"));
            e.setTipodpedido("VEN");
            e.setTipodocumento("NF");
            e.setTransacao(rs.getString("tnspro"));
            switch (e.getTransacao()) {
                case "90126":
                    e.setTipodocumento("R");
                    e.setTipodpedido("MKT");

                    break;
                case "90112":
                    e.setTipodocumento("R");
                    e.setTipodpedido("GAR");
                    break;
                case "90113":
                    e.setTipodocumento("NG");
                    e.setTipodpedido("GAR");
                    break;

                case "90122":
                    e.setTipodocumento("R");
                    e.setTipodpedido("GAR");
                    break;
                case "90123":
                    e.setTipodocumento("NG");
                    e.setTipodpedido("GAR");
                    break;
                case "90124":
                    e.setTipodocumento("NF");
                    e.setTipodpedido("VEN");
                    break;
                default:
                    break;
            }

            e.setSituacaopre(rs.getString("SITPES"));
            if (e.getSituacaopre() == null) {
                e.setSituacaopre("0");
            }

            e.setLiberarMinuta(rs.getString("libmin"));

            e.setData_para_faturar(rs.getDate("data_para_faturar"));
            if (e.getData_para_faturar()
                    != null) {
                e.setData_para_faturarS(this.utilDatas.converterDateToStr(e.getData_para_faturar()));
            }

            e.setDataSeparacao(rs.getDate("datprp"));
            if (e.getDataSeparacao()
                    != null) {
                e.setDataSeparacaoS(this.utilDatas.converterDateToStr(e.getDataSeparacao()));
            }

            e.setEmissao(rs.getDate("datemi"));
            if (e.getEmissao()
                    != null) {
                e.setEmissaoS(this.utilDatas.converterDateToStr(e.getEmissao()));
            }

            e.setDataAgendamento(rs.getDate("datage"));
            if (e.getDataAgendamento() != null) {
                e.setDataAgendamentoS(this.utilDatas.converterDateToStr(e.getDataAgendamento()));
                if (e.getDataAgendamentoS().equals("31/12/1900")) {
                    e.setDataAgendamentoS("");
                }
            }

            e.setDia_transporte(rs.getInt("dia_transporte"));
            e.setQtddia_emissao_hoje(rs.getDouble("qtddia_emissao_hoje"));
            e.setQtddia_separacao_emissao(rs.getDouble("qtddia_separacao_emissao"));
            e.setQtddia_emissao_hoje(rs.getDouble("qtddia_emissao_hoje"));
            e.setQtddia_atrazo(rs.getDouble("qtddia_atrazo"));
            e.setQtddia_atrazo(FormatarNumeros.converterDoubleDoisDecimais(e.getQtddia_atrazo()));
            e.setNumeropre(rs.getInt("numpfa"));
            e.setNumeroanalise(rs.getInt("numane"));

            if (e.getTransacao().equals("90124")) {
                e.setLinha("METAIS");
                e.setPercentualRentabilidade(rs.getDouble("perren"));
                e.setPesoRentabilidade(rs.getDouble("pesren"));
            }

            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean AgendarDataPedido(Pedido t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE e120ped  SET   \n"
                    + " usu_datage=?   \n"
                    + " WHERE  codemp= ? "
                    + " AND    codcli= ?"
                    + " AND    numped=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            if (t.getDatagendada() != null) {
                pst.setDate(1, new java.sql.Date(t.getDatagendada().getTime()));
            } else {
                pst.setDate(1, null);
            }

            pst.setInt(2, t.getEmpresa());
            pst.setInt(3, t.getCliente());
            pst.setInt(4, t.getPedido());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data agendada   com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    public boolean AgendarDataPedidoNew(Pedido t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE e120ped  SET   \n"
                    + " usu_datage=?   \n"
                    + " WHERE     codcli= ?"
                    + " AND    numped=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            if (t.getDatagendada() != null) {
                pst.setDate(1, new java.sql.Date(t.getDatagendada().getTime()));
            } else {
                pst.setDate(1, null);
            }

            pst.setInt(2, t.getCliente());
            pst.setInt(3, t.getPedido());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Data agendada   com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public boolean AtualizarMinuta(Pedido t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE e120ped  SET   \n"
                    + " usu_libmin=?,   \n"
                    + " usu_codmin=?"
                    + " WHERE  codemp= ? "
                    + " AND    codcli= ?"
                    + " AND    numped=?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, "S");
            pst.setInt(2, t.getCodigominuta());
            pst.setInt(3, t.getEmpresa());
            pst.setInt(4, t.getCliente());
            pst.setInt(5, t.getPedido());
            pst.executeUpdate();

//            JOptionPane.showMessageDialog(null, "Data agendada   com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<Pedido> getPedidosExpedicaoSemPre(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT NULL AS DATPRP,\n"
                + "       MAX(FIL.usu_diatra) AS DIA_TRANSPORTE,\n"
                + "       SYSDATE AS DATA_PARA_FATURAR,\n"
                + "       0 QTDDIA_SEPARACAO_HOJE,\n"
                + "       0 AS QTDDIA_SEPARACAO_EMISSAO,\n"
                + "       SYSDATE - MAX(E120PED.DATEMI) AS QTDDIA_EMISSAO_HOJE,\n"
                + "       0 AS QTDDIA_ATRAZO,\n"
                + "       SUM(E075PRO.PESLIQ * E120IPD.QTDPED) AS PESLIQ,\n"
                + "       SUM(E120IPD.QTDPED) AS QTDPED,\n"
                + "       MAX(E120PED.CODEMP) AS CODEMP,\n"
                + "       MAX(E120PED.CODFIL) AS CODFIL,\n"
                + "       MAX(E120PED.NUMPED) AS NUMPED,\n"
                + "       MAX(E120PED.CODCLI) AS CODCLI,\n"
                + "       MAX(E120PED.TNSPRO) AS TNSPRO,\n"
                + "       MAX(E120PED.SITPED) AS SITPED,\n"
                + "       MAX(E120PED.PEDCLI) AS PEDCLI,\n"
                + "       MAX(E120PED.DATEMI) AS DATEMI,\n"
                + "       MAX(E120PED.PEDBLO) AS PEDBLO,\n"
                + "       0 AS SITPES,\n"
                + "       0 AS NUMPFA,\n"
                + "       0 AS NUMANE,\n"
                + "       MAX(E120PED.CODTRA) AS CODTRA,\n"
                + "       MAX(E120PED.USU_DATAGE) AS DATAGE,\n"
                + "       MAX(CLI.NOMCLI) AS NOMCLI,\n"
                + "       MAX(CLI.SIGUFS) AS SIGUFS,\n"
                + "       MAX(CLI.CIDCLI) AS CIDCLI,\n"
                + "       MAX(TRA.NOMTRA) AS NOMTRA,\n"
                + "       MAX(E075PRO.CODORI) AS CODORI,\n"
                + "       MAX(E120PED.USU_LIBMIN) AS LIBMIN\n"
                + "\n"
                + "  FROM E120IPD, E075PRO, E120PED\n"
                + "  LEFT JOIN E085CLI CLI\n"
                + "    ON (CLI.CODCLI = E120PED.CODCLI)\n"
                + "  LEFT JOIN E073TRA TRA\n"
                + "    ON (TRA.CODTRA = E120PED.CODTRA)\n"
                + "  LEFT JOIN E070FIL FIL\n"
                + "    ON (FIL.CODEMP = E120PED.CODEMP AND FIL.CODFIL = E120PED.CODFIL)\n"
                + "\n"
                + " WHERE E120PED.SITPED  in (1,2,9)\n"
                + "   AND E120PED.CODFIL NOT IN (10, 11, 12, 13, 14, 15, 90)\n"
                + "   AND E120PED.DATEMI >= '01/06/2021'\n"
                + "   AND E120PED.CODEMP = E120IPD.CODEMP\n"
                + "   AND E120PED.CODFIL = E120IPD.CODFIL\n"
                + "   AND E120PED.NUMPED = E120IPD.NUMPED\n"
                + "   AND E120PED.TNSPRO IN ('90122', '90123', '90126')\n"
                //      + "   AND E120PED.TNSPRO IN ('90122', '90123')\n"
                + "   AND E075PRO.CODEMP = E120IPD.CODEMP\n"
                + "   AND E075PRO.CODPRO = E120IPD.CODPRO\n"
                + "   and E120PED.usu_libmin not in ('S')\n";
        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }
        sqlSelect += "\nGROUP BY E120PED.CODCLI, E120PED.NUMPED \n ORDER BY E120PED.CODCLI, E120PED.NUMPED";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            resultado = getListaSapiens(rs);

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

    public List<Pedido> getPedidosExpedicaoSemPreMkt(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT NULL AS DATPRP,\n"
                + "       MAX(FIL.usu_diatra) AS DIA_TRANSPORTE,\n"
                + "       SYSDATE AS DATA_PARA_FATURAR,\n"
                + "       0 QTDDIA_SEPARACAO_HOJE,\n"
                + "       0 AS QTDDIA_SEPARACAO_EMISSAO,\n"
                + "       SYSDATE - MAX(E120PED.DATEMI) AS QTDDIA_EMISSAO_HOJE,\n"
                + "       0 AS QTDDIA_ATRAZO,\n"
                + "       SUM(E075PRO.PESLIQ * E120IPD.QTDPED) AS PESLIQ,\n"
                + "       SUM(E120IPD.QTDPED) AS QTDPED,\n"
                + "       MAX(E120PED.CODEMP) AS CODEMP,\n"
                + "       MAX(E120PED.CODFIL) AS CODFIL,\n"
                + "       MAX(E120PED.NUMPED) AS NUMPED,\n"
                + "       MAX(E120PED.CODCLI) AS CODCLI,\n"
                + "       MAX(E120PED.TNSPRO) AS TNSPRO,\n"
                + "       MAX(E120PED.SITPED) AS SITPED,\n"
                + "       MAX(E120PED.PEDCLI) AS PEDCLI,\n"
                + "       MAX(E120PED.DATEMI) AS DATEMI,\n"
                + "       MAX(E120PED.PEDBLO) AS PEDBLO,\n"
                + "       0 AS SITPES,\n"
                + "       0 AS NUMPFA,\n"
                + "       0 AS NUMANE,\n"
                + "       MAX(E120PED.CODTRA) AS CODTRA,\n"
                + "       MAX(E120PED.USU_DATAGE) AS DATAGE,\n"
                + "       MAX(CLI.NOMCLI) AS NOMCLI,\n"
                + "       MAX(CLI.SIGUFS) AS SIGUFS,\n"
                + "       MAX(CLI.CIDCLI) AS CIDCLI,\n"
                + "       MAX(TRA.NOMTRA) AS NOMTRA,\n"
                + "       MAX(E075PRO.CODORI) AS CODORI,\n"
                + "       MAX(E120PED.USU_LIBMIN) AS LIBMIN\n"
                + "\n"
                + "  FROM E120IPD, E075PRO, E120PED\n"
                + "  LEFT JOIN E085CLI CLI\n"
                + "    ON (CLI.CODCLI = E120PED.CODCLI)\n"
                + "  LEFT JOIN E073TRA TRA\n"
                + "    ON (TRA.CODTRA = E120PED.CODTRA)\n"
                + "  LEFT JOIN E070FIL FIL\n"
                + "    ON (FIL.CODEMP = E120PED.CODEMP AND FIL.CODFIL = E120PED.CODFIL)\n"
                + "\n"
                + " WHERE E120PED.SITPED  in (1,2,9)\n"
                + "   AND E120PED.CODFIL NOT IN (10, 11, 12, 13, 14, 15, 90)\n"
                + "   AND E120PED.DATEMI >= '01/01/2021'\n"
                + "   AND E120PED.CODEMP = E120IPD.CODEMP\n"
                + "   AND E120PED.CODFIL = E120IPD.CODFIL\n"
                + "   AND E120PED.NUMPED = E120IPD.NUMPED\n"
                + "   AND E120PED.TNSPRO IN ('90126')\n"
                + "   AND E075PRO.CODEMP = E120IPD.CODEMP\n"
                + "   AND E075PRO.CODPRO = E120IPD.CODPRO\n"
                + "   and E120PED.usu_libmin not in ('S')\n";
        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }
        sqlSelect += "\nGROUP BY E120PED.CODCLI, E120PED.NUMPED \n ORDER BY E120PED.CODCLI, E120PED.NUMPED";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSapiens(rs);

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
    public List<Pedido> getPedidosExpedicaoGeral(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(e120ped.codemp) as codemp,\n"
                + "       max(e120ped.codfil) as codfil,\n"
                + "       max(e120ped.numped) as numped,\n"
                + "       max(e120ped.codcli) as codcli,\n"
                + "       max(e120ped.tnspro) as tnspro,\n"
                + "       max(e120ped.sitped) as sitped,\n"
                + "       max(e120ped.pedcli) as pedcli,\n"
                + "       max(e120ped.datemi) as datemi,\n"
                + "       max(e135pes.sitpes) as sitpes,\n"
                + "       max(e135pes.numpfa) as numpfa,\n"
                + "       max(e135pes.numane) as numane,\n"
                + "       max(e120ped.codtra) as codtra,\n"
                + "       max(e120ped.usu_datage) as datage,\n"
                + "       \n"
                + "       max(e135pes.datprp) as datprp,\n"
                + "       max(fil.usu_diatra) as dia_transporte,\n"
                + "       max(e135pes.datprp) + (max(fil.usu_diatra) + 1) as data_para_faturar,\n"
                + "       sysdate - max(e135pes.datprp) as qtddia_separacao_hoje,\n"
                + "       max(e135pes.datprp) - max(e120ped.datemi) as qtddia_separacao_emissao,\n"
                + "       sysdate - max(e120ped.datemi) as qtddia_emissao_hoje,\n"
                + "       sysdate - (max(e135pes.datprp) + (max(fil.usu_diatra) + 1)) as qtddia_atrazo,\n"
                + "       \n"
                + "       sum(e120ipd.qtdped) as qtdped,\n"
                + "       avg(e075pro.pesliq) as pesoproduto,\n"
                + "       avg(e075pro.pesliq) * sum(e120ipd.qtdped) as pesliq,\n"
                + "       max(e120ipd.seqipd) as seqipd,\n"
                + "       max(e120ipd.codpro) as codpro,\n"
                + "       \n"
                + "       max(cli.nomcli) as nomcli,\n"
                + "       max(cli.sigufs) as sigufs,\n"
                + "       max(cli.cidcli) as cidcli,\n"
                + "       max(tra.nomtra) as nomtra,\n"
                + "       max(e075pro.codori) as codori,\n"
                + "       max(e120ped.usu_libmin) as libmin,\n"
                + "       MAX(E120PED.USU_PERREN) AS PERREN,\n"
                + "       MAX(E120PED.USU_PESREN) AS PESREN"
                + "\n"
                + "  from e120ipd, e075pro, e120ped\n"
                + "  left join e135pes\n"
                + "    on e135pes.codemp = e120ped.codemp\n"
                + "   and e135pes.codfil = e120ped.codfil\n"
                + "   and e135pes.numped = e120ped.numped\n"
                + "  left join e085cli cli\n"
                + "    on (cli.codcli = e120ped.codcli)\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = e120ped.codtra)\n"
                + "  left join e070fil fil\n"
                + "    on (fil.codemp = e120ped.codemp and fil.codfil = e120ped.codfil)\n"
                + "\n"
                + " where 0 = 0\n"
                + "   and e120ped.codfil not in (1, 10, 11, 12, 13, 14, 15, 90)\n"
                + "   and e120ped.sitped in (1, 2, 9)\n"
                + "   and e120ped.codemp = e120ipd.codemp\n"
                + "   and e120ped.codfil = e120ipd.codfil\n"
                + "   and e120ped.numped = e120ipd.numped\n"
                + "   and e120ped.tnspro = e120ipd.tnspro\n"
                + "   and e075pro.codemp = e120ipd.codemp\n"
                + "   and e075pro.codpro = e120ipd.codpro\n"
                + "   and e120ped.tnspro not in ('90170')\n";
        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }
        sqlSelect += "\n group by e120ped.codcli, e120ped.numped\n"
                + " order by e120ped.codcli, e120ped.numped";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSapiens(rs);

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
    public Pedido getPedidoExpedicao(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT MAX(E135PES.DATPRP) AS DATPRP,\n"
                + "       MAX(FIL.usu_diatra) AS DIA_TRANSPORTE,\n"
                + "       MAX(E135PES.DATPRP) + (MAX(FIL.usu_diatra)+1) AS DATA_PARA_FATURAR,\n"
                + "       SYSDATE - MAX(E135PES.DATPRP) AS QTDDIA_SEPARACAO_HOJE,\n"
                + "       MAX(E135PES.DATPRP) - MAX(E120PED.DATEMI) AS QTDDIA_SEPARACAO_EMISSAO,\n"
                + "       SYSDATE -MAX(E120PED.DATEMI) AS QTDDIA_EMISSAO_HOJE,\n"
                + "       SYSDATE -(MAX(E135PES.DATPRP) +(MAX(FIL.usu_diatra)+1)) AS QTDDIA_ATRAZO,\n"
                + "       SUM(E135PES.PESLIQ) AS PESLIQ,\n"
                + "       SUM(E135PES.QTDPED) AS QTDPED,\n"
                + "       MAX(E120PED.CODEMP) AS CODEMP,\n"
                + "       MAX(E120PED.CODFIL) AS CODFIL,\n"
                + "       MAX(E120PED.NUMPED) AS NUMPED,\n"
                + "       MAX(E120PED.CODCLI) AS CODCLI,\n"
                + "       MAX(E120PED.TNSPRO) AS TNSPRO,\n"
                + "       MAX(E120PED.SITPED) AS SITPED,\n"
                + "       MAX(E120PED.PEDCLI) AS PEDCLI,\n"
                + "       MAX(E120PED.DATEMI) AS DATEMI,\n"
                + "       MAX(E135PES.SITPES) AS SITPES,\n"
                + "       MAX(E135PES.NUMPFA) AS NUMPFA,\n"
                + "       MAX(E135PES.NUMANE) AS NUMANE,\n"
                + "       MAX(E120PED.CODTRA) AS CODTRA,\n"
                + "        MAX(E120PED.USU_DATAGE) AS DATAGE,\n"
                + "       MAX(CLI.NOMCLI) AS NOMCLI,\n"
                + "       MAX(CLI.SIGUFS) AS SIGUFS,\n"
                + "       MAX(CLI.CIDCLI) AS CIDCLI,\n"
                + "       MAX(TRA.NOMTRA) AS NOMTRA,\n"
                + "       MAX(E075PRO.CODORI) AS CODORI,"
                + "        MAX(E120PED.USU_LIBMIN) AS LIBMIN\n"
                + "\n"
                + "  FROM E120PED\n"
                + "  LEFT JOIN E135PES\n"
                + "    ON E135PES.CODEMP = E120PED.CODEMP\n"
                + "   AND E135PES.CODFIL = E120PED.CODFIL\n"
                + "   AND E135PES.NUMPED = E120PED.NUMPED\n"
                + "  LEFT JOIN E085CLI CLI\n"
                + "    ON (CLI.CODCLI = E120PED.CODCLI)\n"
                + "  LEFT JOIN E073TRA TRA\n"
                + "    ON (TRA.CODTRA = E120PED.CODTRA)\n"
                + "  LEFT JOIN E070FIL FIL\n"
                + "    ON (FIL.CODEMP = E120PED.CODEMP AND FIL.CODFIL = E120PED.CODFIL)\n"
                + "  LEFT JOIN E075PRO\n"
                + "    ON E135PES.CODEMP = E075PRO.CODEMP\n"
                + "   AND E075PRO.CODPRO = E135PES.CODPRO\n"
                + " WHERE E135PES.SITPES in (2,3)\n"
                + "   AND E120PED.CODFIL NOT IN (10, 11, 12, 13, 14, 15,90)\n"
                + "   AND E135PES.DATPRP >= '01/01/2021'\n";

        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }
        sqlSelect += "\nGROUP BY E120PED.CODCLI, E120PED.NUMPED \n ORDER BY E120PED.CODCLI, E120PED.NUMPED";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSapiens(rs);
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

    @Override
    public List<Pedido> getPedidosGerais(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT MAX(E120PED.CODEMP) AS CODEMP,\n"
                + "       MAX(E120PED.CODFIL) AS CODFIL,\n"
                + "       MAX(E120PED.NUMPED) AS NUMPED,\n"
                + "       MAX(E120PED.CODCLI) AS CODCLI,\n"
                + "       MAX(E120PED.TNSPRO) AS TNSPRO,\n"
                + "       SUM(E120IPD.QTDPED) AS PESLIQ,\n"
                + "       SUM(E120IPD.QTDPED) AS QTDPED,\n"
                + "       \n"
                + "       MAX(E120PED.SITPED) AS SITPED,\n"
                + "       MAX(E120PED.PEDCLI) AS PEDCLI,\n"
                + "       MAX(E120PED.DATEMI) AS DATEMI,\n"
                + "       MAX(E120IPD.SITIPD) AS SITPES,\n"
                + "       MAX(E120PED.DATEMI) AS datprp,\n"
                + "       MAX(E120PED.PEDBLO) AS PEDBLO,\n"
                + "       \n"
                + "       0 AS DIA_TRANSPORTE,\n"
                + "       SYSDATE AS DATA_PARA_FATURAR,\n"
                + "       SYSDATE - MAX(E120PED.DATEMI) AS QTDDIA_SEPARACAO_HOJE,\n"
                + "       0 AS QTDDIA_SEPARACAO_EMISSAO,\n"
                + "       SYSDATE - MAX(E120PED.DATEMI) AS QTDDIA_EMISSAO_HOJE,\n"
                + "       0 AS QTDDIA_ATRAZO,\n"
                + "       0 AS NUMPFA,\n"
                + "       0 AS NUMANE,\n"
                + "       \n"
                + "       MAX(E120PED.CODTRA) AS CODTRA,\n"
                + "       MAX(E120PED.USU_DATAGE) AS DATAGE,\n"
                + "       MAX(CLI.NOMCLI) AS NOMCLI,\n"
                + "       MAX(CLI.SIGUFS) AS SIGUFS,\n"
                + "       MAX(CLI.CIDCLI) AS CIDCLI,\n"
                + "       MAX(TRA.NOMTRA) AS NOMTRA,\n"
                + "       MAX(E075PRO.CODORI) AS CODORI,\n"
                + "       MAX(E120PED.USU_LIBMIN) AS LIBMIN,\n"
                + "       MAX(E120PED.USU_PERREN) AS PERREN,\n"
                + "       MAX(E120PED.USU_PESREN) AS PESREN\n"
                + "  FROM E120IPD, E075PRO, E120PED\n"
                + "\n"
                + "  LEFT JOIN E085CLI CLI\n"
                + "    ON (CLI.CODCLI = E120PED.CODCLI)\n"
                + "  LEFT JOIN E073TRA TRA\n"
                + "    ON (TRA.CODTRA = E120PED.CODTRA)\n"
                + "  LEFT JOIN E070FIL FIL\n"
                + "    ON (FIL.CODEMP = E120PED.CODEMP AND FIL.CODFIL = E120PED.CODFIL)\n"
                + "\n"
                + " WHERE E120PED.SITPED in (1, 2, 9)\n"
                + "   AND E120PED.CODFIL NOT IN (10, 11, 12, 13, 14, 15, 90)\n"
                + "   AND E120IPD.CODEMP = E120PED.CODEMP\n"
                + "   AND E120IPD.CODFIL = E120PED.CODFIL\n"
                + "   AND E120IPD.NUMPED = E120PED.NUMPED\n"
                + "   AND E120IPD.CODEMP = E075PRO.CODEMP\n"
                + "   AND E120IPD.CODPRO = E075PRO.CODPRO\n";

        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }
        sqlSelect += "\nGROUP BY E120PED.CODCLI, E120PED.NUMPED \n ORDER BY E120PED.CODCLI, E120PED.NUMPED";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSapiens(rs);

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
    public boolean removerMinutaPedido(Pedido t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE e120ped  SET   \n"
                    + " usu_libmin=?,   \n"
                    + " usu_codmin=?"
                    + " WHERE  usu_codmin= ?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, "N");
            pst.setInt(2, 0);
            pst.setInt(3, t.getCodigominuta());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Minuta Registro atualizado com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.toString(),
                    "ERRO", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }

    @Override
    public List<Pedido> getPedidosExpedicaoMetais(String PESQUISA, String PESQUISA_POR) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT NULL AS DATPRP,\n"
                + "       MAX(FIL.usu_diatra) AS DIA_TRANSPORTE,\n"
                + "       SYSDATE AS DATA_PARA_FATURAR,\n"
                + "       0 QTDDIA_SEPARACAO_HOJE,\n"
                + "       0 AS QTDDIA_SEPARACAO_EMISSAO,\n"
                + "       SYSDATE - MAX(E120PED.DATEMI) AS QTDDIA_EMISSAO_HOJE,\n"
                + "       0 AS QTDDIA_ATRAZO,\n"
                + "       SUM(E075PRO.PESLIQ * E120IPD.QTDPED) AS PESLIQ,\n"
                + "       SUM(E120IPD.QTDPED) AS QTDPED,\n"
                + "       MAX(E120PED.CODEMP) AS CODEMP,\n"
                + "       MAX(E120PED.CODFIL) AS CODFIL,\n"
                + "       MAX(E120PED.NUMPED) AS NUMPED,\n"
                + "       MAX(E120PED.CODCLI) AS CODCLI,\n"
                + "       MAX(E120PED.TNSPRO) AS TNSPRO,\n"
                + "       MAX(E120PED.SITPED) AS SITPED,\n"
                + "       MAX(E120PED.PEDCLI) AS PEDCLI,\n"
                + "       MAX(E120PED.DATEMI) AS DATEMI,\n"
                + "       MAX(E120PED.PEDBLO) AS PEDBLO,\n"
                + "       0 AS SITPES,\n"
                + "       0 AS NUMPFA,\n"
                + "       0 AS NUMANE,\n"
                + "       MAX(E120PED.CODTRA) AS CODTRA,\n"
                + "       MAX(E120PED.USU_DATAGE) AS DATAGE,\n"
                + "       MAX(CLI.NOMCLI) AS NOMCLI,\n"
                + "       MAX(CLI.SIGUFS) AS SIGUFS,\n"
                + "       MAX(CLI.CIDCLI) AS CIDCLI,\n"
                + "       MAX(TRA.NOMTRA) AS NOMTRA,\n"
                + "       MAX(E075PRO.CODORI) AS CODORI,\n"
                + "       MAX(E120PED.USU_LIBMIN) AS LIBMIN,\n"
                + "       MAX(E120PED.USU_PERREN) AS PERREN,\n"
                + "       MAX(E120PED.USU_PESREN) AS PESREN\n"
                + "\n"
                + "  FROM E120IPD, E075PRO, E120PED\n"
                + "  LEFT JOIN E085CLI CLI\n"
                + "    ON (CLI.CODCLI = E120PED.CODCLI)\n"
                + "  LEFT JOIN E073TRA TRA\n"
                + "    ON (TRA.CODTRA = E120PED.CODTRA)\n"
                + "  LEFT JOIN E070FIL FIL\n"
                + "    ON (FIL.CODEMP = E120PED.CODEMP AND FIL.CODFIL = E120PED.CODFIL)\n"
                + "\n"
                + " WHERE E120PED.SITPED in (1, 2)\n"
                + "   AND E120PED.CODFIL  IN (5)\n"
                + "   AND E120PED.DATEMI >= '01/06/2021'\n"
                + "   AND E120PED.CODEMP = E120IPD.CODEMP\n"
                + "   AND E120PED.CODFIL = E120IPD.CODFIL\n"
                + "   AND E120PED.NUMPED = E120IPD.NUMPED\n"
                + "   AND E120PED.TNSPRO  IN ('90127', '90124')\n"
                + "   AND E075PRO.CODEMP = E120IPD.CODEMP\n"
                + "   AND E075PRO.CODPRO = E120IPD.CODPRO\n"
                + "   and E120PED.usu_libmin not in ('S')\n"
                + "   and e075pro.codori not in ('BA','BM')\n";
        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA_POR;
        }
        sqlSelect += "\nGROUP BY E120PED.CODCLI, E120PED.NUMPED \n ORDER BY E120PED.CODCLI, E120PED.NUMPED";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();
            resultado = getListaSapiens(rs);

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
    public List<Pedido> getPedidosIndustrializacao(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Pedido> resultado = new ArrayList<Pedido>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(ped.codemp) codemp,\n"
                + "       max(ped.codfil) codfil,\n"
                + "       max(ped.numped) numped,\n"
                + "      max(ped.tnspro) tnspro,\n"
                + "      max(ipd.codpro) codpro,\n"
                + "      max(pro.despro) despro,\n"
                + "       max(ped.datemi) datemi,\n"
                + "       max(ped.codcli) codcli,\n"
                + "       max(cli.nomcli) nomcli,\n"
                + "       max(cli.cidcli) cidcli,\n"
                + "       max(cli.sigufs) sigufs,\n"
                + "       sum(ipd.qtdped) qtdped\n"
                + "  from e120ped ped, e120ipd ipd, e085cli cli, e075pro pro\n"
                + " where 0 = 0\n"
                + "   and ped.codemp = ipd.codemp\n"
                + "   and ped.codfil = ipd.codfil\n"
                + "   and ped.numped = ipd.numped\n"
                + "   and ped.datemi >= '01/01/2022'\n"
                + "   and ped.tnspro in ('90124','90127')\n"
                + "   and ped.sitped in (1, 2,9)\n"
                + "   and ped.codemp = 1\n"
                + "   and ped.codcli = cli.codcli \n"
                + "   and ipd.codemp = pro.codemp \n"
                + "   and ipd.codpro = pro.codpro \n";
        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += " \ngroup by ped.numped";
        System.out.print(" " + sqlSelect);
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {
                Pedido ped = new Pedido();
                ped.setEmpresa(rs.getInt("codemp"));
                ped.setFilial(rs.getInt("codfil"));
                ped.setPedido(rs.getInt("numped"));
                ped.setCliente(rs.getInt("codcli"));
                ped.setQuantidade(rs.getDouble("qtdped"));
                ped.setTransacao(rs.getString("tnspro"));
                ped.setEmissao(rs.getDate("datemi"));
                Cliente cli = new Cliente();
                cli.setNome(rs.getString("nomcli"));
                cli.setCidade(rs.getString("cidcli"));
                cli.setEstado(rs.getString("sigufs"));
                ped.setCadCliente(cli);

                ped.setProduto(rs.getString("codpro"));
                Produto pro = new Produto();
                pro.setCodigoproduto(ped.getProduto());
                pro.setDescricaoproduto(rs.getString("despro"));
                ped.setCadProduto(pro);

                resultado.add(ped);
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
}
