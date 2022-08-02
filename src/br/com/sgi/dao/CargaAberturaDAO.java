/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.CargaAbertura;
import br.com.sgi.bean.CargaRegistro;
import br.com.sgi.bean.Transportadora;
import br.com.sgi.conexao.ConexaoMySql;
import br.com.sgi.conexao.ConnectionOracleSap;
import br.com.sgi.interfaces.InterfaceCargaAberturaDAO;
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
import br.com.sgi.util.ConversaoHoras;
import br.com.sgi.util.UtilDatas;

/**
 *
 * @author jairosilva
 */
public class CargaAberturaDAO implements InterfaceCargaAberturaDAO<CargaRegistro> {
    
    private Connection con;
    private UtilDatas utilDatas;
    
    private void openConnectionMySql() throws Exception {
        con = ConexaoMySql.getConexao();
    }
    
    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    
    private void setPreparedStatement(CargaRegistro e,
            java.sql.PreparedStatement pst, String acao) throws SQLException {
        
        pst.setInt(1, e.getEmpresa());
        pst.setInt(2, e.getFilial());
        pst.setInt(3, e.getNumerocarga());
        pst.setString(4, e.getPlaca());
        pst.setString(5, e.getNomeMotorista());
        
        pst.setString(6, e.getDocumentMotorista());
        pst.setDouble(7, e.getPesoEntrada());
        pst.setDate(8, new Date(e.getDataEntrada().getTime()));
        pst.setInt(9, e.getHoraEntrada());
        pst.setDouble(10, e.getPesoSaida());
        
        pst.setDate(11, null);
        pst.setInt(12, 0);
        pst.setDouble(13, 0.0);
        pst.setString(14, e.getObservacaoCarga());
        pst.setInt(15, 0);
        
        if (acao.equals("A")) {
            pst.setInt(16, e.getUsuarioAlteracao());
            pst.setInt(17, e.getHoraAlteracao());
            pst.setDate(18, new Date(e.getDataAlteracao().getTime()));
        } else {
            pst.setInt(16, 0);
            pst.setInt(17, 0);
            pst.setDate(18, null);
        }
        pst.setString(19, "A");
        pst.setString(20, e.getTipoCarga());
        pst.setString(21, e.getOrdemCompra());
        pst.setInt(22, e.getCodigoTransportadora());
        
    }
    
    private List<CargaRegistro> getLista(ResultSet rs, String opc) throws SQLException, ParseException {
        List<CargaRegistro> resultado = new ArrayList<CargaRegistro>();
        
        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            CargaRegistro e = new CargaRegistro();
            e.setEmpresa(rs.getInt("usu_codemp"));
            e.setDataAlteracao(rs.getDate("usu_datalt"));
            e.setDataEntrada(rs.getDate("usu_datent"));
            
            e.setDataEntradaS(this.utilDatas.converterDateToStr(e.getDataEntrada()));
            e.setHoraEntrada(rs.getInt("usu_horent"));
            e.setHoraEntradaS(ConversaoHoras.converterMinutosHora(e.getHoraEntrada()));
            
            e.setDataSaida(rs.getDate("usu_datsai"));
            if (e.getDataSaida() == null) {
            } else {
                e.setHoraSaida(rs.getInt("usu_horsai"));
                e.setDataSaidaS(this.utilDatas.converterDateToStr(e.getDataSaida()));
                e.setHoraSaidaS(ConversaoHoras.converterMinutosHora(e.getHoraSaida()));
            }
            
            e.setDocumentMotorista(rs.getString("usu_motdoc"));
            e.setFilial(rs.getInt("usu_codfil"));
            e.setFornecedor(0);
            e.setHoraAlteracao(rs.getInt("usu_horalt"));
            
            e.setNomeMotorista(rs.getString("usu_nommot"));
            e.setNumerocarga(rs.getInt("usu_nrocar"));
            e.setObservacaoCarga(rs.getString("usu_obscar"));
            
            e.setPercentualDescontoImpureza(0.0);
            e.setPesoDescontoEmbalagens(0.0);
            e.setPesoDescontoImpureza(0.0);
            
            e.setPesoEntrada(rs.getDouble("usu_pesent"));
            e.setPesoSaida(rs.getDouble("usu_pessai"));
            e.setPesoVeiculo(rs.getDouble("usu_pesvei"));
            e.setPesoLiquidoCarga(rs.getDouble("usu_pesliq"));
            
            e.setPlaca(rs.getString("usu_plavei"));
            e.setSituacaoCarga(rs.getString("usu_sitcar"));
            e.setUsuarioAlteracao(rs.getInt("usu_usualt"));
            e.setUsuarioCadastro(rs.getInt("usu_usucad"));
            e.setTipoCarga(rs.getString("usu_cardes"));
            e.setOrdemCompra(rs.getString("usu_numocp"));
            
            e.setPesoDescontoEmbalagens(rs.getDouble("usu_pesemb"));
            e.setPesoDescontoImpureza(rs.getDouble("usu_pesimp"));
            e.setCodigoTransportadora(rs.getInt("usu_codtra"));
            Transportadora tra = new Transportadora();
            
            if (e.getCodigoTransportadora() == 0) {
                tra.setCodigoTransportadora(0);
                tra.setNomeTransportadora("NÃO INFORMADA OU PRÓPRIA");
            } else {
                tra.setCodigoTransportadora(e.getCodigoTransportadora());
                tra.setNomeTransportadora(rs.getString("nomtra"));
            }
            e.setTransportadora(tra);
            if (opc.equals("CG")) {
                e.setInfoMinuta(rs.getString("info"));
            }
            
            resultado.add(e);
        }
        return resultado;
    }
    
    @Override
    public boolean remover(CargaRegistro t) throws SQLException {
        boolean retorno = false;
        java.sql.PreparedStatement pst = null;
        String sqlExcluir = "update usu_tintcarga set usu_sitcar = ?, usu_obscar=upper(?) where usu_nrocar  = ? ";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlExcluir);
            pst.setString(1, t.getSituacaoCarga());
            pst.setString(2, t.getObservacaoCarga());
            pst.setInt(3, t.getNumerocarga());
            pst.executeUpdate();
            pst.close();
            retorno = true;
        } catch (Exception ex) {
            
            retorno = false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
            
        }
        return retorno;
        
    }
    
    @Override
    public boolean alterar(CargaRegistro t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "update  usu_tintcarga set\n"
                + " usu_obscar=? \n"
                + " where usu_codemp=? "
                + " and usu_codfil=? "
                + " and usu_nrocar=?";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setString(1, t.getObservacaoCarga());
            pst.setInt(2, t.getEmpresa());
            pst.setInt(3, t.getFilial());
            pst.setInt(4, t.getNumerocarga());
            
            pst.executeUpdate();
            pst.close();
            
            JOptionPane.showMessageDialog(null, "Registro de carga aletrado com sucesso",
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
    public boolean inserir(CargaRegistro t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "insert into usu_tintcarga(\n"
                + "usu_codemp,usu_codfil,usu_nrocar,usu_plavei,usu_nommot, \n"
                + "usu_motdoc,usu_pesent,usu_datent,usu_horent,usu_pessai, \n"
                + "usu_datsai,usu_horsai,usu_pesvei,usu_obscar,usu_usucad, \n"
                + "usu_usualt,usu_horalt,usu_datalt,usu_sitcar,usu_cardes,"
                + "usu_numocp,usu_codtra) \n"
                + "values \n"
                + "(?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,\n"
                + "?,?,?,?,?,"
                + "?,?)";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            setPreparedStatement(t, pst, "I");
            pst.executeUpdate();
            pst.close();
            
            JOptionPane.showMessageDialog(null, "Registro de carga inserido com sucesso",
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
    public boolean atualizarPesoLiquido(CargaRegistro t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "update  usu_tintcarga set usu_pesliq = ? where usu_nrocar = ?";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            if (t.getPesoLiquidoCarga() < 0) {
                t.setPesoLiquidoCarga(t.getPesoLiquidoCarga() * -1);
            }
            pst.setDouble(1, t.getPesoLiquidoCarga());
            pst.setInt(2, t.getNumerocarga());
            
            pst.executeUpdate();
            pst.close();

//            JOptionPane.showMessageDialog(null, "Peso liquido atualizado com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
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
    public boolean atualizarPesoEntrada(CargaRegistro t) throws SQLException {
        
        PreparedStatement pst = null;
        
        String sqlInsert = "update  usu_tintcarga set usu_pesent = ? where usu_nrocar = ?";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setDouble(1, t.getPesoEntrada());
            pst.setInt(2, t.getNumerocarga());
            
            pst.executeUpdate();
            pst.close();
            
            JOptionPane.showMessageDialog(null, "Peso entrada atualizado com sucesso",
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
    public boolean atualizarPesoSaída(CargaRegistro t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "update  usu_tintcarga set"
                + " usu_pessai = ?, "
                + " usu_pesvei=?, "
                + " usu_sitcar=?, "
                + " usu_datsai=?,"
                + " usu_horsai=?"
                + " where usu_nrocar = ?";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setDouble(1, t.getPesoSaida());
            if (t.getPesoVeiculo() < 0) {
                t.setPesoVeiculo(t.getPesoVeiculo() * -1);
            }
            pst.setDouble(2, t.getPesoVeiculo());
            pst.setString(3, "F");
            pst.setDate(4, new Date(t.getDataSaida().getTime()));
            pst.setInt(5, t.getHoraSaida());
            pst.setInt(6, t.getNumerocarga());
            
            pst.executeUpdate();
            pst.close();
            
            JOptionPane.showMessageDialog(null, "Peso de saída atualizado com sucesso",
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
    public boolean atualizarPesoDesconto(CargaRegistro t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "update  usu_tintcarga set usu_pesimp = ? where usu_nrocar = ?";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setDouble(1, t.getPesoDescontoImpureza());
            pst.setInt(2, t.getNumerocarga());
            
            pst.executeUpdate();
            pst.close();

//            JOptionPane.showMessageDialog(null, "Peso de impureza atualizado sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
//            
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
    public boolean atualizarPesoEmbalagem(CargaRegistro t) throws SQLException {
        
        PreparedStatement pst = null;
        
        String sqlInsert = "update  usu_tintcarga set usu_pesemb = ? where usu_nrocar = ?";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setDouble(1, t.getPesoEmbalagen());
            pst.setInt(2, t.getNumerocarga());
            
            pst.executeUpdate();
            pst.close();

//            JOptionPane.showMessageDialog(null, "Peso embalagem  atualizado com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
//            
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
    public boolean inserirCarga(CargaAbertura t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "insert into usu_tintcarfor(\n"
                + "usu_codemp,usu_codfil,usu_nrocar,usu_codfor)\n"
                + "values \n"
                + "(?,?,?,?)";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, t.getEmpresa());
            pst.setInt(2, t.getFilial());
            pst.setInt(3, t.getNumerocarga());
            pst.setInt(4, t.getFornecedor());
            pst.executeUpdate();
            pst.close();
//            
//            JOptionPane.showMessageDialog(null, "Abertura registrado com sucesso",
//                    "Atenção", JOptionPane.INFORMATION_MESSAGE);

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
    public List<CargaRegistro> getCargasRegistro(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<CargaRegistro> resultado = new ArrayList<CargaRegistro>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "Select car.*, "
                + "  tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra\n"
                + "  from usu_tintcarga car\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = car.usu_codtra)\n"
                + " where car.usu_codemp > 0 \n ";
        sqlSelect += PESQUISA;
        
        sqlSelect += "order by car.usu_nrocar desc";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            
            resultado = getLista(rs,"CN");
            
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
    public CargaRegistro getCargaRegistro(String PESQUISA_POR, String PESQUISA) throws SQLException {
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "Select car.*, tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra,  ite.usu_codpro,  ite.usu_despro\n"
                + "  from  usu_tintcarga car\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = car.usu_codtra)\n"
                + "    left join usu_tintitens ite\n"
                + "    on ite.usu_nrocar = car.usu_nrocar\n"
                + " where car.usu_codemp > 0";
        sqlSelect += PESQUISA;
        
        sqlSelect += "order by car.usu_datent desc";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            List<CargaRegistro> resultado = new ArrayList<CargaRegistro>();
            resultado = getLista(rs,"CN");
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
    
    public CargaRegistro getCargaRegistroMinuta(String PESQUISA_POR, String PESQUISA) throws SQLException {
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "Select car.*, tra.codtra, tra.nomtra, tra.cidtra, tra.sigufs, tra.apetra,  ite.usu_codpro,  ite.usu_despro, ite.usu_codpro || ' - ' || ite.usu_despro as info\n"
                + "  from  usu_tintcarga car\n"
                + "  left join e073tra tra\n"
                + "    on (tra.codtra = car.usu_codtra)\n"
                + "    left join usu_tintitens ite\n"
                + "    on ite.usu_nrocar = car.usu_nrocar\n"
                + " where car.usu_codemp > 0";
        sqlSelect += PESQUISA;
        
        sqlSelect += "order by usu_datent desc";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlSelect);
            
            rs = pst.executeQuery();
            List<CargaRegistro> resultado = new ArrayList<CargaRegistro>();
            resultado = getLista(rs,"CG");
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
    
    public int proxCodCad() throws SQLException {
        Statement st = null;
        String strSql = "SELECT NVL((MAX(usu_nrocar) + 1), 1) PROX_CODALAN FROM usu_tintcarfor";
        
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
    
    public boolean gravarEmail(CargaRegistro t) throws SQLException {
        PreparedStatement pst = null;
        
        String sqlInsert = "UPDATE  usu_tintcarga SET  \n"
                + "	usu_envema = 'S' \n"
                + "       where usu_nrocar =  ?";
        
        try {
            ConnectionOracleSap();
            pst = con.prepareStatement(sqlInsert);
            
            pst.setInt(1, t.getNumerocarga());
            
            pst.executeUpdate();
            pst.close();
            JOptionPane.showMessageDialog(null, "O Sistema ira enviar e-mail automáticamente ",
                    "Atenção:", JOptionPane.OK_OPTION);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro " + ex,
                    "Erro:", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.close();
        }
    }
    
}
