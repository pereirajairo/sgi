package br.com.sgi;
import br.com.sgi.ArquivoTextoEscrita;


public class Criptografe {
	
	private char[] char_text;
	private int[] int_text;
	
	//NESTE M�TODO IREMOS DESCRIPTOGRAFAR O TEXTO QUE EST� CRIPTOGRAFADO
	//A VARI�VEL 'LINHA' EST� RECEBENDO A LINHA DE TEXTO GRAVADO NO ARQUIVO
	//A VETOR 'TXT' RECEBE OS CARACTERES QUE EST�O SENDO DIVIDAS POR ESPA�O
	//LOGO DEPOIS, � FEITO UM FOR PARA O INT_TEXT RECEBER O INTEIRO DO VETOR STRING 'TXT'
	//O CHAR_TEXT IR� RECEBER O CARACTERE, CONFORME A TABELA ASCII DO INT_TEXT
	//O NEW_TEXT IR� AUTO INCREMENTANDO AT� RECEBER TODAS AS LETRAS DE UMA LINHA DE TEXTO
	//DEPOIS � ESCRITO NO NOVO ARQUIVO O TEXTO DESCRIPTOGRAFADO
	public void descriptografar(String nome, int qtde, String decod) {
		ArquivoTextoEscrita type = new ArquivoTextoEscrita();
		ArquivoTextoLeitura read = new ArquivoTextoLeitura();
		char_text = new char[qtde];
		int_text = new int[qtde];
		int i = 0;
		String new_text = "", linha;
		
		type.abrirArquivo(decod);
		read.abrirArquivo(nome);
		
		do {
			linha = read.ler();
			if(linha != null) {
				String[] txt = linha.split(" ");
				for(i = 0; i < txt.length; i++) {
				
					int_text[i] = Integer.parseInt(txt[i]);
					char_text[i] = (char)int_text[i];
					new_text = new_text + char_text[i];
				}
				type.escrever(new_text);
				new_text = "";//SE TIRAR ESSA LINHA, VAI VER O QUE VAI ACONTECER
			}
		}while(linha != null);
		
		type.fecharArquivo();
		read.fecharArquivo();
	}

	//NESTE M�TODO IREMOS CRIPTOGRAFAR O TEXTO ORIGINAL
	//A VARI�VEL 'LINHA' EST� RECEBENDO A LINHA DE TEXTO GRAVADO NO ARQUIVO
	//O CHAR_TEXT IR� RECEBER UM CARACTERE POR VEZ
	//O INT_TEXT VAI RECEBER O NUMERO DO CARACTERE, CONFORME A TABELA ASCII
	//O NEW_TEXT IR� AUTO INCREMENTANDO AT� RECEBER TODAS OS N�MEROS DE UMA LINHA DE TEXTO
	//DEPOIS � ESCRITO NO NOVO ARQUIVO O TEXTO CRIPTOGRAFADO
	public void criptografar(String name_arc, int qtde, String cod) {
		ArquivoTextoEscrita type = new ArquivoTextoEscrita();
		ArquivoTextoLeitura read = new ArquivoTextoLeitura();
		char_text = new char[qtde];
		int_text = new int[qtde];
		int i = 0, j = 0;
		String new_text = "", linha;
		
		type.abrirArquivo(cod);
		read.abrirArquivo(name_arc);
		
		do {
			
			linha = read.ler();
			
			if(linha != null) {
			
				for(i = 0; i < linha.length(); i++) {
			
					char_text[j] = linha.charAt(i);
					int_text[j] = (int)char_text[j];
					new_text = new_text + Integer.toString(int_text[j]) + " ";
					j++;
				}
			type.escrever(new_text);
			new_text = ""; //SE TIRAR ESSA LINHA, VAI VER O QUE VAI ACONTECER
			}
		}while(linha != null);
		
		type.fecharArquivo();
		read.fecharArquivo();
	}

}
