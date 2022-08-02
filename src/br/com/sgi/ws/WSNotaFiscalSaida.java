/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

import br.com.sgi.bean.SucataMovimento;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
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
public class WSNotaFiscalSaida {

    public static String chamarMetodoWsXmlHttpSapiens(SucataMovimento suc, String usuario, String senha) throws Exception {
        // TODO code application logic here
        // TODO code application logic here

        double valor_parcela = suc.getPesofaturado() * suc.getPrecounitario();
        // integrar.executar("procauto", "3n3rg14");

        String requestSoap;//requisicao/request no formato xml, repare que isto foi copiado da regiao destacada em azul na figura 1
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "     <ser:GravarNotasFiscaisSaida>\n"
                + "         <user>" + usuario + "</user>\n"
                + "         <password>" + senha + "</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "            <dadosGerais>\n"
                + "             <codEmp>1</codEmp>\n"
                + "             <codFil>" + suc.getFilial().toString().trim() + "</codFil>\n"
                + "             <codSnf>" + suc.getSerie() + "</codSnf>\n"
                + "             <codCli>" + suc.getCliente().toString().trim() + "</codCli>\n"
                + "             <tipNfs>9</tipNfs>\n"
                + "             <datEmi>" + suc.getDatageracaoS() + "</datEmi>\n"
                + "             <tnsPro>" + suc.getTransacao_complemento().trim() + "</tnsPro>\n"
                + "             <codEdc>55</codEdc>\n"
                + "             <codCpg>" + suc.getCondicao_pgto_complemento() + "</codCpg>\n"
                + "             <vlrFin>" + valor_parcela + "</vlrFin>"
                + "             <produtos>\n"
                + "                    <seqIpv>1</seqIpv>\n"
                + "                    <tnsPro>" + suc.getTransacao_complemento().trim() + "</tnsPro>\n"
                + "                    <codPro>COIMP0002</codPro>\n"
                + "                    <uniMed>UN</uniMed>\n "
                + "                    <qtdFat>" + suc.getPesofaturado() + "</qtdFat>\n"
                + "                    <preUni>" + suc.getPrecounitario() + "</preUni>\n"
                + "                    <vlrFin>" + valor_parcela + "</vlrFin>\n"
                + "             </produtos>\n"
                + "            </dadosGerais>\n"
                + "            <fecNot>1</fecNot>\n"
                + "            <gerarDocumentoEletronico>1</gerarDocumentoEletronico>\n"
                + "            <tipoProcessamento>1</tipoProcessamento>\n"
                + "         </parameters>\n"
                + "      </ser:GravarNotasFiscaisSaida>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        // System.out.println(requestSoap);

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        String url = "http://192.168.2.105:28080/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_ven_notafiscal";//url do webservice nao e a url do wsdl do webservice, repare que isto foi copia da parte vermelha da figura 1

        // String url = "http://192.168.2.193:18080/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_ven_notafiscal";//url do webservice nao e a url do wsdl do webservice, repare que isto foi copia da parte vermelha da figura 1
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");
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
            // transfac.setAttribute("indent-number", new Integer(espacosIdentacao));
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource((Node) xml);
            trans.transform(source, result);
            String xmlString = sw.toString();
            return xmlString;
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
