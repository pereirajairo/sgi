/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.util;

import br.com.sgi.bean.NotaFiscal;
import br.com.sgi.frame.SucataManualAuto;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author jairo.silva
 */
public class TratarXml {

    private NotaFiscal nota = new NotaFiscal();
    private SucataManualAuto veioCampo;
    private static DateFormat dateFormatHour = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    private String data_hora;

    public void GravarArquivo(String xml, String nome) throws Exception {
        System.out.println("/ -> " + new File("/").getCanonicalPath());
        System.out.println("… -> " + new File("…").getCanonicalPath());
        System.out.println(". -> " + new File(".").getCanonicalPath());
        String dir = new File(".").getCanonicalPath();
        try {
            data_hora = "_" + dateFormatHour.format(new Date()) + "";
            FileWriter file = new FileWriter(dir + "\\xml\\" + nome + data_hora + ".xml", true);
            //teste += "\n\r";
            file.write(xml);
            file.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro!", "Atenção!!!", JOptionPane.ERROR_MESSAGE);
        }

        //  deleteData(nome);
        lerarq(dir + "\\xml\\" + nome + data_hora + ".xml");

    }

    
    
    private void deleteData(String dir, String nome) {
        deleteFolder(dir + "\\xml\\" + nome + ".xml");
    }

    private static void deleteFolder(String path) {
        System.out.println(path);
        try {
            File file = new File(path);
            file.delete();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void lerarq(String xml) throws Exception {
        //Aqui você informa o nome do arquivo XML.  
        File f = new File(xml);

        //Criamos uma classe SAXBuilder que vai processar o XML  
        SAXBuilder sb = new SAXBuilder();

         //Este documento agora possui toda a estrutura do arquivo.  
        Document d;
        try {
            d = sb.build(f);
            // d = sb.build(xml);

            //Recuperamos o elemento root  
            Element nfe = d.getRootElement();

            //Recuperamos os atributos filhos (Attributes)  
            List atributes = nfe.getAttributes();
            Iterator i_atr = atributes.iterator();

            //Iteramos com os atributos filhos  
            while (i_atr.hasNext()) {
                Attribute atrib = (Attribute) i_atr.next();
                System.out.println("\nattribute de (" + nfe.getName() + "):" + atrib.getName() + " - valor: " + atrib.getValue());
            }
            //Recuperamos os elementos filhos (children)  
            List elements = nfe.getChildren();
            Iterator i = elements.iterator();

            //Iteramos com os elementos filhos, e filhos do dos filhos  
            while (i.hasNext()) {
                Element element = (Element) i.next();
                System.out.println("element:" + element.getName());
                trataElement(element);
            }

        } catch (JDOMException ex) {
            Logger.getLogger(TratarXml.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TratarXml.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void trataElement(Element element) throws Exception {
        List atributes = element.getAttributes();
        Iterator i_atr = atributes.iterator();
        int ncont = 0;

        while (i_atr.hasNext()) {
            Attribute atrib = (Attribute) i_atr.next();
            System.out.println("\nattribute de element.name (" + element.getName() + "): "
                    + "atrib.name" + atrib.getName() + " - atrib.valor: " + atrib.getValue());
          

        }

        List elements = element.getChildren();
        Iterator it = elements.iterator();
        //Iteramos com os elementos filhos, e filhos do dos filhos  
        while (it.hasNext()) {
            Element el = (Element) it.next();
            System.out.println("element(" + element.getName() + "):" + el.getName() + " - Valor: " + el.getText());
            if (el.getName().equals("codEmp")) {
                this.nota.setEmpresa(Integer.valueOf(el.getText().trim()));
            }
            if (el.getName().equals("codFil")) {
                this.nota.setFilial(Integer.valueOf(el.getText().trim()));
            }
            if (el.getName().equals("codSnf")) {
                this.nota.setSerie(el.getText().trim());
            }
            if (el.getName().equals("numNfv")) {
                this.nota.setNotafiscal(Integer.valueOf(el.getText().trim()));
            }
            if (el.getName().equals("retorno")) {
                this.nota.setSituacao(el.getText().trim());
            }
            trataElement(el);
        }
    }

    /**
     * @return the nota
     */
    public NotaFiscal getNota() {
        return nota;
    }

    /**
     * @param nota the nota to set
     */
    public void setNota(NotaFiscal nota) {
        this.nota = nota;
    }

}
