
import javax.swing.JFrame;

/**
 * The main class for the Pacman game. Extends JFrame to create the game window.
 */
public class Main extends JFrame{

	 /**
     * Constructs a new Main object. Initializes the game by adding a new instance of the Model class.
     */
	public Main() {
		add(new Model());
	}
	
	 /**
     * The main method that creates an instance of the Main class, sets up the game window, and makes it visible.
     *
     * @param args Command-line arguments (not used in this application).
     */
	public static void main(String[] args) {
		Main pacman = new Main();
		pacman.setVisible(true);
		pacman.setTitle("Pacman");
		pacman.setSize(590,555);
		pacman.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pacman.setLocationRelativeTo(null);
		
	}

} 