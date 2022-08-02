/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import javax.swing.JOptionPane;

/**
 *
 * @author jairo.silva
 */
public class Mensagem {

    public static void mensagemRegistros(String tipomsg, String mensagem) {

        JOptionPane.showMessageDialog(null, "Atenção: " + mensagem,
                tipomsg + ":", JOptionPane.ERROR_MESSAGE);

    }

    public static void mensagem(String tipomsg, String mensagem) {

        switch (tipomsg) {
              case "OK":
                JOptionPane.showMessageDialog(null, "Atenção: " + mensagem,
                        tipomsg + ":", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "ERROR":
                JOptionPane.showMessageDialog(null, "Atenção: " + mensagem,
                        tipomsg + ":", JOptionPane.ERROR_MESSAGE);
                break;
            case "INSERT":
                JOptionPane.showMessageDialog(null, "Atenção: Registro inserido com sucesso",
                        tipomsg + ":", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "UPDATE":
                JOptionPane.showMessageDialog(null, "Atenção: Registro atualiado com sucesso",
                        tipomsg + ":", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "DELETE":
                JOptionPane.showMessageDialog(null, "Atenção: Registro excluído com sucesso",
                        tipomsg + ":", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                break;
        }

    }
}
