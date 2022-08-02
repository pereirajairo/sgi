/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.swing.JOptionPane;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author jairosilva
 */
public class WSEnviarEmail {

    public static String e(String remetente, String assunto, String corpo, String destinatario, String anexo, String nomeAnexo) throws SOAPException, IOException {
        JOptionPane.showMessageDialog(null, "kct" + anexo);
        String nossaSenhora = "vai";
        String requestSoap;//requisicao/request no formato xml, repare que isto foi copiado da regiao destacada em azul na figura 1

        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "  <soapenv:Body>\n"
                + "    <ser:SendMail>\n"
                + "      <user>procauto</user>\n"
                + "      <password>3n3rg14</password>\n"
                + "      <encryption>0</encryption>\n"
                + "      <parameters>\n"
                + "        <pmCCAddress>" + remetente + "</pmCCAddress>\n"
                + "        <pmSubject>" + assunto + "</pmSubject>\n"
                + "        <pmBody>" + corpo + "</pmBody>\n"
                + "        <pmToAddress>" + destinatario + "</pmToAddress>\n"
                + "        <pmCCOAddress></pmCCOAddress>\n"
                + "        <pmIsHtml>F</pmIsHtml>\n"
                + "        <pmAttachFile>" + anexo + "</pmAttachFile>\n"
                + "        <pmAttachName>" + nomeAnexo + "</pmAttachName>\n"
                + "      </parameters>\n"
                + "    </ser:SendMail>\n"
                + "  </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        // System.out.println(requestSoap);

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        String url = "http://192.168.2.193:18080/g5-senior-services/sapiens_Synccom_senior_g5_co_ger_mcserviceemail";//url do webservice nao e a url do wsdl do webservice, repare que isto foi copia da parte vermelha da figura 1

        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml; charset=UTF-8");
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));
        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        return passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        // return  "ok";
    }

    public static String enviarEmail(String remetente, String assunto, String corpo, String destinatario, String anexo, String nomeAnexo) throws Exception {
        // TODO code application logic here
        // TODO code application logic here
        String nossaSenhora = "vai";
        String requestSoap;//requisicao/request no formato xml, repare que isto foi copiado da regiao destacada em azul na figura 1

        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "  <soapenv:Body>\n"
                + "    <ser:SendMail>\n"
                + "      <user>procauto</user>\n"
                + "      <password>3n3rg14</password>\n"
                + "      <encryption>0</encryption>\n"
                + "      <parameters>\n"
                + "        <pmCCAddress>" + remetente + "</pmCCAddress>\n"
                + "        <pmSubject>" + assunto + "</pmSubject>\n"
                + "        <pmBody>" + corpo + "</pmBody>\n"
                + "        <pmToAddress>" + destinatario + "</pmToAddress>\n"
                + "        <pmCCOAddress></pmCCOAddress>\n"
                + "        <pmIsHtml>F</pmIsHtml>\n"
                + "        <pmAttachFile>" + anexo + "</pmAttachFile>\n"
                + "        <pmAttachName>" + nomeAnexo + "</pmAttachName>\n"
                + "      </parameters>\n"
                + "    </ser:SendMail>\n"
                + "  </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        // System.out.println(requestSoap);

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        String url = "http://192.168.2.193:18080/g5-senior-services/sapiens_Synccom_senior_g5_co_ger_mcserviceemail";//url do webservice nao e a url do wsdl do webservice, repare que isto foi copia da parte vermelha da figura 1

        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml; charset=UTF-8");
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));
        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        return passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);

    }

    public static String passarXMLParaStringSapiens(Document xml, int espacosIdentacao) {
        try {
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            //transfac.setAttribute("indent-number", new Integer(espacosIdentacao));
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource((Node) xml);
            trans.transform(source, result);
            String xmlString = sw.toString();

            System.out.println(xmlString);
            return xmlString;
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
