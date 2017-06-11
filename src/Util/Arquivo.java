package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Arquivo {

	private static Arquivo instance = null;
	private BufferedWriter escritor = null;
	private ArrayList<String> instrucoes = new ArrayList<String>();

	private Arquivo() {
	}

	public static Arquivo getInstance() {

		/**
		 * Singleton
		 */

		if (instance == null) {
			instance = new Arquivo();
		}
		return instance;
	}

	public ArrayList<String> getInstrucoes() {

		/**
		 * Retorna o array de instrucoes
		 */

		return this.instrucoes;
	}

	public void lerArquivo() {

		/**
		 * M�todo respons�vel por ler o arquivo e salvar as linhas referentes ao
		 * processo em um array chamado instrucoes
		 */

		BufferedReader leitor = null;

		try {

			leitor = new BufferedReader(new FileReader("entrada.txt"));

			String linha = "";

			while ((linha = leitor.readLine()) != null) {
				if (linha.length() > 0)
					instrucoes.add(linha);
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				leitor.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public ArrayList<Processo> getListProcessos() {

		/**
		 * M�todo respons�vel por traduzir o array de instrucoes em um array do
		 * tipo Processo
		 */

		// Vari�veis b�sicas necess�rias para ajudar no aux�lio da tradu��o
		String element = null;
		ArrayList<Processo> ListaProcessos = new ArrayList<>();
		Processo genericProcesso = new Processo();
		int tempoChegada = 0, tempoComputacao = 0, prioridade = 0, periodo = 0, deadline = 0;
		ArrayList<Integer> temposIO = null;
		List<String> list = null;
		String AuxIO = null;

		// chamada do m�todo lerArquivo() para preencher o array instrucoes com
		// o que for lido do arquivo
		lerArquivo();

		// para as intru��es lidas do arquivo, fa�a:
		for (byte k = 0; k < instrucoes.size(); k++) {

			// element recebe a instru��o apontada por k
			// Alt1 se transforma em um vetor da parti��o de element, usando " "
			// como ponto de parti��o
			element = instrucoes.get(k);
			String Alt1[] = element.split(Pattern.quote(" "));
			
			//verifica se cont�m 6 elementos dentro do vetor, se n�o retorna null
			if (Alt1.length == 6) {
				
				//para cada elemento do vetor fa�a
				for (byte j = 0; j < Alt1.length; j++) {

					switch (j) {

					case 0:

						tempoChegada = Integer.valueOf(Alt1[j]);

						break;
					case 1:

						tempoComputacao = Integer.valueOf(Alt1[j]);

						break;
					case 2:
						
						//AuxIO recebe o elemento para auxiliar no tratamento do mesmo
						AuxIO = Alt1[j];
						//verifica se a quantidade de caracteres � maior que dois (apenas a existencia dos colchetes) 
						if (AuxIO.length() > 2) {
							//list recebe array string da parti��o dos elementos por ";"
							list = Arrays.asList(AuxIO.substring(1, AuxIO.length() - 1).split(";"));
						
							//converter os elementos da lista em um array de inteiros
							for (byte i = 0; i < list.size(); i++) {
								temposIO.add(Integer.parseInt(list.get(i)));
							}
						} else {
							temposIO = null;
						}

						break;
					case 3:

						prioridade = Integer.valueOf(Alt1[j]);

						break;
					case 4:

						periodo = Integer.valueOf(Alt1[j]);

						break;
					case 5:

						deadline = Integer.valueOf(Alt1[j]);

						break;

					}

				}
				
				//associa os campos tratados em um processo gen�rico
				genericProcesso = new Processo(k, tempoChegada, tempoComputacao, temposIO, prioridade, periodo,
						deadline);
				//adiciona o processo generico a lista de processos
				ListaProcessos.add(genericProcesso);
				
				//reseta algumas informa��es
				list = null;
				temposIO = null;
				
			}else{
				return null;
			}
		}
		
		//retorna a lista de processos 
		return ListaProcessos;

	}

	public void arquivoSaida(String instrucao, String registradores) {
		
		/**
		 * M�todo respons�vel por escrever os dados no arquivo de sa�da
		 */

		try {
			if (new File("saida.txt").exists() == false) {

				new File("saida.txt").createNewFile();

			}

			escritor = new BufferedWriter(new FileWriter("saida.txt"));

			escritor.append("\r\n" + instrucao + "\r\n");
			escritor.append(registradores + "\r\n");
			escritor.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
