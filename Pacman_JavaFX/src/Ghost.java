import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Arrays;

import javax.swing.ImageIcon;

public class Ghost {
	private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, dx, dy, ghostSpeed;
	private int BLOCK_SIZE, N_GHOSTS, N_BLOCKS;
	private short[] screenData;
	private Image ghost;
	boolean[] ghostActive = new boolean[getN_GHOSTS()]; 

	public Ghost(short[] screenData, int N_BLOCKS, int N_GHOSTS, int BLOCK_SIZE, int[] ghost_x, int[] ghost_y, int[] ghost_dx, int[] ghost_dy, int[] dx, int[] dy, int[] ghostSpeed) {
		this.setN_GHOSTS(N_GHOSTS);
		this.BLOCK_SIZE = BLOCK_SIZE;
		this.screenData = screenData;
		this.setGhost_x(ghost_x);
		this.setGhost_y(ghost_y);
		this.ghost_dx = ghost_dx;
		this.ghost_dy = ghost_dy;
		this.N_BLOCKS = N_BLOCKS;
		this.dx = dx;
		this.dy = dy;
		this.ghostSpeed = ghostSpeed;

		this.ghostActive = new boolean[N_GHOSTS];
		Arrays.fill(ghostActive, Boolean.TRUE); // Gör alla spöken aktiva direkt - när ett spöke äts upp så tar man bort det spökets aktiva status

		loadImages();
	}


	private void loadImages() {
		ghost = new ImageIcon("C:\\Users\\simeo\\Downloads\\Shrek-ezgif.com-resize (1).gif").getImage();
	}

	public void move() {
		int pos;
		int count;
		for (int i = 0; i < getN_GHOSTS(); i++) {   

			if (ghostActive[i]) {  // Om spöket är aktivt så ska det röra sig

				if (getGhost_x()[i] % BLOCK_SIZE == 0 && getGhost_y()[i] % BLOCK_SIZE == 0) {
					pos = getGhost_x()[i] / BLOCK_SIZE + N_BLOCKS * (int) (getGhost_y()[i] / BLOCK_SIZE); // Koordinaten för positionen

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

					if (count == 0 || (screenData[pos] & 15) == 15) {
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
			}
			getGhost_x()[i] = getGhost_x()[i] + (ghost_dx[i] * ghostSpeed[i]);
			getGhost_y()[i] = getGhost_y()[i] + (ghost_dy[i] * ghostSpeed[i]);

		}
	}

	public void updateNumGhosts() {
		this.setN_GHOSTS(ghostActive.length);
	}

	public void removeGhost(int currentGhostIndex) {

		// Här så tar vi bort ett spöke temporärt genom att skifta ner alla element med ett index
		for (int i = currentGhostIndex; i < getN_GHOSTS() - 1; i++) {
			getGhost_x()[i] = getGhost_x()[i + 1];
			getGhost_y()[i] = getGhost_y()[i + 1];
			ghost_dx[i] = ghost_dx[i + 1];
			ghost_dy[i] = ghost_dy[i + 1];
			ghostSpeed[i] = ghostSpeed[i + 1];
			ghostActive[i] = ghostActive[i + 1];
		}
		
		setN_GHOSTS(getN_GHOSTS() - 1); // Minska antalet aktiva spöken
		updateNumGhosts(); // Denna funktion kallar vi på så att spöken inte försvinner helt. De spawnar i ett annat spöke istället.
	}

	public void draw(Graphics2D g2d, int[] x, int[] y) {
		for (int i = 0; i < getN_GHOSTS(); i++) {
			if (ghostActive[i]) {
				g2d.drawImage(ghost, x[i], y[i], null);
			}
		}
	}

	
	public int getNumGhosts() {
		return getN_GHOSTS();
	}

	public int[] getGhost_x() {
		return ghost_x;
	}

	public void setGhost_x(int[] ghost_x) {
		this.ghost_x = ghost_x;
	}

	public int[] getGhost_y() {
		return ghost_y;
	}

	public void setGhost_y(int[] ghost_y) {
		this.ghost_y = ghost_y;
	}

	public int getN_GHOSTS() {
		return N_GHOSTS;
	}

	public void setN_GHOSTS(int n_GHOSTS) {
		N_GHOSTS = n_GHOSTS;
	}
	
	public void setGhostActive() {
	    ghostActive = Arrays.copyOf(ghostActive, N_GHOSTS);
	    Arrays.fill(ghostActive, Boolean.TRUE);
	}


}
