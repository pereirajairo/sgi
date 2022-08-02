/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.NotaEntrada;
import br.com.sgi.interfaces.InterfaceSucataMovimentoDAO;
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
public class SucataMovimentoDAO1 implements InterfaceSucataMovimentoDAO<SucataMovimento> {

    private Connection con;
    private UtilDatas utilDatas;

    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }

    private void setPreparedStatement(SucataMovimento e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {
        //usu_codemp,usu_codfil,usu_codcli,usu_numped, 
        pst.setInt(1, e.getEmpresa());
        pst.setInt(2, e.getFilial());
        pst.setInt(3, e.getCliente());
        pst.setInt(4, e.getPedido());
        //,usu_numnfv,usu_numnfc,usu_numocp,usu_codpro, 

        pst.setInt(5, e.getNotasaida());
        pst.setInt(6, e.getNotaentrada());
        pst.setInt(7, e.getOrdemcompra());
        pst.setString(8, e.getProduto());

        //usu_codsnf,usu_pessuc,usu_pesaju,usu_pesmov,usu_qtdped,         
        pst.setString(9, "");
        pst.setDouble(10, e.getPesosucata());
        pst.setDouble(11, e.getPesoajustado());
        pst.setDouble(12, e.getPesomovimento());
        pst.setDouble(13, e.getQuantidade());

        //usu_qtddev,usu_pesdev,usu_debcre,usu_autmot,usu_obsmov, 
        pst.setDouble(14, e.getQuantidadedevolvida());
        pst.setDouble(15, e.getPesodevolvido());
        pst.setString(16, e.getDebitocredito());
        pst.setString(17, e.getAutomoto());
        pst.setString(18, e.getObservacaomovimento());

        //usu_obsace,usu_numtit,usu_codtpt,usu_tipmov,usu_perren,         
        pst.setString(19, e.getObservacaoacerto());
        pst.setString(20, "");
        pst.setString(21, "");
        pst.setString(22, e.getTipomovimento());
        pst.setDouble(23, e.getPercentualrendimento());

        //usu_codusu,usu_datger,usu_datmov,usu_horger,usu_hormov, 
        pst.setInt(24, e.getUsuario());
        pst.setDate(25, new Date(e.getDatageracao().getTime()));
        pst.setDate(26, new Date(e.getDatamovimento().getTime()));
        pst.setInt(27, 0);
        pst.setInt(28, 0);

        //usu_tnspro,usu_gerocp,usu_envema,usu_emapar,usu_pesord,         
        pst.setString(29, "");
        pst.setString(30, e.getGerarordem());
        pst.setString(31, e.getEnviaremail());
        pst.setString(32, e.getEmail());
        pst.setDouble(33, e.getPesoordemcompra());

        //usu_pesrec,usu_pessal,usu_codfilsuc,usu_pesfat,usu_sitsuc, 
        pst.setDouble(34, e.getPesorecebido());
        pst.setDouble(35, e.getPesosaldo());
        pst.setInt(36, e.getFilialsucata());
        pst.setDouble(37, e.getPesofaturado());
        pst.setString(38, "MANUAL");

        //usu_codpes,usu_codmin,usu_codsuc,usu_sitped,usu_pesped                
        pst.setInt(39, 0);
        pst.setInt(40, 0);
        pst.setString(41, e.getSucata());
        pst.setString(42, "");
        pst.setDouble(43, e.getPesopedido());

        //chave
        pst.setInt(44, e.getCodigolancamento());
        pst.setInt(45, e.getSequencia());

    }

    private List<SucataMovimento> getLista(ResultSet rs) throws SQLException, ParseException {
        List<SucataMovimento> resultado = new ArrayList<SucataMovimento>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            SucataMovimento e = new SucataMovimento();

            e.setEmpresa(rs.getInt("usu_codemp"));
            e.setFilial(rs.getInt("usu_codfil"));
            e.setPedido(rs.getInt("usu_numped"));
            e.setCodigolancamento(rs.getInt("usu_codlan")); // Cabeçalho
            e.setSequencia(rs.getInt("usu_seqmov"));

            e.setNotasaida(rs.getInt("usu_numnfv"));
            e.setNotaentrada(rs.getInt("usu_numnfc"));
            e.setOrdemcompra(rs.getInt("usu_numocp"));
            e.setProduto(rs.getString("usu_codpro"));
            e.setSucata(rs.getString("usu_codsuc"));
            e.setSerie(rs.getString("usu_codsnf"));

            e.setPesosucata(rs.getDouble("usu_pessuc"));
            e.setPesosucataS(FormatarPeso.mascaraPorcentagem(e.getPesosucata(), FormatarPeso.PORCENTAGEM));
            e.setPesoajustado(rs.getDouble("usu_pesaju"));
            e.setPesomovimento(rs.getDouble("usu_pesmov"));
            e.setQuantidade(rs.getDouble("usu_qtdped"));
            e.setQuantidadedevolvida(rs.getDouble("usu_qtddev"));
            e.setPesodevolvido(rs.getDouble("usu_pesdev"));
            e.setPesopedido(rs.getDouble("usu_pesped"));
            e.setPercentualrendimento(rs.getDouble("usu_perren"));
            e.setPesoordemcompra(rs.getDouble("usu_pesord"));
            e.setPesorecebido(rs.getDouble("usu_pesrec"));
            e.setPesosaldo(rs.getDouble("usu_pessal"));

            e.setObservacaomovimento(rs.getString("usu_obsmov"));
            e.setObservacaoacerto(rs.getString("usu_obsace"));

            e.setDebitocredito(rs.getString("usu_debcre"));
            e.setMes(rs.getInt("mes"));
            e.setAno(rs.getInt("ano"));
            e.setCliente(rs.getInt("usu_codcli"));

            e.setAutomoto(rs.getString("usu_autmot"));
            e.setUsuario(rs.getInt("usu_codusu"));
            e.setNumerotitulo(rs.getString("usu_numtit"));
            e.setCodigotitulo(rs.getString("usu_codtpt"));
            e.setTipomovimento(rs.getString("usu_tipmov"));

            e.setUsuario(rs.getInt("usu_codusu"));

            e.setDatageracao(rs.getDate("usu_datger"));
            e.setDatageracaoS(this.utilDatas.converterDateToStr(e.getDatageracao()));
            e.setDatamovimento(rs.getDate("usu_datmov"));
            e.setDatamovimentoS(this.utilDatas.converterDateToStr(e.getDatamovimento()));
            e.setHorageracao(rs.getString("usu_horger"));
            e.setHoramovimento(rs.getString("usu_hormov"));

            e.setGerarordem(rs.getString("usu_gerocp"));

            e.setPesofaturado(rs.getDouble("usu_pesfat"));
            e.setFilialsucata(rs.getInt("usu_codfilsuc"));
            e.setSituacao(rs.getString("usu_sitsuc"));
            if (e.getSituacao() == null) {
                e.setSituacao("GERADO");
            }

            e.setCodigopeso(rs.getInt("usu_codpes"));
            e.setCodigominuta(rs.getInt("usu_codmin"));

            //    e.setPesosaldo(e.getPesosucata() - e.getPesorecebido());
            Cliente cli = new Cliente();
            cli.setCodigo(e.getCliente());
            cli.setNome(rs.getString("apecli"));
            cli.setEstado(rs.getString("sigufs"));
            e.setCadCliente(cli);
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public boolean remover(SucataMovimento t) throws SQLException {
        PreparedStatement pst = null;

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement("update usu_tsucmov set    \n"
                    + " usu_debcre = ?,\n"
                    + " usu_obsmov=?   \n "
                    + " WHERE usu_codlan = ?  \n"
                    + " and usu_seqmov=?");
            pst.setString(1, t.getDebitocredito());
            pst.setString(2, t.getObservacaomovimento());
            pst.setInt(3, t.getCodigolancamento());
            pst.setInt(4, t.getSequencia());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro removido com sucesso",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, e.toString());
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
    public boolean gerarOrdem(SucataMovimento t) throws SQLException {

        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE usu_tsucmov SET \n"
                    + " usu_numocp=?, \n"
                    + " usu_pessuc=?, \n"
                    + " usu_gerocp=?, \n"
                    + " usu_envema=?, \n"
                    + " usu_emapar=?, \n"
                    + " usu_pesaju=?, \n"
                    + " usu_pessal=?  \n"
                    + " where usu_codlan=? \n"
                    + " and usu_seqmov = ?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getOrdemcompra());

            pst.setDouble(2, t.getPesosucata());

            pst.setString(3, t.getGerarordem());
            pst.setString(4, t.getEnviaremail());
            pst.setString(5, t.getEmail());
            pst.setDouble(6, t.getPesoajustado());
            pst.setDouble(7, t.getPesosaldo());

            pst.setInt(8, t.getCodigolancamento());
            pst.setInt(9, t.getSequencia());
            pst.executeUpdate();
            pst.close();

//            JOptionPane.showMessageDialog(null, "O Sistema irá geração da Ordem de Compra",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
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
    public boolean alterar(SucataMovimento t) throws SQLException {
        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE usu_tintmin SET \n"
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
    public boolean inserir(SucataMovimento t) throws SQLException {
        PreparedStatement pst = null;

        String sqlInsert = "insert into usu_tsucmov(\n"
                + "usu_codemp,usu_codfil,usu_codcli,usu_numped, \n"
                + "usu_numnfv,usu_numnfc,usu_numocp,usu_codpro, \n"
                + "usu_codsnf,usu_pessuc,usu_pesaju,usu_pesmov,usu_qtdped, \n"
                + "usu_qtddev,usu_pesdev,usu_debcre,usu_autmot,usu_obsmov, \n"
                + "usu_obsace,usu_numtit,usu_codtpt,usu_tipmov,usu_perren, \n"
                + "usu_codusu,usu_datger,usu_datmov,usu_horger,usu_hormov, \n"
                + "usu_tnspro,usu_gerocp,usu_envema,usu_emapar,usu_pesord, \n"
                + "usu_pesrec,usu_pessal,usu_codfilsuc,usu_pesfat,usu_sitsuc, \n"
                + "usu_codpes,usu_codmin,usu_codsuc,usu_sitped,usu_pesped,\n"
                + "usu_codlan,usu_seqmov) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?)";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "Registro inserido com sucesso",
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

    @Override
    public SucataMovimento getSucataMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataMovimento> resultado = new ArrayList<SucataMovimento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select suc.*, to_char(suc.usu_datger, 'mm') as mes, \n"
                + " to_char(suc.usu_datger, 'yyyy') as ano,\n"
                + " cli.apecli apecli, cli.sigufs sigufs\n"
                + "  from usu_tsucmov suc, e085cli cli\n"
                + " where usu_codemp > 0\n"
                + "   and suc.usu_codcli = cli.codcli ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "   order by usu_numocp, usu_debcre ";

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

    public int proxCodCad(Integer id) throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_seqmov) + 1), 1) PROX_CODALAN "
                + " FROM usu_tsucmov where usu_codlan = " + id;

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
    public List<SucataMovimento> getSucatasMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataMovimento> resultado = new ArrayList<SucataMovimento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select suc.*, to_char(suc.usu_datger, 'mm') as mes, \n"
                + " to_char(suc.usu_datger, 'yyyy') as ano,\n"
                + " cli.apecli apecli, cli.sigufs sigufs\n"
                + "  from usu_tsucmov suc, e085cli cli\n"
                + " where usu_codemp > 0\n"
                + "   and suc.usu_codcli = cli.codcli ";

        sqlSelect = "select case usu_debcre\n"
                + "         when '4 - CREDITO' then\n"
                + "          usu_pesrec\n"
                + "         else\n"
                + "          0\n"
                + "       end as peso_sucata_cre,\n"
                + "       \n"
                + "       case usu_debcre\n"
                + "         when '4 - CREDITO' then\n"
                + "          usu_pesrec * (mov.usu_perren / 100)\n"
                + "         else\n"
                + "          0\n"
                + "       end as peso_produto_cre,\n"
                + "       \n"
                + "       case usu_debcre\n"
                + "         when '3 - DEBITO' then\n"
                + "          ((usu_pesfat + usu_pessuc) * -1) +\n"
                + "          (usu_pesfat / (mov.usu_perren) * 100)\n"
                + "         else\n"
                + "          0\n"
                + "       end as peso_sucata_deb,\n"
                + "       \n"
                + "       case usu_autmot\n"
                + "         when 'AUT' then\n"
                + "          usu_pessuc\n"
                + "         else\n"
                + "          0\n"
                + "       end as peso_sucata_auto,\n"
                + "       \n"
                + "       case usu_autmot\n"
                + "         when 'MOT' then\n"
                + "          usu_pessuc\n"
                + "         else\n"
                + "          0\n"
                + "       end as peso_sucata_moto,\n"
                + "       \n"
                + "       mov.*,\n"
                + "       cli.nomcli,\n"
                + "       cli.apecli,\n"
                + "       cli.sigufs,\n"
                + "       cli.cidcli,\n"
                + "       \n"
                + "       case usu_autmot\n"
                + "         when 'IND' then\n"
                + "          'IND.'\n"
                + "         when 'AUT' then\n"
                + "          'AUTO'\n"
                + "         when 'MOT' then\n"
                + "          'MOTO'\n"
                + "       end as usu_autmot,\n"
                + "       usu_codsuc,\n"
                + "       usu_codpro,\n"
                + "       pro.despro as dessuc,\n"
                + "       usu_codpro || ' - ' || pro.despro || '-' || mov.usu_perren as sucata_rentabilidade,\n"
                + "       usu_codpro || ' - ' || pro.despro as sucata_descricao\n"
                + "\n"
                + "  from e085cli cli, usu_tsucmov mov\n"
                + "  left join e075pro pro\n"
                + "    on (mov.usu_codemp = pro.codemp and mov.usu_codsuc = pro.codpro)\n"
                + " where usu_debcre in ('3 - DEBITO', '4 - CREDITO')\n"
                + "   and usu_autmot in ('IND')\n"
                + "   and mov.usu_codcli = cli.codcli\n ";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "   order by mov.usu_codcli, mov.usu_numped, mov.usu_sitsuc, mov.usu_debcre ";

        System.out.println("select movimento" + sqlSelect);

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
    public boolean gerarNota(SucataMovimento t) throws SQLException {

        PreparedStatement pst = null;

        try {

            String sqlInsert = "UPDATE usu_tsucmov SET \n"
                    + " usu_debcre=?, \n"
                    + " usu_pessuc=?, \n"
                    + " usu_gerocp=?, \n"
                    + " usu_envema=?, \n"
                    + " usu_emapar=?, \n"
                    + " usu_pesaju=?, \n"
                    + " usu_pessal=?,  \n"
                    + " usu_sitsuc=?  \n"
                    + " where usu_codlan=? \n"
                    + " and usu_seqmov = ?";

            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);

            pst.setString(1, t.getDebitocredito());

            pst.setDouble(2, t.getPesosucata());

            pst.setString(3, t.getGerarordem());
            pst.setString(4, t.getEnviaremail());
            pst.setString(5, t.getEmail());
            pst.setDouble(6, t.getPesoajustado());
            pst.setDouble(7, t.getPesosaldo());

            pst.setInt(8, t.getCodigolancamento());
            pst.setString(9, t.getSituacao());

            pst.setInt(10, t.getSequencia());
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(null, "O Sistema irá geração da nota complementar",
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
    public List<SucataMovimento> getSucatasMovimentoAgrupado(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<SucataMovimento> resultado = new ArrayList<SucataMovimento>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(suc.usu_codcli) usu_codcli,\n"
                + "       max(suc.usu_numped) usu_numped,\n"
                + "       max(suc.usu_codfil) usu_codfil,\n"
                + "       max(suc.usu_codfilsuc) usu_codfilsuc,\n"
                + "       max(suc.usu_datger) usu_datger,\n"
                + "       max(suc.usu_autmot) usu_autmot,\n"
                + "       max(suc.usu_codemp) usu_codemp,\n"
                + "       max(to_char(suc.usu_datger, 'mm')) as mes,\n"
                + "       max(to_char(suc.usu_datger, 'yyyy')) as ano,\n"
                + "       sum(suc.usu_pesped) as usu_pesped,\n"
                + "       max(cli.apecli) apecli,\n"
                + "       max(cli.sigufs) sigufs\n"
                + "  from usu_tsucmov suc, e085cli cli\n"
                + " where usu_codemp > 0\n"
                + "   and suc.usu_codcli = cli.codcli";

        if (!PESQUISA.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "   group by suc.usu_codcli ";

        System.out.println("select movimento" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();
            this.utilDatas = new UtilDatas();
            while (rs.next()) {
                SucataMovimento e = new SucataMovimento();

                e.setEmpresa(rs.getInt("usu_codemp"));
                e.setFilial(rs.getInt("usu_codfil"));
                e.setFilialsucata(rs.getInt("usu_codfilsuc"));
                e.setPedido(rs.getInt("usu_numped"));

                e.setPesopedido(rs.getDouble("usu_pesped"));

                e.setMes(rs.getInt("mes"));
                e.setAno(rs.getInt("ano"));
                e.setCliente(rs.getInt("usu_codcli"));

                e.setAutomoto(rs.getString("usu_autmot"));

                e.setDatageracao(rs.getDate("usu_datger"));
                e.setDatageracaoS(this.utilDatas.converterDateToStr(e.getDatageracao()));

                Cliente cli = new Cliente();
                cli.setCodigo(e.getCliente());
                cli.setNome(rs.getString("apecli"));
                cli.setEstado(rs.getString("sigufs"));
                e.setCadCliente(cli);
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
    public NotaEntrada notaEntrada(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

    @Override
    public SucataMovimento getContaCorrente(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SucataMovimento> getContaCorrentesInd(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SucataMovimento> getContaCorrentesAuto(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SucataMovimento> getSucatasMovimentoOcp(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean atualizarNota(SucataMovimento t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean atualizarOrdemCompra(SucataMovimento t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}
