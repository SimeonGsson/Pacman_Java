import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class Pacman {
	private int x, y, dx, dy, req_dx, req_dy, N_GHOSTS;
	private final int BLOCK_SIZE;
	private int PACMAN_SPEED;
	private final int N_BLOCKS;
	private Model model;
	private Image up, down, left, right, fireMode;
	private short[] screenData;
	private int[] ghost_x;
	private int[] ghost_y;
	private boolean dying;
	private boolean inGame;
	private boolean pacmanEatingMode;
	private Timer pacmanEatingModeTimer;
	private Timer increaseSpeed;


	public Pacman(short[] screenData, int N_BLOCKS, Model model, int[] ghost_x, int[] ghost_y, boolean dying, int N_GHOSTS, boolean inGame) {
		this.screenData = screenData;
		this.N_BLOCKS = N_BLOCKS;
		this.BLOCK_SIZE = 24;
		this.PACMAN_SPEED = 3;
		this.model = model;
		this.ghost_x = ghost_x;
		this.ghost_y = ghost_y;
		this.dying = dying;
		this.inGame = inGame;
		this.N_GHOSTS = N_GHOSTS;
		this.x = 17 * BLOCK_SIZE; 
		this.y = 17 * BLOCK_SIZE; 
		pacmanEatingMode = false;
		loadImages();
	}

	private void loadImages() {
		down = new ImageIcon("C:\\Users\\simeo\\Downloads\\down.gif").getImage();
		up = new ImageIcon("C:\\Users\\simeo\\Downloads\\up.gif").getImage();
		right = new ImageIcon("C:\\Users\\simeo\\Downloads\\right.gif").getImage();
		left = new ImageIcon("C:\\Users\\simeo\\Downloads\\left.gif").getImage();
		fireMode = new ImageIcon("C:\\Users\\simeo\\Downloads\\y8 (1).gif\\").getImage();
	}

	
	public void move() {

		int pos;
		short ch;
		inGame = true;

		if (x % BLOCK_SIZE == 0 && y % BLOCK_SIZE == 0) {
			pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
			ch = screenData[pos];


			if ((ch & 16) !=0) {
				screenData[pos] = (short) (ch & 15);
				model.incrementScore();
				//		System.out.println("Eaten a food pellet at position " + pos + ". New screenData value: " + screenData[pos]);
			}

			// Denna metod kollar om det är en vägg eller ej
			if (req_dx != 0 || req_dy != 0) {
				if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
						|| (req_dy == 1 && req_dy == 0 && (ch & 4) != 0)
						|| (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
						|| (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
					dx = req_dx;
					dy = req_dy;
				}
			}

			if (x == model.get_randomCoordinate_x() * BLOCK_SIZE && y == model.get_randomCoordinate_y() * BLOCK_SIZE)  {
				pacmanEatingMode = true;
				model.set_randomCoordinate_x(15);
				model.set_randomCoordinate_y(20);
				System.out.println("PacmanEatingMode aktiverad");
				pacmanEatingModeTimer = new Timer(10000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						pacmanEatingMode = false;
						System.out.println("PacmanEatingMode är över");
					}
				});
				pacmanEatingModeTimer.setRepeats(false);
				pacmanEatingModeTimer.start();

				// Gör så detta endast gäller i 30 sekunder
			} else if (x == model.get_randomCoordinateTwo_x() * BLOCK_SIZE && y == model.get_randomCoordinateTwo_y() * BLOCK_SIZE) {
				model.addLife();
				model.set_randomCoordinateTwo_x(30);
				System.out.println("AddLife");
			} else if (x == model.get_randomCoordinateThree_x() * BLOCK_SIZE && y == model.get_randomCoordinateThree_y() * BLOCK_SIZE) {
				if (PACMAN_SPEED <= 3) {
					increaseSpeed();
					model.set_randomCoordinateThree_x(14);
					model.set_randomCoordinateThree_y(20);
					System.out.println("IncreaseSpeed");
				}
			}

			// Check for standstill
			if ((dx == -1 && dy == 0 && (ch & 1) != 0)
					|| (dx == 1 && dy == 0 && (ch & 4) != 0)
					|| (dx == 0 && dy == -1 && (ch & 2) != 0)
					|| (dx == 0 && dy == 1 && (ch & 8) != 0)) {
				dx = 0;
				dy = 0;
			}
		}
		// This shiet works now - Kollar efter en kollision mellan spöke och mr pac
		for (int i = 0; i < N_GHOSTS; i++) {
			if (x > (ghost_x[i] - 12) && x < (ghost_x[i] + 12)
					&& y > (ghost_y[i] - 12) && y < (ghost_y[i] + 12)
					&& inGame) {
				//System.out.println("Collaps between pacman and ghost.");
				// If Pacman is in eating mode, eat the ghost instead of dying
				if (pacmanEatingMode()) {
					eatGhost(i); // This is a new method you will need to implement
				} else {
					//System.out.println("Collaps between pacman and ghost.");
					dying = true;
					setDeath(dying);
				}
			}
		}
		x = x + PACMAN_SPEED * dx;
		y = y + PACMAN_SPEED * dy;
	}

	
	public void drawPac(Graphics2D g2d) {
		if (pacmanEatingMode == false) {
			if (req_dx == -1) {
				g2d.drawImage(left, x + 1, y + 1, model );
			} else if (req_dx == 1) {
				g2d.drawImage(right, x + 1, y + 1, model);
			} else if (req_dy == -1) {
				g2d.drawImage(up, x + 1, y + 1, model);
			} else {
				g2d.drawImage(down, x + 1, y + 1, model);
			}       
		} else {
			if (req_dx == -1) {
				g2d.drawImage(fireMode, x - 12, y - 15, model );
				g2d.drawImage(left, x + 1, y + 1, model );
			} else if (req_dx == 1) {
				g2d.drawImage(fireMode, x - 12, y - 15, model );
				g2d.drawImage(right, x + 1, y + 1, model);
			} else if (req_dy == -1) {
				g2d.drawImage(fireMode, x - 12, y - 15, model );
				g2d.drawImage(up, x + 1, y + 1, model);
			} else {
				g2d.drawImage(fireMode, x - 12, y - 15, model );
				g2d.drawImage(down, x + 1, y + 1, model);
			}   		
		}
	}

	
	public void updateNumGhosts(int numGhosts) {
		this.N_GHOSTS = numGhosts;
	}

	public void eatGhost(int ghostIndex) {
		// Ät spöket
		model.getGhostClass().removeGhost(ghostIndex);

		// Uppdatera antalet spöken
		updateNumGhosts(model.getGhostClass().getNumGhosts());

		// Öka poäng med 5
		model.incrementScoreByFive();
	}


	public void increaseSpeed() {
		// Denna gäller endast 10 sekunder. Det är det vi satt timnen till.
		PACMAN_SPEED += 1; //
		System.out.println("SpeedMode aktiverad");
		increaseSpeed = new Timer(10000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((x % BLOCK_SIZE == 0) && (y % BLOCK_SIZE == 0)) {
					PACMAN_SPEED -= 1;
					dx = 0;
					dy = 0;
					req_dx = 0;
					req_dy = 0;
					move();
					System.out.println("SpeedMode är över. Pacman stannar upp tills du börjar röra på karaktären igen.");
				} else {
					increaseSpeed.setInitialDelay(100); // Denna checker finns med eftersom att det blir strul om pacman står mellan två koordinater
					increaseSpeed.setRepeats(false); // Checker för att göra så den inte uppdaterar oändligt med gånger
					increaseSpeed.restart(); // Vi kör om timern för att kika om pacman nu står rätt
				}
			}
		});
		increaseSpeed.setRepeats(false);
		increaseSpeed.start();
	}


	public boolean pacmanEatingMode() {
		return pacmanEatingMode;
	}	

	public Point getPacManLocation() {
	    return new Point(x, y); 
	}

	public void setReq_dx(int req_dx) {
		this.req_dx = req_dx;
	}

	public void setReq_dy(int req_dy) {
		this.req_dy = req_dy;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setDX(int dx) {
		this.dx = dx;
	}

	public void setDY(int dy) {
		this.dy = dy;
	}

	public void setReqDY(int reqDY) {
		this.req_dy = reqDY;
	}

	public void setReqDX(int reqDX) {
		this.req_dx = reqDX;
	}

	public void resetDeath() {
		dying = false;
	}

	public void setDeath(boolean dying) {
		this.dying = dying;
	}

	public boolean getDeath() {
		return dying;
	}

	public boolean getPacmanEatingMode() {
		return pacmanEatingMode;
	}
}
