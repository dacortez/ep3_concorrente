package dacortez.diningSavages;

/**
 * Classe que representa um cozinheiro do problema.  
 * 
 * @author Daniel Augusto Cortez
 * @version 02.06.2014
 */
public class Cook extends Thread {
	
	// Número de vezes que o cozinheiro encheu o pote
	private int totalFilled;
	
	// Pote (monitor) disponível ao cozinheiro
	private PotMonitor pot;
	
	public int getTotalFilled() {
		return totalFilled;
	}
	
	public void setPot(PotMonitor pot) {
		this.pot = pot;
	}
	
	public Cook(String name) {
		super(name);
		totalFilled = 0;
	}
	
	public void updateTotalFilled() {
		totalFilled++;
	}
	
	@Override
	public void run() {
		while (!pot.repetitionsIsFinished())
			pot.makePortions(this);
	}
	
	@Override
	public String toString() {
		return "Cozinheiro[" + getName() + ", " + totalFilled + "]"; 
	}
}
