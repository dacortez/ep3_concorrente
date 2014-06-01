package dacortez.diningSavages;

public class Cook extends Thread {
	
	// Pote (monitor) disponível ao cozinheiro
	private PotMonitor pot;
		
	public void setPot(PotMonitor pot) {
		this.pot = pot;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				pot.makePortions(this);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
