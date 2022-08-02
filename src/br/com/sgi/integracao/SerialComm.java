/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.integracao;

import br.com.sgi.bean.BalancaParametro;
import br.com.sgi.dao.BalancaParametroDAO;
import br.com.sgi.frame.IntegrarPesosRegistrar;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author jairo.silva
 */
public class SerialComm implements SerialPortEventListener {

    InputStream inputStream;

    public void execute() throws SQLException {

        String portName = getPortNameByOS();

        CommPortIdentifier portId = getPortIdentifier(portName);
        if (portId != null) {

            try {
                SerialPort serialPort = (SerialPort) portId.open(this.getClass().getName(), 2000);

                inputStream = serialPort.getInputStream();

                serialPort.addEventListener(this);

                serialPort.notifyOnDataAvailable(true);

                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

            } catch (PortInUseException e) {
                System.out.println("br.com.recebimento.integracao.SerialComm.execute()\n" + e.getMessage());
            } catch (IOException e) {
                System.out.println("br.com.recebimento.integracao.SerialComm.execute()\n" + e.getMessage());
            } catch (UnsupportedCommOperationException e) {
                e.printStackTrace();
                System.out.println("br.com.recebimento.integracao.SerialComm.execute()\n" + e.getMessage());
            } catch (TooManyListenersException e) {
                System.out.println("br.com.recebimento.integracao.SerialComm.execute()\n" + e.getMessage());
                
            }

        } else {
            System.out.println("Porta Serial não disponível");
            IntegrarPesosRegistrar.getPesoBalanca("ERRO");
        }
    }

    /**
     * Get The port name
     *
     */
    private String getPortNameByOS() throws SQLException {

        Scanner in = null;
        String parametroCom = "COM1";

        BalancaParametroDAO balancaParametroDAO = new BalancaParametroDAO();
        BalancaParametro balancaParametro = new BalancaParametro();

        balancaParametro = balancaParametroDAO.getBalancaParametro();
        parametroCom = balancaParametro.getPortaCom();

        String osname = System.getProperty("os.name", "").toLowerCase();
        if (osname.startsWith("windows")) {
            // windows
            //  return "COM3";
            return parametroCom;
        } else if (osname.startsWith("linux")) {
            // linux
            // return "/dev/ttyS0";
            return parametroCom;
        } else if (osname.startsWith("mac")) {
            // mac
            //   return "???";
            return parametroCom;
        } else {
            IntegrarPesosRegistrar.getPesoBalanca("ERRO");
            System.out.println("Sorry, your operating system is not supported");
            System.exit(1);
            return null;
        }

    }

    /**
     * Get the Port Identifier
     *
     */
    private CommPortIdentifier getPortIdentifier(String portName) {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        Boolean portFound = false;
        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println("Available port: " + portId.getName());
                if (portId.getName().equals(portName)) {
                    System.out.println("Found port: " + portName);
                    portFound = true;
                    return portId;
                }
            }

        }

        return null;

    }

    private frmBalanca balanca;

    public void serialEvent(SerialPortEvent event) {

        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[20];
                try {
                    int numBytes = 0;
                    while (inputStream.available() > 0) {
                        numBytes = inputStream.read(readBuffer);
                    }
                    String result = new String(readBuffer);
                    result = result.substring(1, numBytes);
                    System.out.println("Read: " + result);

                    result = result.trim();

                    String pesoPegado = result.substring(result.length() - 1);
                    System.out.println("peso pegado " + pesoPegado);
                    String pesos = "";
                    int tam = result.length() - 2;

                    if (pesoPegado.equals("g")) {
                        pesos = result.substring(result.lastIndexOf("kg") - 5);
                        pesos = pesos.substring(0, 5);
                        System.out.println("pesar  " + pesos);
                        IntegrarPesosRegistrar.getPesoBalanca(pesos);
                    }
                } catch (IOException e) {
                    IntegrarPesosRegistrar.getPesoBalanca("ERRO");
                }
                break;
        }
    }

}
