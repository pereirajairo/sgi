/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.dao;

import br.com.sgi.bean.CameraParametro;

import br.com.sgi.interfaces.InterfaceCameraParametroDAO;
import br.com.sgi.util.UtilDatas;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairosilva
 */
public class CameraParametroDAO implements InterfaceCameraParametroDAO<CameraParametro> {

    private String urlCamera;
    private String usuarioCamera;
    private String senhaCamera;
    private UtilDatas utilDatas;

    private List<CameraParametro> getCameraParametros(ResultSet rs) throws SQLException, ParseException {
        List<CameraParametro> resultado = new ArrayList<CameraParametro>();

        this.utilDatas = new UtilDatas();
        while (rs.next()) {
            //  Funcionario e = new Funcionario();
            //  e.setCodigoFuncionario(rs.getInt("NumCad"));
            // e.setNomeFuncionario(rs.getString("NomFun"));

            // resultado.add(e);
        }
        return resultado;
    }

    @Override
    public CameraParametro getCameraParametro() throws SQLException {

        CameraParametro e = new CameraParametro();
        Scanner in = null;
        try {
            in = new Scanner(new FileReader("ConfiguracaoCamera.prop"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CameraParametroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (in.hasNextLine()) {
            String line = in.nextLine();
            // System.out.println(line);
            String aURL = line.substring(0, 3);
            String aUsuario = line.substring(0, 7);
            String aSenha = line.substring(0, 5);
            if (aURL.equals("URL")) {
                int pos = line.length();
                urlCamera = line.substring(4, pos);
                //   System.out.println(ParametroCOM);
            }
            if (aUsuario.equals("USUARIO")) {
                int pos = line.length();
                // pos = (8 - pos) + 1;
                usuarioCamera = line.substring(8, pos);
                //  System.out.println(BalNom.trim());

            }
            if (aSenha.equals("SENHA")) {
                int pos = line.length();
                senhaCamera = line.substring(6, pos);
                //  System.out.println(BalNom.trim());

            }
        }

        e.setUrlCamera(urlCamera);
        e.setUsuarioCamera(usuarioCamera);
        e.setSenhaCamera(senhaCamera);
        return e;
    }

    @Override
    public List<CameraParametro> getCameraParametros(String PESQUISA_POR, String PESQUISA) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
