/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgi.ws;

import br.com.sgi.bean.NotaEntrada;
import br.com.sgi.bean.NotaEntradaItem;
import br.com.sgi.bean.SucataEcoParametros;
import br.com.sgi.bean.UsuarioWs;
import br.com.sgi.bean.WebServiceParametro;
import br.com.sgi.dao.WebServiceParametroDAO;
import br.com.sgi.frame.SucataEcoLancamento;
import br.com.sgi.main.Menu;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
 * @author matheus.luiz
 */
public class WsPedido {

    private static List<SucataEcoParametros> listSucataEcoParametros = new ArrayList<SucataEcoParametros>();
    private static List<SucataEcoLancamento> listSucataEcoParametro = new ArrayList<SucataEcoLancamento>();
    private static WebServiceParametro webServiceParametro = new WebServiceParametro();
    private static WebServiceParametroDAO webServiceParametroDAO = new WebServiceParametroDAO();
    private static SucataEcoParametros sucataEcoParametros = new SucataEcoParametros();

    private static String ip = "";
    private static String porta = "";
    private static String wsUsuario;
    private static String wsSenha;

    public UsuarioWs getuUsuarioWs(UsuarioWs usuarioWs) {
        int x = 1;
        return usuarioWs;
    }

    private static void CarregaParametroWebService() throws SQLException {
        UsuarioWs user = Menu.getUsuarioWs();

        webServiceParametro = webServiceParametroDAO.getWebServiceParametro();
        ip = webServiceParametro.getIpWebService();
        porta = webServiceParametro.getPortaWebService();
        wsUsuario = user.getNome();
        wsSenha = user.getSenha();

    }

    public static String gerarPedido() throws Exception {
        CarregaParametroWebService();

        String requestSoap;
        requestSoap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://services.senior.com.br\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ser:GravarPedidos_11>\n"
                + "         <user>jairo.silva</user>\n"
                + "         <password>jja146452jja</password>\n"
                + "         <encryption>0</encryption>\n"
                + "         <parameters>\n"
                + "           \n"
                + "            <pedido>\n"
                + "               <opeExe>I</opeExe>\n"
                + "               <codEmp>1</codEmp>\n"
                + "               <codFil>1</codFil>\n"
                + "               <codCli>9345</codCli>\n"
                + "               <tipPed>1</tipPed>\n"
                + "               <tnsPro>90124</tnsPro>\n"
                + "               <obsPed>Vendas</obsPed>\n"
                + "               <pedCli>1</pedCli>\n"
                + "               <prcPed>1</prcPed>\n"
                + "               <fecPed>N</fecPed>\n"
                + "               <pedBlo>N</pedBlo>\n"
                + "               <codCpg>99</codCpg>\n"
                + "               <codFor>9345</codFor>\n"
                + "               <sitPed>9</sitPed>\n"
                + "               <sigInt>ERBS</sigInt>               \n"
                + "               <pgtAnt>N</pgtAnt>\n"
                + "               <cifFob>F</cifFob>\n"
                + "               <temPar>N</temPar>\n"
                + "               <indSig>N</indSig>\n"
                + "               <acePar>N</acePar>  \n"
                + "               <ignorarErrosPedidos>N</ignorarErrosPedidos>  \n"
                + "               <ignorarErrosItens>N</ignorarErrosItens>  \n"
                + "               <inserirApenasPedidoCompleto>N</inserirApenasPedidoCompleto> \n"
                + "               <ignorarErrosParcela>N</ignorarErrosParcela> \n"
                + "               <IgnorarPedidoBloqueado>N</IgnorarPedidoBloqueado>             \n"
                + "               <produto>\n"
                + "                   <opeExe>I</opeExe>\n"
                + "                   <tnsPro>90124</tnsPro>\n"
                + "                   <codPro>P3CLV0001</codPro>\n"
                + "                   <codDer>.</codDer>\n"
                + "                   <qtdPed>10</qtdPed>\n"
                + "                   <codTpr>MET</codTpr>\n"
                + "                   <sitIpd>9</sitIpd>\n"
                + "               </produto>\n"
                + "               <produto>\n"
                + "                   <opeExe>I</opeExe>\n"
                + "                   <tnsPro>90124</tnsPro>\n"
                + "                   <codPro>P3CLV0001</codPro>\n"
                + "                   <codDer>.</codDer>\n"
                + "                   <qtdPed>1000</qtdPed>\n"
                + "                   <codTpr>MET</codTpr>\n"
                + "                   <sitIpd>9</sitIpd>\n"
                + "               </produto>\n"
                + "\n"
                + "            </pedido>\n"
                + "           \n"
                + "         </parameters>\n"
                + "      </ser:GravarPedidos_11>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        System.out.println(requestSoap);
        ip = "192.168.2.106";
        porta = "18080";

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        String url = "http://" + ip + ":" + porta + "/g5-senior-services/sapiens_Synccom_senior_g5_co_mcm_ven_pedidos";
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml");
        System.out.println(url);

        //fim da regiao a ser excluida caso o webservice nao possua a proprieade SOAPAction
        MessageFactory messageFactory = MessageFactory.newInstance();

        SOAPMessage msg = messageFactory.createMessage(headers, (new ByteArrayInputStream(requestSoap.getBytes())));

        SOAPMessage soapResponse = soapConnection.call(msg, url);
        Document xmlRespostaARequisicao = soapResponse.getSOAPBody().getOwnerDocument();
        System.out.println(passarXMLParaStringSapiens(xmlRespostaARequisicao, 4));//imprime na tela o xml de retorno.
        String x = passarXMLParaStringSapiens(xmlRespostaARequisicao, 4);
        int indexArroba = x.indexOf("<mensagemRetorno>");
        int indexEComercial = x.indexOf("</mensagemRetorno>");
        String resultado = x.substring(indexArroba + 17, indexEComercial);
        System.out.println(resultado);
        return resultado;

    }

    public static String passarXMLParaStringSapiens(Document xml, int espacosIdentacao) {
        try {
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            transfac.setAttribute("indent-number", new Integer(espacosIdentacao));
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
