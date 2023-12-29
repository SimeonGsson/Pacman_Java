
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
	private final Font smallFont = new Font("Arial", Font.BOLD, 17); // Font för texten
	private final Font bigFont = new Font("Arial", Font.BOLD, 25); // Font för GameOver
	private boolean inGame = false;
	private boolean dying = false;

	private final int BLOCK_SIZE = 24; // Storleken på rutorna
	private final int N_BLOCKS = 20; // Antalet rutor per rad eller kolumn. Spelplanen kommer att vara 15x15
	private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // rutornas totala yta. Hur stor yta kommer dessa rutor ta upp? Spelplanen kommer dock vara 15x15
	private final int MAX_GHOSTS = 12; // Totala antalet spöken som kan finns med
	private final int PACMAN_SPEED = 4; // Hur snabb pacman ska vara från början - ta bort final om denna ska ändras under spelets gång

	private int N_GHOSTS = 3; // Antalet spöken som finns med från början
	private int lives, score;
	private int [] dx, dy; // Behövs för positionen för spökena
	private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // Behövs också för att veta antalet och positionen av spökena 

	private Image heart, ghost; // Ikoner för objekten
	private Image up, down, left, right; // Ikoner för pacman

	private int pacman_x, pacman_y, pacman_dx, pacman_dy; // Delta grejerna är riktningen för pacman som åberopas, de andra är positionen för pacman
	private int req_dx, req_dy; // Determined in the Tadapter class, extends KeyAdapter{} och hanterar inmatningen av tangenttryck

	private GameMap gameMap;
	private Ghost ghostclass;
	private Pacman pacmanclass;
	private short [] selectedMap;

	private final int validSpeeds[] = {1,2,3,4,6,8}; // De olika hastigheterna som pacman och karaktärerna kan ha
	private final int maxSpeed = 6; 

	private int currentSpeed = 2; // Pacmands hastighet från början (behöver denna verkligen vara satt till 3? Med tanke på att den ändå kan byta värde när den initieras?
	private short [] screenData; // Tar in datan för att rita om spelet är något händer
	private Timer timer;

	private boolean gameOver = false;
	private boolean gameStarted = false;


	public Model() {
		loadImages();
		initVariables();
		addKeyListener(new TAdapter());
		gameMap = new GameMap(N_BLOCKS, screenData);
		selectedMap = gameMap.getMapOne(); // Dehär blir default map så att det funkar att välja mellan två olika
		pacmanclass = new Pacman(screenData, N_BLOCKS, this, ghost_x, ghost_y, dying, N_GHOSTS, inGame);
		ghostclass = new Ghost(screenData, N_BLOCKS, N_GHOSTS, BLOCK_SIZE, ghost_x, ghost_y, ghost_dx, ghost_dy, dx, dy, ghostSpeed);
		setFocusable(true);
		initGame();
	}
	
	public short[] getCurrentMap(short[] currentMap) {
		selectedMap = currentMap;
		return selectedMap;
	}


	private void loadImages() {
		ghost = new ImageIcon("C:\\Users\\simeo\\Downloads\\ghost.gif").getImage();
		heart = new ImageIcon("C:\\Users\\simeo\\Downloads\\heart.png").getImage();
	}


	private void initVariables() {
		screenData = new short[N_BLOCKS * N_BLOCKS];
		d = new Dimension(500, 540);
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


	private void playGame(Graphics2D g2d) { // funktionen som håller igång spelet.
		if (dying) {
			death();
			dying = false;
		}
		pacmanclass.move();
		dying = pacmanclass.getDeath();
		ghostclass.move();
		checkMaze();

	}


	public void showIntroScreen(Graphics2D g2d) {
		String start = "Tryck SPACE för att starta :)";
		String map = "Välkommen! Klicka på 1 eller 2 för att byta bana";
		g2d.setColor(Color.black);
		g2d.drawRect(10, 100, 460, 100);
		g2d.fillRect(10, 100, 460, 100);
		g2d.drawRect(10, 270, 460, 100);
		g2d.fillRect(10, 270, 460, 100);
		g2d.setColor(Color.orange);
		g2d.drawString(map, (SCREEN_SIZE) / 9, 150);
		g2d.drawString(start, (SCREEN_SIZE) / 4, 325);
	}


	public void drawScore(Graphics2D g) {
		g.setFont(smallFont);
		g.setColor(Color.orange);
		String s = "Score: " + score;
		g.drawString(s,  SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

		for (int i = 0; i < lives; i++) {
			g.drawImage(heart,  i * 28 + 8, SCREEN_SIZE +1, this);
		}
	}


	public void drawGameOver(Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.drawRect(10, 100, 460, 100);
		g2d.fillRect(10, 100, 460, 100);
		g2d.setFont(bigFont);
		g2d.setColor(Color.red);
		String s = "SPELET ÄR ÖVER";
		g2d.drawString(s,  135, 150);
		g2d.setFont(smallFont);
		g2d.setColor(Color.orange);
		String s2 = "Tryck SPACE för att starta spelet igen";
		g2d.drawString(s2, 105, 172);
		
	}


	public void checkMaze() {
		int i = 0;
		boolean finished = true;

		// Check for the presence of pellets (represented by the number 16)
		for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
			if ((screenData[i] & 16) !=0) {
				finished = false;
				break;
			}
		}

		if (finished) {
			score += 50;

			if (N_GHOSTS < MAX_GHOSTS) {
				N_GHOSTS++;
			}
			if (currentSpeed < maxSpeed) {
				currentSpeed++;
			}
			initLevel();
		}
	}


	private void death() {
		lives--;

		if (lives == 0) {
			gameOver = true;
			inGame = false;
		}

		continueLevel();
	}
	

	public void incrementScore() {
		score++;
	}


	private void initGame() { // Spelet initieras - sätt ut startvärden som ska återställas vid spelstart
		lives = 3;
		score = 0;
		initLevel();
		N_GHOSTS = 4;
		currentSpeed = 2; // Pacmans hastighet initieras
	}


	private void initLevel() {
		int i;
		for (i = 0; i<N_BLOCKS * N_BLOCKS; i++) { // För varje ruta 15x15 = 225 st 
			screenData[i] = selectedMap[i]; // Kopiera det värdet på spelplan som representerar den rutan
		}

		continueLevel();
	}

	
	private void continueLevel() { // Definierar positionen av alla spöken och sätter ut en random hastighet till dem
		int dx = 1;
		int random;

		for (int i = 0; i < N_GHOSTS; i++) { // För varje spöke
			ghost_y[i] = 2 * BLOCK_SIZE;
			ghost_x[i] = 2 * BLOCK_SIZE;  
			ghost_dy[i] = 0;
			ghost_dx[i] = dx;
			dx = -dx;
			random = (int) (Math.random() * (currentSpeed + 1));

			if (random > currentSpeed) {
				random = currentSpeed;
			}

			ghostSpeed[i] = validSpeeds[random]; // spökenas hastighet sätts ut fast randomized
		}

		pacmanclass.setX(17 * BLOCK_SIZE);
		pacmanclass.setY(17 * BLOCK_SIZE);
		pacmanclass.setDX(0);
		pacmanclass.setDY(0);
		pacmanclass.setReq_dx(0);
		pacmanclass.setReq_dy(0);
		pacmanclass.resetDeath();
		dying = false;
	}

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);

		gameMap.draw(g2d);
		drawScore(g2d);
		pacmanclass.drawPac(g2d);
		ghostclass.draw(g2d, ghost_x, ghost_y);


		if (inGame) {
			playGame(g2d);
		}else if(gameOver) { 
			drawGameOver(g2d);
		}else if(!gameStarted) {
			showIntroScreen(g2d);
		}

		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}


	class TAdapter extends KeyAdapter {
	    public void keyPressed(KeyEvent e) {
	        int key = e.getKeyCode();
	        char keyChar = e.getKeyChar();

	        if (Character.isDigit(keyChar) && !inGame) {
	            int mapNumber = Character.getNumericValue(keyChar);
	            switch (mapNumber) {
	                case 1:
	                	System.out.println("Key 1 pressed");
	                    getCurrentMap(gameMap.getMapOne());
	                    initLevel();
	                    break;
	                case 2:
	                	System.out.println("Key 2 pressed");
	                    getCurrentMap(gameMap.getMapTwo());
	                    initLevel();
	                    break;
	                // add more cases if you have more maps
	                default:
	                    break;
	            }
	        }

			if (inGame) {
				if (key == KeyEvent.VK_LEFT) {
					pacmanclass.setReq_dx(-1);
					pacmanclass.setReq_dy(0);
				} else if (key == KeyEvent.VK_RIGHT) {
					pacmanclass.setReq_dx(1);
					pacmanclass.setReq_dy(0);
				} else if (key == KeyEvent.VK_UP) {
					pacmanclass.setReq_dx(0);
					pacmanclass.setReq_dy(-1);
				} else if (key == KeyEvent.VK_DOWN) {
					pacmanclass.setReq_dx(0);
					pacmanclass.setReq_dy(1);
				} else if (key == KeyEvent.VK_DOWN) {
					pacmanclass.setReq_dx(0);
					pacmanclass.setReq_dy(1);
				} else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
					inGame = false;
				}
			} else {
				if (key == KeyEvent.VK_SPACE) {
					inGame = true;
					initGame();
				}
			}
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}


	public short[] getScreenData() {
		// TODO Auto-generated method stub
		return screenData;
	}

}
