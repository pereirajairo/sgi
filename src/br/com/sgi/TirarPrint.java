/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author jairo.silva
 */
public class TirarPrint {

    public static void tirarPrint(String pCaminhoDoPrint) throws AWTException, IOException {
        pCaminhoDoPrint = "\\\\192.168.2.34\\Public\\ImagemCameraBalanca\\" + pCaminhoDoPrint + "";

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension tamanhoTela = toolkit.getScreenSize();
        Rectangle limitesTela = new Rectangle(tamanhoTela);
        Robot robot = new Robot();
        BufferedImage capturaDeTela = robot.createScreenCapture(limitesTela);
        ImageIO.write(capturaDeTela, "png", new File(pCaminhoDoPrint));

    }

}
