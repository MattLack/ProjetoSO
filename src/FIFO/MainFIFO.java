package FIFO;

import java.util.ArrayList;

import Util.Arquivo;
import Util.Processo;
import Util.ProcessoSaida;

public class MainFIFO {
	
	private static MainFIFO instance = null;
	private ArrayList<ProcessoSaida> ProcessosConcluidos = new ArrayList<>();
	private ArrayList<Processo> Prontos = Arquivo.getInstance().getListProcessos();
	private float ThroughPut = 0;

	private MainFIFO() {
	}

	public static MainFIFO getInstance() {

		/**
		 * Singleton
		 */

		if (instance == null) {
			instance = new MainFIFO();
		}
		return instance;
	}
	
	public void FIFO() {

		this.executando();
		this.ThroughPUT();
		
		Arquivo.getInstance().arquivoSaida("");
		Arquivo.getInstance().arquivoSaida("FIFO");
		Arquivo.getInstance().arquivoSaida("ThroughPut: "+this.getFIFOThroughPUT());
		for (ProcessoSaida processoSaida : ProcessosConcluidos) {
			Arquivo.getInstance().arquivoSaida(processoSaida.toString());
		}
		

	}

	public void pronto(int x) {
		//System.out.println("entrou pronto");
		
		for(int i =0; i< Prontos.size(); i++){
			Prontos.get(i).setTempoPronto(x);
			//System.out.println("i:"+i+","+ Prontos.get(i).getTempoPronto().toString());
		}
		
		
		

	}

	public void encerrado(Processo p) {
		
		int tPRONTO = p.getTempoPronto().size(); // tempo total no estado pronto
		int tIO = p.TempoEstadoBloqueado(); // tempo total IO - bloqueado
		int tRESPOSTA = p.getStopPoint(); // tempo em que o PC finaliza o
											// processo
		int ID = p.getID(); // ID do processo

		ProcessoSaida pSAIDA = new ProcessoSaida(ID, tRESPOSTA, tIO, tPRONTO);

		this.ProcessosConcluidos.add(pSAIDA);
		System.out.println("encerrou "+pSAIDA.getID());

	}

	public void executando() {
		
		System.out.println("entrou executando");

		boolean chave = true;
		boolean fim = false;
		boolean executando = true;
		Processo p = new Processo();
		int PC = 0;
		int aux0 = 0;
		
		this.pronto(PC); // conta pronto no array de prontos

		while (fim == false) {

			if (chave) { // siginifica que tem que pegar processo da fila de
							// pronto

				if (!Prontos.isEmpty()) { // enquanto prontos n�o estiver vazio
					p = new Processo(Prontos.get(0).getID(), Prontos.get(0).getTempoChegada(),
							Prontos.get(0).getTempoComputacao(), Prontos.get(0).getTemposIO(),
							Prontos.get(0).getPrioridade(), Prontos.get(0).getPeriodo(), Prontos.get(0).getDeadline());
					p.setBloqPoint(Prontos.get(0).getBloqPoint());
					p.setDeadline(Prontos.get(0).getDeadline());
					p.setTPRONT(Prontos.get(0).getTempoPronto());
					if(Prontos.get(0).TempoEstadoBloqueado() == -1){
						p.setTBLOQ(Prontos.get(0).getTemposIO().size());
					}else{
						p.setTBLOQ(Prontos.get(0).TempoEstadoBloqueado());
					}
					Prontos.remove(0); // Processo foi retirado da fila de
										// pronto e a fila � atualizada
					chave = false;
					executando = true;

				} else { // fim da execu��o
					fim = true;
				}

			}
			if (!chave) { // fluxo seguinte

				while (executando) {

					if ((p.containsTempoIO(PC) == false) && (p.getTempoComputacao() > 0)) { // executando...

						aux0 = p.getTempoComputacao();
						aux0--;
						p.setTempoComputacao(aux0); // decrementa tempo de
														// computa��o
						PC++; // conta PC
						this.pronto(PC); // conta pronto no array de prontos
						
					} else if (p.containsTempoIO(PC) && (p.getTempoComputacao() > 0)) { // chamada
																						// i.o

						p.setBloqPoint(PC); // o tempo em PC cujo foi encontrado
											// o bloqueio no processo
						bloqueado(p); // passa pra bloqueado
						executando = false; // faz sair do loop de execu��o
						chave = true; // faz solicitar novo processo a fila de
										// pronto

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
		
		System.out.println("entrou bloqueado");

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
		 * Calcula o Throughput de fifo Soma dos tempos de conclus�o de cada
		 * processo dividido pelo n�mero de processos conclu�dos
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
