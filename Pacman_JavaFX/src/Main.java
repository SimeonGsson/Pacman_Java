
import javax.swing.JFrame;

/**
 * Huvudklassen för Pacman-spelet. Utökar JFrame för att skapa spelets fönster.
 */
public class Main extends JFrame{
	
	/**
     * Konstruerar en ny instans av Main. Initialiserar spelet genom att lägga till en ny instans av Model-klassen.
     */
	public Main() {
        add(new Model()); // Skicka med highscoreManager till din Model-klass
	}
	
	 /**
     * Huvudmetoden som skapar en instans av Main-klassen, ställer in spelets fönster och gör det synligt.
     *
     * @param args Kommandoradsargument (används inte i detta program).
     */
	 public static void main(String[] args) {
	        Main pacman = new Main();
	        pacman.setVisible(true);
	        pacman.setTitle("Pacman");
	        pacman.setSize(590, 555);
	        pacman.setDefaultCloseOperation(EXIT_ON_CLOSE);
	        pacman.setLocationRelativeTo(null);
	    
	    }
} 