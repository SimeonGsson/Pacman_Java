
import java.awt.BasicStroke;
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
	private final int PACMAN_SPEED = 4; // Hur snabb pacman ska vara från början - ta bort final om denna ska ändras under spelets gång

	private int N_GHOSTS = 6; // Antalet spöken som finns med från början
	private int lives, score;
	private int [] dx, dy; // Behövs för positionen för spökena
	private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // Behövs också för att veta antalet och positionen av spökena 

	private Image heart, ghost; // Ikoner för objekten
	private Image up, down, left, right; // Ikoner för pacman

	private int pacman_x, pacman_y, pacman_dx, pacman_dy; // Delta grejerna är riktningen för pacman som åberopas, de andra är positionen för pacman
	private int req_dx, req_dy; // Determined in the Tadapter class, extends KeyAdapter{} och hanterar inmatningen av tangenttryck

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
		setFocusable(true);
		initGame();
	}

	
	private void loadImages() {
		down = new ImageIcon("C:\\Users\\simeo\\Downloads\\down.gif").getImage();
		up = new ImageIcon("C:\\Users\\simeo\\Downloads\\up.gif").getImage();
		right = new ImageIcon("C:\\Users\\simeo\\Downloads\\right.gif").getImage();
		left = new ImageIcon("C:\\Users\\simeo\\Downloads\\left.gif").getImage();
		ghost = new ImageIcon("C:\\Users\\simeo\\Downloads\\ghost.gif").getImage();
		heart = new ImageIcon("C:\\Users\\simeo\\Downloads\\heart.png").getImage();
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

	
	private void playGame(Graphics2D g2d) { // funktionen som håller igång spelet.
		if (dying) {
			death();
		} else {
			movePacman();
			drawPacman(g2d);
			moveGhosts(g2d);
			checkMaze();
		}
	}

	
	public void showIntroScreen(Graphics2D g2d) {
		String start = "Press SPACE to start";
		g2d.setColor(Color.orange);
		g2d.drawString(start, (SCREEN_SIZE) / 4, 150);
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
	g2d.setFont(smallFont);
	g2d.setColor(Color.red);
	String s = "GAME OVER";
	g2d.drawString(s,  (SCREEN_SIZE) / 4, 150);
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

	
	private void moveGhosts(Graphics2D g2d) {

		int pos;
		int count;

		for (int i = 0; i < N_GHOSTS; i++) {
			if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
				pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

				count = 0;

				if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
					dx[count] = -1;
					dy[count] = 0;
					count++;
				}

				if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
					dx[count] = 0;
					dy[count] = -1;
					count++;
				}

				if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
					dx[count] = 1;
					dy[count] = 0;
					count++;
				}

				if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
					dx[count] = 0;
					dy[count] = 1;
					count++;
				}

				if (count == 0) {

					if ((screenData[pos] & 15) == 15) {
						ghost_dx[i] = 0;
						ghost_dy[i] = 0;
					} else {
						ghost_dx[i] = -ghost_dx[i];
						ghost_dy[i] = -ghost_dy[i];
					}

				} else {

					count = (int) (Math.random() * count);

					if (count > 3) {
						count = 3;
					}

					ghost_dx[i] = dx[count];
					ghost_dy[i] = dy[count];
				}

			}

			ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
			ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
			drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

			if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
					&& pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
					&& inGame) {

				dying = true;
			}
		}
	}


	private void drawGhost(Graphics2D g2d, int x, int y) {
		g2d.drawImage(ghost, x, y, this);
	}
	

	public void movePacman() {
		int pos;
		short ch;

		if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
			pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
			ch = screenData[pos];

			if ((ch & 16) !=0) {
				screenData[pos] = (short) (ch & 15);
				score++;
		//		System.out.println("Eaten a food pellet at position " + pos + ". New screenData value: " + screenData[pos]);
			}

			if (req_dx != 0 || req_dy != 0) {
				if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
						|| (req_dy == 1 && req_dy == 0 && (ch & 4) != 0)
						|| (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
						|| (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
					pacman_dx = req_dx;
					pacman_dy = req_dy;
				}
			}

			// Check for standstill
			if ((pacman_dx == -1 && pacman_dy == 0 && (ch & 1) != 0)
					|| (pacman_dx == 1 && pacman_dy == 0 && (ch & 4) != 0)
					|| (pacman_dx == 0 && pacman_dy == -1 && (ch & 2) != 0)
					|| (pacman_dx == 0 && pacman_dy == 1 && (ch & 8) != 0)) {
				pacman_dx = 0;
				pacman_dy = 0;
			}
		}
		pacman_x = pacman_x + PACMAN_SPEED * pacman_dx;
		pacman_y = pacman_y + PACMAN_SPEED * pacman_dy;
	}

	
	private void drawPacman(Graphics2D g2d) {

		if (req_dx == -1) {
			g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this );
		} else if (req_dx == 1) {
			g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
		} else if (req_dy == -1) {
			g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
		} else {
			g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
		}
	}

	
	private void drawMaze(Graphics2D g2d) {

		short i = 0;
		int x, y;

		for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
			for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

				g2d.setColor(new Color(0,153,0));
				g2d.setStroke(new BasicStroke(3));

				if ((levelData[i] == 0)) { 
					g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
				}

				if ((screenData[i] & 1) != 0) { 
					g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 2) != 0) { 
					g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
				}

				if ((screenData[i] & 4) != 0) { 
					g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
							y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 8) != 0) { 
					g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
							y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 16) != 0) { 
					g2d.setColor(new Color(255,255,255));
					g2d.fillOval(x + 10, y + 10, 6, 6);
				}

				i++;
			}
		}
	}

	
	private void initGame() { // Spelet initieras - sätt ut startvärden som ska återställas vid spelstart
		lives = 3;
		score = 0;
		initLevel();
		N_GHOSTS = 3;
		currentSpeed = 2; // Pacmans hastighet initieras
	}

	
	private void initLevel() {
		int i;
		for (i = 0; i<N_BLOCKS * N_BLOCKS; i++) { // För varje ruta 15x15 = 225 st 
			screenData[i] = levelData[i]; // Kopiera det värdet på spelplan som representerar den rutan
		}
		
		continueLevel();
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

		pacman_x = 7 * BLOCK_SIZE;
		pacman_y = 11 * BLOCK_SIZE;
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
					inGame = false; // Avsluta spelet genom att klicka på Escape
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

}
