/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.interfaces.InterfaceClienteDAO;
import br.com.sgi.bean.Cliente;
import br.com.sgi.bean.ClienteGrupo;
import br.com.sgi.bean.Representante;
import br.com.sgi.bean.Vendedor;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.util.UtilDatas;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jairosilva
 */
public class ClienteDAO implements InterfaceClienteDAO<Cliente> {

    private Connection con;

    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    private UtilDatas utilDatas;

    private List<Cliente> getLista(ResultSet rs) throws SQLException, ParseException {
        this.utilDatas = new UtilDatas();
        List<Cliente> resultado = new ArrayList<Cliente>();

        String fone;
        String fon2;
        String fon3;
        String fon4;
        String fon5;
        String faxc;

        while (rs.next()) {
            Cliente c = new Cliente();

            c.setEmpresa(0);
            c.setFilial(0);
            c.setCodigo(rs.getInt("codcli"));
            c.setCodigo_cliente(rs.getInt("codcli"));
            c.setBairro(rs.getString("baicli"));
            c.setCidade(rs.getString("cidcli"));
            c.setNumero(rs.getString("nencli"));
            c.setFantasia(rs.getString("apecli"));

            fone = rs.getString("foncli");
            fon2 = rs.getString("foncl2");
            fon3 = rs.getString("foncl3");
            fon4 = rs.getString("foncl4");
            fon5 = rs.getString("foncl5");
            faxc = rs.getString("faxcli");

            c.setTelefone("Fone: " + fone + " ou " + fon2 + " ou " + fon3 + " ou " + fon4 + " ou " + fon5 + " ou fax " + faxc);

            c.setCep(rs.getString("cepcli"));
            c.setNome(rs.getString("nomcli"));
            c.setEmail(rs.getString("intnet"));
            c.setEndereco(rs.getString("endcli"));
            c.setEstado(rs.getString("sigufs"));
            c.setGrupo_empresa(rs.getString("codgre"));

            c.setData_ultimo_faturamento(rs.getDate("datemi"));
            if (c.getData_ultimo_faturamento() != null) {
                c.setData_ultimo_faturamentoS(this.utilDatas.converterDateToStr(c.getData_ultimo_faturamento()));
            } else {
                c.setData_ultimo_faturamentoS("SEM MOVIMENTO");
            }

            c.setSituacao("ATIVO");
            if (c.getData_ultimo_faturamento() != null) {
                c.setDias_ultimo_faturamento(this.utilDatas.qtdDias(c.getData_ultimo_faturamento(), new Date()));
            } else {
                c.setDias_ultimo_faturamento(0);
            }

            if (c.getDias_ultimo_faturamento() > 90) {
                c.setSituacao("INATIVO");
            }

            if (c.getData_ultimo_faturamentoS().equals("SEM MOVIMENTO")) {
                c.setSituacao("INATIVO");
            }

            //  c.setDias_ultimo_faturamento(FormatarNumeros.converterDoubleDoisDecimais(c.getDias_ultimo_faturamento()));
            c.setOrigem(rs.getString("codori"));
            String codori = "MOT";
            if (c.getOrigem().equals("BA")) {
                c.setOrigem("AUTO");
                codori = "AUT";
            } else {
                c.setOrigem("MOTO");

            }
            if (c.getData_ultimo_faturamentoS().equals("SEM MOVIMENTO")) {
                c.setOrigem("");
            }
            c.setGrupocliente_id(rs.getInt("codgre"));

            ClienteGrupo cli = new ClienteGrupo();
            cli.setCodgrp(c.getGrupo_empresa());
            cli.setNome(rs.getString("nomgre"));

            c.setCadClienteGrupo(cli);
            c.setRepresentante(rs.getInt("codrep"));

            Representante rep = new Representante();
            rep.setCodigo(c.getRepresentante());
            rep.setNome(rs.getString("nomrep"));
            c.setCadRepresentante(rep);

            Vendedor ven = new Vendedor();
            ven.setCodigo(rs.getInt("codven"));
            ven.setNome(rs.getString("nomven"));
            c.setCadVendedor(ven);

            resultado.add(c);

        }
        return resultado;
    }

    private List<Cliente> getListaGeral(ResultSet rs) throws SQLException, ParseException {
        this.utilDatas = new UtilDatas();
        List<Cliente> resultado = new ArrayList<Cliente>();

        String fone;
        String fon2;
        String fon3;
        String fon4;
        String fon5;
        String faxc;

        while (rs.next()) {
            Cliente c = new Cliente();

            c.setEmpresa(0);
            c.setFilial(0);
            c.setCodigo(rs.getInt("codcli"));
            c.setCodigo_cliente(rs.getInt("codcli"));
            c.setBairro(rs.getString("baicli"));
            c.setCidade(rs.getString("cidcli"));
            c.setNumero(rs.getString("nencli"));
            c.setFantasia(rs.getString("apecli"));

            fone = rs.getString("foncli");
            fon2 = rs.getString("foncl2");
            fon3 = rs.getString("foncl3");
            fon4 = rs.getString("foncl4");
            fon5 = rs.getString("foncl5");
            faxc = rs.getString("faxcli");

            c.setTelefone("Fone: " + fone + " ou " + fon2 + " ou " + fon3 + " ou " + fon4 + " ou " + fon5 + " ou fax " + faxc);

            c.setCep(rs.getString("cepcli"));
            c.setNome(rs.getString("nomcli"));
            c.setEmail(rs.getString("intnet"));
            c.setEndereco(rs.getString("endcli"));
            c.setEstado(rs.getString("sigufs"));
            c.setGrupo_empresa(rs.getString("codgre"));

            c.setData_ultimo_faturamento(rs.getDate("datemi"));
            if (c.getData_ultimo_faturamento() != null) {
                c.setData_ultimo_faturamentoS(this.utilDatas.converterDateToStr(c.getData_ultimo_faturamento()));
            } else {
                c.setData_ultimo_faturamentoS("SEM MOVIMENTO");
            }

            if (c.getData_ultimo_faturamento() != null) {
                c.setDias_ultimo_faturamento(this.utilDatas.qtdDias(c.getData_ultimo_faturamento(), new Date()));
            } else {
                c.setDias_ultimo_faturamento(0);
            }

            c.setSituacao(rs.getString("SITCLI"));
            if (c.getSituacao().equals("A")) {
                c.setSituacao("ATIVO");
            }

            if (c.getSituacao().equals("I")) {
                c.setSituacao("INATIVO");
            }

            if (c.getSituacao().equals("P")) {
                c.setSituacao("PRE-INATIVO");
            }

            //  c.setDias_ultimo_faturamento(FormatarNumeros.converterDoubleDoisDecimais(c.getDias_ultimo_faturamento()));
            c.setOrigem(rs.getString("codori"));
            String codori = "MOT";
            if (c.getOrigem().equals("BA")) {
                c.setOrigem("AUTO");
                codori = "AUT";
            } else {
                c.setOrigem("MOTO");

            }
            if (c.getData_ultimo_faturamentoS().equals("SEM MOVIMENTO")) {
                c.setOrigem("");
            }
            c.setGrupocliente_id(rs.getInt("codgre"));

            c.setPedido(rs.getString("numped"));
            c.setQuatidade_fat_ano(rs.getDouble("qtdfatano"));
            // c.setQuatidade_fat_ano(rs.getInt(""));

            ClienteGrupo cli = new ClienteGrupo();
            cli.setCodgrp(c.getGrupo_empresa());
            cli.setNome(rs.getString("nomgre"));
            if (c.getGrupo_empresa().equals("0")) {
                cli.setNome("SEM GRUPO INFORMADO");
            }
            c.setCadClienteGrupo(cli);

            c.setRepresentante(rs.getInt("codrep"));
            Representante rep = new Representante();
            rep.setCodigo(c.getRepresentante());
            rep.setNome(rs.getString("nomrep"));
            c.setCadRepresentante(rep);

            Vendedor ven = new Vendedor();
            ven.setCodigo(rs.getInt("codven"));
            ven.setNome(rs.getString("nomven"));
            c.setCadVendedor(ven);

            resultado.add(c);

        }
        return resultado;
    }

    public Representante getRepresentante(Integer codcli, String codori) throws SQLException {
        Representante rep = new Representante();
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = " select hcl.usu_codven, rep.nomrep as nomrep\n"
                + "   from usu_t081hcl hcl\n"
                + "   left join e090rep rep\n"
                + "     on (rep.codrep = hcl.usu_codven)\n"
                + "  where hcl.usu_codcli = " + codcli + "\n"
                + "    and hcl.usu_codagp = '" + codori + "' ";

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            if (rs.next()) {
                rep.setCodigo(rs.getInt("usu_codven"));
                rep.setNome(rs.getString("nomrep"));

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
        return rep;

    }

    @Override
    public List<Cliente> getClientes(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select codcli, nomcli, sigufs, cli.codgre, nomgre\n"
                + "  from e085cli cli\n"
                + "  left join e069gre gre\n"
                + "    on (gre.codgre = cli.codgre)\n"
                + " where codcli > 0";

        sqlSelect = "select MES,\n"
                + "       ANO,\n"
                + "       CODCLI,\n"
                + "       DATEMI,\n"
                + "       CODORI,\n"
                + "       NOMCLI,\n"
                + "       CIDCLI,\n"
                + "       SIGUFS,\n"
                + "       NENCLI,\n"
                + "       APECLI,\n"
                + "       FONCLI,\n"
                + "       foncl2,\n"
                + "       foncl3,\n"
                + "       foncl4,\n"
                + "       foncl5,\n"
                + "       faxcli,\n"
                + "       CEPCLI,\n"
                + "       INTNET,\n"
                + "       ENDCLI,\n"
                + "       CODGRE,\n"
                + "       NOMGRE,\n"
                + "       BAICLI,\n"
                + "       CODVEN,\n"
                + "       NOMVEN,\n"
                + "       CODREP,\n"
                + "       NOMREP\n"
                + "  from (SELECT MES,\n"
                + "               ANO,\n"
                + "               CODCLI,\n"
                + "               DATEMI,\n"
                + "               CODORI,\n"
                + "               NOMCLI,\n"
                + "               CIDCLI,\n"
                + "               SIGUFS,\n"
                + "               NENCLI,\n"
                + "               APECLI,\n"
                + "               FONCLI,\n"
                + "               foncl2,\n"
                + "               foncl3,\n"
                + "               foncl4,\n"
                + "               foncl5,\n"
                + "               faxcli,\n"
                + "               CEPCLI,\n"
                + "               INTNET,\n"
                + "               ENDCLI,\n"
                + "               CODGRE,\n"
                + "               NOMGRE,\n"
                + "               BAICLI,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT to_char(usu_codven)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT to_char(usu_codven)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END codven,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codven = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codven = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END nomven,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT to_char(usu_codrep)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT to_char(usu_codrep)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END codrep,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codrep = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codrep = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END nomrep\n"
                + "          FROM (select max(to_char(a.datemi, 'mm')) mes,\n"
                + "                       max(to_char(a.datemi, 'yyyy')) ano,\n"
                + "                       max(a.codcli) codcli,\n"
                + "                       max(a.datemi) datemi,\n"
                + "                       max(c.codori) codori,\n"
                + "                       max(d.nomcli) nomcli,\n"
                + "                       max(d.cidcli) cidcli,\n"
                + "                       max(d.sigufs) sigufs,\n"
                + "                       max(d.nencli) nencli,\n"
                + "                       max(d.apecli) apecli,\n"
                + "                       max(d.foncli) foncli,\n"
                + "                       max(d.foncl2) foncl2,\n"
                + "                       max(d.foncl3) foncl3,\n"
                + "                       max(d.foncl4) foncl4,\n"
                + "                       max(d.foncl5) foncl5,\n"
                + "                       max(d.faxcli) faxcli,\n"
                + "                       max(d.cepcli) cepcli,\n"
                + "                       max(d.intnet) intnet,\n"
                + "                       max(d.endcli) endcli,\n"
                + "                       max(d.codgre) codgre,\n"
                + "                       max(gre.nomgre) nomgre,\n"
                + "                       max(d.baicli) baicli\n"
                + "                  from e140nfv a, e140ipv b, e075pro c, e001tns e, e085cli d\n"
                + "                  left join e069gre gre\n"
                + "                    on (gre.codgre = d.codgre)\n"
                + "                 where a.sitnfv = 2\n"
                + "                   and a.codemp = 1\n"
                + "                   and a.codemp = b.codemp\n"
                + "                   and a.codfil = b.codfil\n"
                + "                   and a.codsnf = b.codsnf\n"
                + "                   and a.numnfv = b.numnfv\n"
                + "                   and b.codemp = c.codemp\n"
                + "                   and b.codpro = c.codpro\n"
                + "                   and a.codcli = d.codcli\n"
                + "                   and a.codcli <> 1\n"
                + "                   and a.codemp = e.codemp\n"
                + "                   and a.tnspro = e.codtns\n"
                + "                   and e.venfat = 'S'\n"
                + "                   and d.sitcli = 'A'\n"
                + "                   and not exists\n"
                + "                   (select 1 from e070fil e where a.codcli = e.filcli)\n"
                //   + "                   and a.codfil " + PESQUISA_POR + "\n"
                + "                   and b.codtpr not IN ('FUNC','FUNS','17FU','17FS')\n"
                + "                 group by a.codcli, c.codori\n"
                + "                 order by a.codcli) dd) ddd\n"
                + " WHERE 0 = 0\n "
                + " and codgre not in (26) \n"
                + "  AND codcli NOT IN (SELECT codcli FROM E301TCR WHERE CODEMP = 1 \n"
                + "   AND vctpro < to_date(SYSDATE, 'dd/mm/yyyy') \n"
                + "  AND VLRABE <> 0 \n"
                + "  AND CODTPT NOT IN ('ADT','CRE','DEV'))";
        sqlSelect += PESQUISA;
        System.out.println(" sql" + sqlSelect);

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
    public Cliente getCliente(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select max(to_char(a.datemi, 'mm')) mes,\n"
                + "       max(to_char(a.datemi, 'yyyy')) ano,\n"
                + "       max(a.codcli) codcli,\n"
                + "       max(a.datemi) datemi,\n"
                + "       max(c.codori) codori,\n"
                + "       max(d.nomcli) nomcli,\n"
                + "       max(d.cidcli) cidcli,\n"
                + "       max(d.sigufs) sigufs,\n"
                + "       max(d.nencli) nencli,\n"
                + "       max(d.apecli) apecli,\n"
                + "       max(d.foncli) foncli,\n"
                + "       max(d.cepcli) cepcli,\n"
                + "       max(d.intnet) intnet,\n"
                + "       max(d.endcli) endcli,\n"
                + "       max(d.codgre) codgre,\n"
                + "       max(gre.nomgre) nomgre,\n"
                + "       max(d.baicli) baicli\n"
                + "  from e140nfv a, e140ipv b, e075pro c, e001tns e, e085cli d\n"
                + "  left join e069gre gre\n"
                + "    on (gre.codgre = d.codgre)\n"
                + " where a.sitnfv = 2\n"
                + "   and a.codemp = 1\n"
                + "   and a.codemp = b.codemp\n"
                + "   and a.codfil = b.codfil\n"
                + "   and a.codsnf = b.codsnf\n"
                + "   and a.numnfv = b.numnfv\n"
                + "   and b.codemp = c.codemp\n"
                + "   and b.codpro = c.codpro\n"
                + "   and c.codori in ('BM', 'BA')\n"
                + "   and a.codcli = d.codcli\n"
                + "   and a.codemp = 1\n";
        sqlSelect += PESQUISA;
        sqlSelect += "  and a.codemp = e.codemp\n"
                + "   and a.tnspro = e.codtns\n"
                + "   and e.venfat = 'S'\n"
                + "   and not exists (select 1\n"
                + "          from e070fil e\n"
                + "         where a.codcli = e.filcli\n"
                + "           and a.codfil not in (0))\n"
                + "   and b.codtpr not IN ('FUNC', 'FUNS0', '17FU', '17FS')\n"
                + " group by a.codcli, c.codori\n"
                + " order by a.codcli";

        sqlSelect = "select MES,\n"
                + "       ANO,\n"
                + "       CODCLI,\n"
                + "       DATEMI,\n"
                + "       CODORI,\n"
                + "       NOMCLI,\n"
                + "       CIDCLI,\n"
                + "       SIGUFS,\n"
                + "       NENCLI,\n"
                + "       APECLI,\n"
                + "       FONCLI,\n"
                + "       foncl2,\n"
                + "       foncl3,\n"
                + "       foncl4,\n"
                + "       foncl5,\n"
                + "       faxcli,\n"
                + "       CEPCLI,\n"
                + "       INTNET,\n"
                + "       ENDCLI,\n"
                + "       CODGRE,\n"
                + "       NOMGRE,\n"
                + "       BAICLI,\n"
                + "       CODVEN,\n"
                + "       NOMVEN,\n"
                + "       CODREP,\n"
                + "       NOMREP\n"
                + "  from (SELECT MES,\n"
                + "               ANO,\n"
                + "               CODCLI,\n"
                + "               DATEMI,\n"
                + "               CODORI,\n"
                + "               NOMCLI,\n"
                + "               CIDCLI,\n"
                + "               SIGUFS,\n"
                + "               NENCLI,\n"
                + "               APECLI,\n"
                + "               FONCLI,\n"
                + "               foncl2,\n"
                + "               foncl3,\n"
                + "               foncl4,\n"
                + "               foncl5,\n"
                + "               faxcli,\n"
                + "               CEPCLI,\n"
                + "               INTNET,\n"
                + "               ENDCLI,\n"
                + "               CODGRE,\n"
                + "               NOMGRE,\n"
                + "               BAICLI,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT to_char(usu_codven)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT to_char(usu_codven)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END codven,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codven = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codven = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END nomven,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT to_char(usu_codrep)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT to_char(usu_codrep)\n"
                + "                     FROM USU_T081HCL\n"
                + "                    WHERE USU_CODCLI = dd.codcli\n"
                + "                      AND USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END codrep,\n"
                + "               CASE\n"
                + "                 WHEN codori = 'BA' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codrep = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'AUT')\n"
                + "                 WHEN codori = 'BM' THEN\n"
                + "                  (SELECT nomrep\n"
                + "                     FROM USU_T081HCL\n"
                + "                     LEFT JOIN E090REP\n"
                + "                       ON USU_T081HCL.usu_codrep = E090REP.CODREP\n"
                + "                    WHERE USU_T081HCL.USU_CODCLI = dd.codcli\n"
                + "                      AND USU_T081HCL.USU_CODAGP = 'MOT')\n"
                + "                 ELSE\n"
                + "                  ''\n"
                + "               END nomrep\n"
                + "          FROM (select max(to_char(a.datemi, 'mm')) mes,\n"
                + "                       max(to_char(a.datemi, 'yyyy')) ano,\n"
                + "                       max(a.codcli) codcli,\n"
                + "                       max(a.datemi) datemi,\n"
                + "                       max(c.codori) codori,\n"
                + "                       max(d.nomcli) nomcli,\n"
                + "                       max(d.cidcli) cidcli,\n"
                + "                       max(d.sigufs) sigufs,\n"
                + "                       max(d.nencli) nencli,\n"
                + "                       max(d.apecli) apecli,\n"
                + "                       max(d.foncli) foncli,\n"
                + "                       max(d.foncl2) foncl2,\n"
                + "                       max(d.foncl3) foncl3,\n"
                + "                       max(d.foncl4) foncl4,\n"
                + "                       max(d.foncl5) foncl5,\n"
                + "                       max(d.faxcli) faxcli,\n"
                + "                       max(d.cepcli) cepcli,\n"
                + "                       max(d.intnet) intnet,\n"
                + "                       max(d.endcli) endcli,\n"
                + "                       max(d.codgre) codgre,\n"
                + "                       max(gre.nomgre) nomgre,\n"
                + "                       max(d.baicli) baicli\n"
                + "                  from e140nfv a, e140ipv b, e075pro c, e001tns e, e085cli d\n"
                + "                  left join e069gre gre\n"
                + "                    on (gre.codgre = d.codgre)\n"
                + "                 where a.sitnfv = 2\n"
                + "                   and a.codemp = 1\n"
                + "                   and a.codemp = b.codemp\n"
                + "                   and a.codfil = b.codfil\n"
                + "                   and a.codsnf = b.codsnf\n"
                + "                   and a.numnfv = b.numnfv\n"
                + "                   and b.codemp = c.codemp\n"
                + "                   and b.codpro = c.codpro\n"
                + "                   and a.codcli = d.codcli\n"
                + "                   and a.codcli <> 1\n"
                + "                   and a.codemp = e.codemp\n"
                + "                   and a.tnspro = e.codtns\n"
                + "                   and e.venfat = 'S'\n"
                + "                   and not exists\n"
                + "                 (select 1 from e070fil e where a.codcli = e.filcli)\n"
                + "                   and a.codfil " + PESQUISA_POR + "\n"
                + "                   and b.codtpr not IN ('FUNC', 'FUNS', '17FU', '17FS')\n"
                + "                 group by a.codcli, c.codori\n"
                + "                 order by a.codcli) dd) ddd\n"
                + " WHERE 0 = 0\n "
                + " AND CODGRE NOT IN (26)";
        sqlSelect += PESQUISA;
        System.out.println(" sql" + sqlSelect);

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

    @Override
    public Cliente getClienteSucata(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select codcli, nomcli, sigufs, cli.codgre, nomgre\n"
                + "  from e085cli cli\n"
                + "  left join e069gre gre\n"
                + "    on (gre.codgre = cli.codgre)\n"
                + " where codcli > 0";
        sqlSelect += PESQUISA;

        System.out.println("br.com.recebimento.dao.ClienteDAO.getClienteSucata() \n" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSucata(rs);
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

    public List<ClienteGrupo> getClienteGrupo(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<ClienteGrupo> resultado = new ArrayList<ClienteGrupo>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select codgre, nomgre from e069gre order by codgre asc";
        sqlSelect += PESQUISA;

        System.out.println("br.com.recebimento.dao.ClienteDAO.getClienteSucata() \n" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            while (rs.next()) {
                ClienteGrupo g = new ClienteGrupo();
                g.setCodgrp(rs.getString("codgre"));
                g.setNome(rs.getString("nomgre"));
                resultado.add(g);
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

    private List<Cliente> getListaSucata(ResultSet rs) throws SQLException, ParseException {
        List<Cliente> resultado = new ArrayList<Cliente>();
        while (rs.next()) {
            Cliente e = new Cliente();
            e.setCodigo(rs.getInt("codcli"));
            e.setNome(rs.getString("nomcli"));
            e.setEstado(rs.getString("sigufs"));
            e.setGrupocodigo(rs.getString("codgre"));
            e.setGruponome(rs.getString("nomgre"));
            ClienteGrupo grp = new ClienteGrupo();
            grp.setCodgrp(e.getGrupocodigo());
            if (grp.getCodgrp().equals("0")) {
                grp.setNome("NÃO INFORMADA");
            } else {
                grp.setNome(e.getGruponome());
            }

            e.setCadClienteGrupo(grp);
            resultado.add(e);
        }
        return resultado;
    }

    @Override
    public List<Cliente> getClientesSucata(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select codcli, apecli as nomcli, sigufs, cli.codgre, nomgre\n"
                + "  from e085cli cli\n"
                + "  left join e069gre gre\n"
                + "    on (gre.codgre = cli.codgre)\n"
                + " where 1 = 1 \n";

        sqlSelect += PESQUISA;
        System.out.println(" sql" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaSucata(rs);

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
    public List<Cliente> getClientesMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select  0 AS MES,\n"
                + "       0 AS ANO,\n"
                + "       CODCLI,\n"
                + "       '' AS DATEMI,\n"
                + "       '0' AS CODORI,\n"
                + "       NOMCLI,\n"
                + "       CIDCLI,\n"
                + "       SIGUFS,\n"
                + "       NENCLI,\n"
                + "       APECLI,\n"
                + "       FONCLI,\n"
                + "       FONCL2,\n"
                + "       FONCL3,\n"
                + "       FONCL4,\n"
                + "       FONCL5,\n"
                + "       FAXCLI,\n"
                + "       CEPCLI,\n"
                + "       INTNET,\n"
                + "       ENDCLI,\n"
                + "       gre.CODGRE,\n"
                + "       NOMGRE,\n"
                + "       BAICLI,\n"
                + "       0 AS  CODVEN,\n"
                + "       '' AS NOMVEN,\n"
                + "        0 AS CODREP,\n"
                + "       '' AS NOMREP"
                + "  from e085cli cli\n"
                + "  left join e069gre gre\n"
                + "    on (gre.codgre = cli.codgre)\n"
                + " where codcli > 0 \n"
                + " and cli.codgre not in (26)";
        sqlSelect += PESQUISA;

        System.out.println(" sql" + sqlSelect);

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

    public List<Cliente> getClientesGeral(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select (to_char(usu_datreg, 'mm')) mes,\n"
                + "       (to_char(usu_datreg, 'yyyy')) ano,\n"
                + "       usu_codcli as CODCLI,\n"
                + "       usu_ultven as DATEMI,\n"
                + "       usu_codori as CODORI,\n"
                + "       cli.nomcli as NOMCLI,\n"
                + "       cli.cidcli as CIDCLI,\n"
                + "       cli.sigufs as SIGUFS,\n"
                + "       cli.nencli as NENCLI,\n"
                + "       cli.apecli as APECLI,\n"
                + "       cli.foncli as FONCLI,\n"
                + "       cli.foncl2 as foncl2,\n"
                + "       cli.foncl3 as foncl3,\n"
                + "       cli.foncl4 as foncl4,\n"
                + "       cli.foncl5 as foncl5,\n"
                + "       cli.faxcli as faxcli,\n"
                + "       cli.cepcli as CEPCLI,\n"
                + "       cli.intnet as INTNET,\n"
                + "       cli.endcli as ENDCLI,\n"
                + "       cli.codgre as CODGRE,\n"
                + "       gre.nomgre as NOMGRE,\n"
                + "       cli.baicli as BAICLI,\n"
                + "       usu_codven as CODVEN,\n"
                + "       usu_nomven as NOMVEN,\n"
                + "       usu_codrep as CODREP,\n"
                + "       usu_nomrep as NOMREP,\n"
                + "       usu_qtdfatmed as qtdfatano,\n"
                + "       car.usu_sitcli as sitcli,\n"
                + "       car.usu_diafat as diafat,\n"
                + "       usu_tipven as numped\n"
                + "\n"
                + "  from usu_t085ICC car\n"
                + "  left join e085cli cli\n"
                + "    on cli.codcli = car.usu_codcli\n"
                + "  left join e069gre gre\n"
                + "    on gre.codgre = cli.codgre\n"
                + " where cli.codgre not in (26) \n"
                + "   and usu_datreg = '23/01/2022'";

        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\norder by car.usu_sitcli,  car.usu_qtdfatmed desc";

        System.out.println(" sql" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaGeral(rs);

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

    public Cliente getClienteGeral(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select (to_char(usu_datreg, 'mm')) mes,\n"
                + "       (to_char(usu_datreg, 'yyyy')) ano,\n"
                + "       usu_codcli as CODCLI,\n"
                + "       usu_ultven as DATEMI,\n"
                + "       usu_codori as CODORI,\n"
                + "       cli.nomcli as NOMCLI,\n"
                + "       cli.cidcli as CIDCLI,\n"
                + "       cli.sigufs as SIGUFS,\n"
                + "       cli.nencli as NENCLI,\n"
                + "       cli.apecli as APECLI,\n"
                + "       cli.foncli as FONCLI,\n"
                + "       cli.foncl2 as foncl2,\n"
                + "       cli.foncl3 as foncl3,\n"
                + "       cli.foncl4 as foncl4,\n"
                + "       cli.foncl5 as foncl5,\n"
                + "       cli.faxcli as faxcli,\n"
                + "       cli.cepcli as CEPCLI,\n"
                + "       cli.intnet as INTNET,\n"
                + "       cli.endcli as ENDCLI,\n"
                + "       cli.codgre as CODGRE,\n"
                + "       gre.nomgre as NOMGRE,\n"
                + "       cli.baicli as BAICLI,\n"
                + "       usu_codven as CODVEN,\n"
                + "       usu_nomven as NOMVEN,\n"
                + "       usu_codrep as CODREP,\n"
                + "       usu_nomrep as NOMREP,\n"
                + "       usu_qtdfatmed as qtdfatano,\n"
                + "       car.usu_sitcli as sitcli,\n"
                + "       car.usu_diafat as diafat,\n"
                + "       usu_tipven as numped\n"
                + "\n"
                + "  from usu_t085ICC car\n"
                + "  left join e085cli cli\n"
                + "    on cli.codcli = car.usu_codcli\n"
                + "  left join e069gre gre\n"
                + "    on gre.codgre = cli.codgre\n"
                + " where cli.codgre not in (26)\n "
                + "   and usu_codcli not in (915351)\n"
                + "   and usu_datreg = '23/01/2022' ";

        if (!PESQUISA_POR.isEmpty()) {
            sqlSelect += PESQUISA;
        }
        sqlSelect += "\norder by car.usu_sitcli, car.usu_qtdfatmed desc";

        System.out.println(" sql" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);

            rs = pst.executeQuery();

            resultado = getListaGeral(rs);
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
    public Cliente getClienteMovimento(String PESQUISA_POR, String PESQUISA) throws SQLException {

        List<Cliente> resultado = new ArrayList<Cliente>();

        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "select  0 AS MES,\n"
                + "       0 AS ANO,\n"
                + "       CODCLI,\n"
                + "       '' AS DATEMI,\n"
                + "       '0' AS CODORI,\n"
                + "       NOMCLI,\n"
                + "       CIDCLI,\n"
                + "       SIGUFS,\n"
                + "       NENCLI,\n"
                + "       APECLI,\n"
                + "       FONCLI,\n"
                + "       FONCL2,\n"
                + "       FONCL3,\n"
                + "       FONCL4,\n"
                + "       FONCL5,\n"
                + "       FAXCLI,\n"
                + "       CEPCLI,\n"
                + "       INTNET,\n"
                + "       ENDCLI,\n"
                + "       gre.CODGRE,\n"
                + "       NOMGRE,\n"
                + "       BAICLI,\n"
                + "       0 AS  CODVEN,\n"
                + "       '' AS NOMVEN,\n"
                + "       0 AS CODREP,\n"
                + "       '' AS NOMREP"
                + "  from e085cli cli\n"
                + "  left join e069gre gre\n"
                + "    on (gre.codgre = cli.codgre)\n"
                + " where codcli > 0 "
                + " and cli.codgre not in (26)";
        sqlSelect += PESQUISA;

        System.out.println(" sql" + sqlSelect);

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

    @Override
    public Integer QuatidadeTituloVencido(String PESQUISA_POR, String PESQUISA) throws SQLException {
        Integer retorno = 0;
        Date data_vencimento = null;
        java.sql.PreparedStatement pst = null;

        ResultSet rs = null;

        String sqlSelect = "SELECT COUNT(CODCLI) QTDTIT,  MAX(VCTPRO) VCTPRO\n"
                + "  FROM E301TCR\n"
                + " WHERE CODEMP = 1\n"
                + "    AND vctpro < to_date(SYSDATE,'dd/mm/yyyy') \n"
                + "   AND VLRABE <> 0      \n"
                + "   AND CODTPT NOT IN ('ADT', 'CRE', 'DEV')\n";
        sqlSelect += PESQUISA;
        System.out.println(" sql" + sqlSelect);

        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            rs = pst.executeQuery();

            if (rs.next()) {
                retorno = rs.getInt("qtdtit");
                data_vencimento = rs.getDate("VCTPRO");
                if (data_vencimento != null) {
                    if (UtilDatas.analisarDataEqual(data_vencimento, new Date())) {
                        retorno = 0;
                    }
                } else {
                    retorno = 0;
                }

            }

            pst.close();
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return 0;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();

        }
        return retorno;

    }

}
