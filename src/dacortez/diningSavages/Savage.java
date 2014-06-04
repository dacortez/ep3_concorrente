package dacortez.diningSavages;

/**
 * Classe que representa um selvagem do problema. 
 * 
 * @author Daniel Augusto Cortez
 * @version 02.06.2014
 */
public class Savage extends Thread implements Rankable {
	
	// Peso do selvagem
	private int weight;
	
	// Número de vezes que o cozinheiro encheu o pote
	private int totalEaten;
	
	// Pote (monitor) disponível ao selvagem
	private PotMonitor pot;
	
	public int getTotalEaten() {
		return totalEaten;
	}
	
	public void setPot(PotMonitor pot) {
		this.pot = pot;
	}
	
	public Savage(String name, int weight) {
		super(name);
		this.weight = weight;
		totalEaten = 0;
	}
	
	public void updateTotalEaten() {
		++totalEaten;
	}
	
	@Override
	public void run() {
		while (!pot.isFinished())
			pot.eatPortion(this);
	}
	
	@Override
	public int getRank() {
		return weight;
	}
	
	@Override
	public String toString() {
		return "Selvagem[" + getName() + ", " + weight + ", " + totalEaten + "]"; 
	}
}
