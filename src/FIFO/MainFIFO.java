package FIFO;

import java.util.ArrayList;

import Util.Arquivo;
import Util.Processo;
import Util.ProcessoSaida;

public class MainFIFO {

	private ArrayList<ProcessoSaida> ProcessosConcluidos = new ArrayList<>();
	private ArrayList<Processo> Prontos = Arquivo.getInstance().getListProcessos();
	private float ThroughPut = 0;

	public void FIFO() {

		this.executando();
		this.ThroughPUT();

	}

	public void pronto(int x) {

		for (Processo p : Prontos) { // conta o tempo de pronto para todos os
										// processos no array
			p.setTempoPronto(x);
		}

	}

	public void encerrado(Processo p) {

		int tPRONTO = p.TempoEstadoPronto(); // tempo total no estado pronto
		int tIO = p.TempoEstadoBloqueado(); // tempo total IO - bloqueado
		int tRESPOSTA = p.getStopPoint(); // tempo em que o PC finaliza o
											// processo
		int ID = p.getID(); // ID do processo

		ProcessoSaida pSAIDA = new ProcessoSaida(ID, tRESPOSTA, tIO, tPRONTO);

		this.ProcessosConcluidos.add(pSAIDA);

	}

	public void executando() {

		boolean chave = true;
		boolean fim = false;
		boolean executando = true;
		Processo p = new Processo();
		int PC = 0;
		int aux0 = 0;

		while (fim == false) {

			if (chave) { // siginifica que tem que pegar processo da fila de
							// pronto

				if (!Prontos.isEmpty()) { // enquanto prontos n�o estiver vazio
					p = Prontos.get(0);
					Prontos.remove(0); // Processo foi retirado da fila de
										// pronto e a fila � atualizada
					chave = false;

				} else { // fim da execu��o
					fim = true;
				}

			}
			if (!chave) { // fluxo seguinte

				while (executando) {

					if ((p.containsTempoIO(PC) == false) && (p.getTempoComputacao() > 0)) { // executando...

						this.pronto(PC); // conta pronto no array de prontos
						aux0 = p.getTempoComputacao();
						p.setTempoComputacao(aux0--); // decrementa tempo de
														// computa��o
						PC++; // conta PC

					} else if (p.containsTempoIO(PC) && (p.getTempoComputacao() > 0)) { // chamada
																						// i.o

						p.setBloqPoint(PC); // o tempo em PC cujo foi encontrado
											// o bloqueio no processo
						bloqueado(p); // passa pra bloqueado

					} else if (p.getTempoComputacao() == 0) { // acaba tempo de
																// computa��o

						p.setStopPoint(PC); // tempo em que foi concluido
						encerrado(p); // constr�i o processo final e finaliza o
										// processo
						executando = false; // faz sair do loop de execu��o
						chave = true; // faz solicitar novo processo a fila de
										// pronto

					}

				}

			}

		}

	}

	public void bloqueado(Processo b) {

		int num = b.getBloqPoint(); // pega o ponto de parada do pc
		ArrayList<Integer> temposIO = b.getTemposIO(); // pega os tempos de i/o
		boolean key = true;

		while (key == true) {
			if (key == true && temposIO.contains(num)) { // enquanto cont�m o
															// valor no array
															// este valor �
															// removido
				temposIO.remove(num);
				num++;
			} else {
				key = false;
			}

		}

		b.setTemposIO(temposIO); // seta o array de temposIO no processo
		this.Prontos.add(b); // manda o processo para o final da fila de prontos

	}
	

	public void ThroughPUT() {
		
		/**
		 * Calcula o Throughput de fifo
		 * Soma dos tempos de conclus�o de cada processo
		 * dividido pelo n�mero de processos conclu�dos
		 */

		int aux1 = 0;
		int aux2 = 0;

		for (ProcessoSaida p : this.ProcessosConcluidos) {
			aux1 = aux1 + p.getTempoResposta();
		}
		aux2 = this.ProcessosConcluidos.size();

		this.ThroughPut = aux1 / aux2;

	}

	public float getFIFOThroughPUT() {
		
		return this.ThroughPut;
		
	}

}
