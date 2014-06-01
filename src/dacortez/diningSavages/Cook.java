package dacortez.diningSavages;

public class Cook extends Thread {
	
	// Pote (monitor) disponível ao cozinheiro
	private PotMonitor pot;
	
	public Cook(String name) {
		super(name);
	}
	
	public void setPot(PotMonitor pot) {
		this.pot = pot;
	}
	
	@Override
	public void run() {
		while (true)
			pot.makePortions(this);
	}
}
