/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.Cliente;
import br.com.sgi.interfaces.InterfaceFilialDAO;
import br.com.sgi.bean.Filial;
import br.com.sgi.conexao.ConnectionOracleSap;
import java.sql.Connection;
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
public class FilialDAO implements InterfaceFilialDAO<Filial> {
    
    private Connection con;
    
    private void ConnectionOracleSap() throws Exception {
        con = ConnectionOracleSap.openConnection();
    }
    
    private List<Filial> getLista(ResultSet rs) throws SQLException, ParseException {
        List<Filial> resultado = new ArrayList<Filial>();
        while (rs.next()) {
            Filial e = new Filial();
            e.setEmpresa_id(rs.getInt("codemp"));
            e.setFilial(rs.getInt("codfil"));
            e.setRazao_social(rs.getString("nomfil"));
            e.setProdutosucata(rs.getString("usu_prosuc"));
            e.setSerie(rs.getString("vensnp"));
            e.setCondicao_pgto_complemento(rs.getString("usu_codcpg"));
            e.setTransacao_complemento(rs.getString("usu_tnscpl"));
            e.setPrecoSucata(rs.getDouble("usu_vlrsuc"));
            e.setCnpj(rs.getString("numcgc"));
            e.setEstado(rs.getString("sigufs"));
            int tam = e.getCnpj().length();
            if (tam < 14) {
                e.setCnpj("0" + e.getCnpj());
            }
            e.setDiretorio("\\\\SRV-SPNS01\\XMLs\\" + e.getCnpj() + "\\xml\\" + e.getCnpj() + "\\Cte Recebimento\\");
            resultado.add(e);
        }
        return resultado;
    }
    
    @Override
    public List<Filial> getFilias(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Filial> resultado = new ArrayList<Filial>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "select codfil, sigfil as nomfil, usu_prosuc, usu_codcpg,  usu_tnscpl,usu_vlrsuc from e070fil where codfil > 0 ";
        sqlSelect = "select codemp, numcgc,  codfil, sigfil as nomfil, usu_prosuc, vensnp,  usu_codcpg, usu_tnscpl, usu_tnseco, usu_vlrsuc, sigufs from e070fil where codfil > 0 ";
        sqlSelect += PESQUISA;
        sqlSelect += " order by codfil asc";
        
        System.out.println(sqlSelect);
        
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
    public Filial getFilia(String PESQUISA_POR, String PESQUISA) throws SQLException {
        List<Filial> resultado = new ArrayList<Filial>();
        
        java.sql.PreparedStatement pst = null;
        
        ResultSet rs = null;
        
        String sqlSelect = "select codemp,  numcgc,  codfil,  sigfil as nomfil, usu_prosuc, vensnp,  usu_codcpg, usu_tnscpl, usu_tnseco, usu_vlrsuc, sigufs"
                + " from e070fil where codfil > 0 ";
        
        sqlSelect += PESQUISA;
        
        System.out.println("Filial" + sqlSelect);
        
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
    
}
