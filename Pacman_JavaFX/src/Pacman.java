import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Pacman {
	private int x, y, dx, dy;
	private int req_dx, req_dy;
	private final int BLOCK_SIZE;
	private final int PACMAN_SPEED;
	private short[] screenData;
	private final int N_BLOCKS;
	private Model model;
	private Image up, down, left, right;
	private int[] ghost_x;
	private int[] ghost_y;
	private boolean dying;
	private boolean inGame;
	private int N_GHOSTS;

	public Pacman(short[] screenData, int N_BLOCKS, Model model, int[] ghost_x, int[] ghost_y, boolean dying, int N_GHOSTS, boolean inGame) {
		this.screenData = screenData;
		this.N_BLOCKS = N_BLOCKS;
		this.BLOCK_SIZE = 24;
		this.PACMAN_SPEED = 4;
		this.model = model;
		this.ghost_x = ghost_x;
		this.ghost_y = ghost_y;
		this.dying = dying;
		this.inGame = inGame;
		this.N_GHOSTS = N_GHOSTS;
		this.x = 17 * BLOCK_SIZE; 
		this.y = 17 * BLOCK_SIZE; 
		loadImages();
	}

	private void loadImages() {
		down = new ImageIcon("C:\\Users\\simeo\\Downloads\\down.gif").getImage();
		up = new ImageIcon("C:\\Users\\simeo\\Downloads\\up.gif").getImage();
		right = new ImageIcon("C:\\Users\\simeo\\Downloads\\right.gif").getImage();
		left = new ImageIcon("C:\\Users\\simeo\\Downloads\\left.gif").getImage();
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
				dying = true;
				setDeath(dying);
			}
		}

		x = x + PACMAN_SPEED * dx;
		y = y + PACMAN_SPEED * dy;

	}


	public void drawPac(Graphics2D g2d) {
		if (req_dx == -1) {
			g2d.drawImage(left, x + 1, y + 1, model );
		} else if (req_dx == 1) {
			g2d.drawImage(right, x + 1, y + 1, model);
		} else if (req_dy == -1) {
			g2d.drawImage(up, x + 1, y + 1, model);
		} else {
			g2d.drawImage(down, x + 1, y + 1, model);
		}       
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
}
