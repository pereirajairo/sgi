/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.integracao;

import br.com.sgi.bean.BalancaParametro;
import br.com.sgi.dao.BalancaParametroDAO;
import br.com.sgi.util.FormatarNumeros;
import br.com.sgi.frame.BalancinhaFabrica;
import br.com.sgi.frame.BalancinhaFabricachumbo;
import br.com.sgi.frame.CadastroCaixa;
import br.com.sgi.frame.SucataPDVLancamento;
import br.com.sgi.frame.SucataEcoLancamento;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jairo.silva
 */
public class Balancinhas {

    static SerialPort comPort;
    static String stringBuffer;

    public static void ClosePort() {

        if (comPort != null) {
            if (comPort.isOpen()) {
                comPort.closePort();
            }
        }
    }

    private static final class DataListener implements SerialPortDataListener {

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {

            //   System.out.println("In event listener.");
            if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                return;
            }
            //  System.out.println("Past event type check.");
            byte[] newData = new byte[comPort.bytesAvailable()];
            int numRead = comPort.readBytes(newData, newData.length);
            stringBuffer = new String(newData, 0, numRead);
            //  System.out.println("comPort "+comPort+" ");
            //   System.out.println("New Data " + newData + " ");
            // System.out.println("Read " + stringBuffer + " bytes.");
            //  System.out.println("Read " + numRead + " bytes.");

            String fraseInvertida = new StringBuilder(stringBuffer).reverse().toString();
            //JOptionPane.showMessageDialog(null, "PESO           " + fraseInvertida);

            if (stringBuffer.startsWith("=")) {
                String Concat = fraseInvertida.substring(0, 8);
                // System.out.println("Concat " + Concat + " ");
                int tam = Concat.length();
                String peso = Concat.substring(1, tam - 1);
                //  System.out.println("peso " + peso);
                peso = FormatarNumeros.removerPontos(peso);
                Double pesos = FormatarNumeros.converterToDouble(peso);
                pesos = pesos / 10;
                BalancinhaFabrica.getPesoBalanca(pesos.toString());
                CadastroCaixa.getPesoBalanca(pesos.toString());
                BalancinhaFabricachumbo.getPesoBalanca(pesos.toString());
                SucataPDVLancamento.getPesoBalanca(pesos.toString());
                SucataEcoLancamento.getPesoBalanca(pesos.toString());
                //  System.out.println("Pesos " + pesos);

            }
            if ((stringBuffer.startsWith("D")) || (stringBuffer.startsWith("B")) || (stringBuffer.startsWith("@")) || (stringBuffer.startsWith("M"))) {
                String Concat = stringBuffer.substring(0, 8);
                // System.out.println("Concat " + Concat + " ");
                int tam = Concat.length();
                String peso = Concat.substring(1, tam);
                //  System.out.println("peso " + peso);
                peso = FormatarNumeros.removerPontos(peso);
                Double pesos = FormatarNumeros.converterToDouble(peso);
                pesos = pesos / 10;
                BalancinhaFabrica.getPesoBalanca(pesos.toString());
                CadastroCaixa.getPesoBalanca(pesos.toString());
                BalancinhaFabricachumbo.getPesoBalanca(pesos.toString());
                SucataPDVLancamento.getPesoBalanca(pesos.toString());
                SucataEcoLancamento.getPesoBalanca(pesos.toString());
                // System.out.println("Pesos " + pesos);

            }

            if ((stringBuffer.startsWith("0,00"))) {
                String Concat = stringBuffer.substring(0, 9);
                // System.out.println("Concat " + Concat + " ");
                int tam = Concat.length();
                String peso = Concat.substring(1, tam);
                //  System.out.println("peso " + peso);
                peso = FormatarNumeros.removerPontos(peso);
                Double pesos = FormatarNumeros.converterToDouble(peso);
                pesos = pesos / 10;
                BalancinhaFabrica.getPesoBalanca(pesos.toString());
                CadastroCaixa.getPesoBalanca(pesos.toString());
                BalancinhaFabricachumbo.getPesoBalanca(pesos.toString());
                SucataPDVLancamento.getPesoBalanca(pesos.toString());
                SucataEcoLancamento.getPesoBalanca(pesos.toString());
                //  System.out.println("Pesos " + pesos);

            }
        }
    }

    static public void main(String[] args) throws SQLException {
        //comPort = SerialPort.getCommPorts()[0];

        Scanner in = null;
        String parametroCom = "COM1";

        BalancaParametroDAO balancaParametroDAO = new BalancaParametroDAO();
        BalancaParametro balancaParametro = new BalancaParametro();
        try {
            balancaParametro = balancaParametroDAO.getBalancaParametro();
            parametroCom = balancaParametro.getPortaCom();
        } catch (SQLException ex) {
            Logger.getLogger(BalancinhaFabrica.class.getName()).log(Level.SEVERE, null, ex);
        }

        comPort = SerialPort.getCommPort(parametroCom);
        comPort.openPort();
        System.out.println("COM port open: " + comPort.getDescriptivePortName() + " Porta serial: " + parametroCom + ".");
        DataListener listener = new DataListener();
        comPort.addDataListener(listener);
        System.out.println("Event Listener open.");

    }
}
