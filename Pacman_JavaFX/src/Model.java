
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener{
	
	private Dimension d; // Spelplanens storlek
	private final Font smallFont = new Font("Arial", Font.BOLD, 14); // Font för texten
	private boolean inGame = false;
	private boolean dying = false;
	
	private final int BLOCK_SIZE = 24; // Storleken på rutorna
	private final int N_BLOCKS = 15; // Antalet rutor per rad eller kolumn. Spelplanen kommer att vara 15x15
	private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // rutornas totala yta. Hur stor yta kommer dessa rutor ta upp? Spelplanen kommer dock vara 15x15
	private final int MAX_GHOSTS = 12; // Totala antalet spöken som kan finns med
	private final int PACMAN_SPEED = 6; // Hur snabb pacman ska vara från början - ta bort final om denna ska ändras under spelets gång
	
	private int N_GHOSTS = 6; // Antalet spöken som finns med från början
	private int lives, score;
	private int [] dx, dy; // Behövs för positionen för spökena
	private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // Behövs också för att veta antalet och positionen av spökena 
	
	private Image heart, ghost; // Ikoner för objekten
	private Image up, down, left, right; // Ikoner för pacman
	
	private int pacman_x, pacman_y, pacman_dx, pacman_dy; // Delta grejerna är riktningen för pacman som åberopas, de andra är positionen för pacman
	private int req_dx, req_dy; // Determined in the Tadapter class, extends KeyAdapter{} och hanterar inmatningen av tangenttryck
	
	private final int validSpeeds[] = {1,2,3,4,6,8}; // De olika hastigheterna som pacman och karaktärerna kan ha
	private final int maxSpeed = 6; 
	private int currentSpeed = 3; // Pacmands hastighet från början (behöver denna verkligen vara satt till 3? Med tanke på att den ändå kan byta värde när den initieras?
	private short [] screenData; // Tar in datan för att rita om spelet är något händer
	private Timer timer;
	
	private final short levelData[] = {
	    	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
	        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
	        25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
	        0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
	        19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
	        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
	        17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
	        21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
	        17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
	        25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
	    };
		// 0 = vägg, 1 = vägg till vänster, 2 = tak vägg, 4 = vägg till höger, 8 = golv vägg, 16 = ätbara platser
		// Gäller att plussa ihop dem för att få dem att fungera
	
	public Model() {
		loadImages();
		initVariables();
		addKeyListener(new TAdapter());
		setFocusable(true);
		initGame();
	}
	
	private void loadImages() {
		down = new ImageIcon("/Pacman_JavaFX/src/Images/down.gif").getImage();
		up = new ImageIcon("/Pacman_JavaFX/src/Images/up.gif").getImage();
		right = new ImageIcon("/Pacman_JavaFX/src/Images/right.gif").getImage();
		left = new ImageIcon("/Pacman_JavaFX/src/Images/left.gif").getImage();
		ghost = new ImageIcon("/Pacman_JavaFX/src/Images/ghost.gif").getImage();
		heart = new ImageIcon("/Pacman_JavaFX/src/Images/heart.gif").getImage();
	}
	
	private void initVariables() {
		screenData = new short[N_BLOCKS * N_BLOCKS];
		d = new Dimension(400, 400);
		ghost_x = new int [MAX_GHOSTS];
		ghost_dx = new int [MAX_GHOSTS];
		ghost_y = new int [MAX_GHOSTS];
		ghost_dy = new int [MAX_GHOSTS];
		ghostSpeed = new int [MAX_GHOSTS];
		dx = new int[4];
		dy = new int [4];
		
		timer = new Timer(40, this); // bestämmer hur ofta allt ritas om
		timer.start();
	}
	
	private void initGame() { // Spelet initieras - sätt ut startvärden som ska återställas vid spelstart
		lives = 3;
		score = 0;
		initLevel();
		N_GHOSTS = 6;
		currentSpeed = 3; // Pacmans hastighet initieras
	}
	
	private void initLevel() {
		int i;
		for (i = 0; i<N_BLOCKS * N_BLOCKS; i++) { // För varje ruta 15x15 = 225 st 
			screenData[i] = levelData[i]; // Kopiera det värdet på spelplan som representerar den rutan
		}
	}

	private void playGame(Graphics2d g2d) {
		
	}
	
	private void continueLevel() { // Definierar positionen av alla spöken och sätter ut en random hastighet till dem
		int dx = 1;
		int random;

		for (int i = 0; i < N_GHOSTS; i++) { // För varje spöke
			ghost_y[i] = 4 * BLOCK_SIZE;
			ghost_x[i] = 4 * BLOCK_SIZE;
			ghost_dy[i] = 0;
			ghost_dx[i] = dx;
			dx = -dx;
			random = (int) (Math.random() * (currentSpeed + 1));

			if (random > currentSpeed) {
				random = currentSpeed;
			}

			ghostSpeed[i] = validSpeeds[random]; // spökenas hastighet sätts ut fast randomized
		}
		
		pacman_x = 15 * BLOCK_SIZE;
		pacman_y = 15 * BLOCK_SIZE;
		pacman_dx = 0;
		pacman_dy = 0;
		req_dx = 0;
		req_dy = 0;
		dying = false;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);
		
		drawMaze(g2d);
		drawScore(g2d);
		
		if (inGame) {
			playGame(g2d);
			
		}else {
			showIntroScreen(g2d);
		}
		
		Toolkit.getDefaultToolkit().sync();


	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if (inGame) {
				if (key == KeyEvent.VK_LEFT) {
					req_dx = -1; // Vänster
					req_dy = 0;
				}
				else if (key == KeyEvent.VK_RIGHT) {
					req_dx = 1; // Höger
					req_dy = 0;
				}
				else if (key == KeyEvent.VK_UP) {
					req_dx = 0;
					req_dy = -1; // Upp
				}
				else if (key == KeyEvent.VK_DOWN) {
					req_dx = 0;
					req_dy = 1; // Ner
				}
				else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
					inGame = false; // Avsluta spelet genom att klicka på mellanslag
				}
			} else {
				if (key == KeyEvent.VK_SPACE) {
					inGame = true;
					initGame();
				}
			}
		}
	}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
