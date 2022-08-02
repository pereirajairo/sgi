/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import br.com.sgi.bean.Filial;
import br.com.sgi.bean.Usuario;
import br.com.sgi.dao.FilialDAO;
import br.com.sgi.dao.UsuarioERPDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**

teste
*
 * @author jairosilva
 */
public class AbrangeciaSapiens {

    public static String AbrangeciaSapiensFilial(String Login) throws SQLException {
        Usuario usuario;
        UsuarioERPDAO usuarioERPDAO = null;
        String abreFilial;
        String PesquisaFilial = " ";
        usuario = new Usuario();
        if (usuarioERPDAO == null) {
            usuarioERPDAO = new UsuarioERPDAO();
        }
        UsuarioERPDAO dao = new UsuarioERPDAO();
        usuario = usuarioERPDAO.getAbrangencia(Login.toString());

        if (usuario != null) {
            String procurarPorVircula = ",";
            String procurarPorTraso = "-";

            abreFilial = usuario.getCodfil();
            int tamAbreFilial = abreFilial.length();
            if (tamAbreFilial <= 2) {
                PesquisaFilial = " ";
                String replace = abreFilial.replace(',', ' ');
                if (replace.equals(" ")) {
                    PesquisaFilial = " ";
                } else {
                    PesquisaFilial += replace.trim() + " ";
                }

            } else if ((abreFilial.toLowerCase().contains(procurarPorTraso.toLowerCase())) || (abreFilial.toLowerCase().contains(procurarPorVircula.toLowerCase()))) {
                int y;
                int i = 0;
                PesquisaFilial = " ";
                String Variavel = abreFilial;

                y = Variavel.length();
                String[] parts = Variavel.split(",");
                for (int w = 0; w < parts.length; w++) {
                    int index;
                    String teste = parts[w];
                    index = teste.indexOf("-");
                    if (index <= 0) {
                        PesquisaFilial += parts[w] + ", ";
                        System.out.println(PesquisaFilial);
                    }
                    if (index > 0) {
                        int cont1 = 0;
                        int cont2 = 0;
                        String[] parts1 = teste.split("-");
                        for (int a = 0; a < parts1.length; a++) {
                            if (a % 2 != 0) {
                                String valor1 = parts1[0];
                                String valor2 = parts1[1];
                                cont1 = Integer.valueOf(valor1);
                                cont2 = Integer.valueOf(valor2);
                                int cont = 0;
                                while (cont1 <= cont2) {
                                    PesquisaFilial += cont1 + ", ";
                                    System.out.println(PesquisaFilial);
                                    cont1 = cont1 + 1;
                                    cont++;
                                }
                            }
                        }
                    }
                }
                int tam = PesquisaFilial.length();
                PesquisaFilial = PesquisaFilial.substring(0, tam - 2);

                System.out.println(PesquisaFilial);
            }

        } else if (usuario == null) {
            List<Filial> listFilial = new ArrayList<Filial>();
            String codigoFilial;
            FilialDAO filialDAO = null;
            if (filialDAO == null) {
                filialDAO = new FilialDAO();
            }
            listFilial = filialDAO.getFilias("", " AND CODEMP = 1  ");

            if (listFilial != null) {
                for (Filial filial : listFilial) {
                    codigoFilial = filial.getFilial().toString();
                    PesquisaFilial += codigoFilial + ", ";
                }
            }
            int tam = PesquisaFilial.length();
            PesquisaFilial = PesquisaFilial.substring(0, tam - 2);

            System.out.println(PesquisaFilial);
        }
        return PesquisaFilial;
    }

}
