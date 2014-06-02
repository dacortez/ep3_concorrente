package dacortez.diningSavages;

/**
 * Interface a ser implementada pelas threads que precisam ser
 * colocadas na fila de uma variável de condição com prioridade. 
 * 
 * @author Daniel Augusto Cortez
 * @version 02.06.2014
 */
public interface Rankable {
	int getRank();
}
