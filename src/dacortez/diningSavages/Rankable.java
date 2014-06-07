package dacortez.diningSavages;

/**
 * Interface a ser implementada pelas threads que precisam ser
 * colocadas na fila de uma variável de condição usando prioridades.
 * Prioridades mais altas (ranks) são colocadas no começo da fila. 
 * 
 * @author Daniel Augusto Cortez
 * @version 07.06.2014
 */
public interface Rankable {
	void setRank(int rank);
	int getRank();
}
