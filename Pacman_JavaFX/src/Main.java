
import javax.swing.JFrame;

public class Main extends JFrame{

	public Main() {
		add(new Model());
	}
	
	public static void main(String[] args) {
		Main pacman = new Main();
		pacman.setVisible(true);
		pacman.setTitle("Pacman");
		pacman.setSize(496,545);
		pacman.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pacman.setLocationRelativeTo(null);
		
	}

}