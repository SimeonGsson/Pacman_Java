import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Arrays;

import javax.swing.ImageIcon;

public class Ghost {
	private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, dx, dy, ghostSpeed;
	private int BLOCK_SIZE, N_GHOSTS, N_BLOCKS;
	private short[] screenData;
	private Image ghost;
	boolean[] ghostActive = new boolean[N_GHOSTS];  // Add this line, assume all ghosts are active initially

	public Ghost(short[] screenData, int N_BLOCKS, int N_GHOSTS, int BLOCK_SIZE, int[] ghost_x, int[] ghost_y, int[] ghost_dx, int[] ghost_dy, int[] dx, int[] dy, int[] ghostSpeed) {
		this.N_GHOSTS = N_GHOSTS;
		this.BLOCK_SIZE = BLOCK_SIZE;
		this.screenData = screenData;
		this.ghost_x = ghost_x;
		this.ghost_y = ghost_y;
		this.ghost_dx = ghost_dx;
		this.ghost_dy = ghost_dy;
		this.N_BLOCKS = N_BLOCKS;
		this.dx = dx;
		this.dy = dy;
		this.ghostSpeed = ghostSpeed;

		this.ghostActive = new boolean[N_GHOSTS];
		Arrays.fill(ghostActive, Boolean.TRUE); // Set all ghosts to be active initially

		loadImages();
	}


	private void loadImages() {
		ghost = new ImageIcon("C:\\Users\\simeo\\Downloads\\ghost.gif").getImage();
	}



	public void move() {
		int pos;
		int count;
		for (int i = 0; i < N_GHOSTS; i++) {   

			if (ghostActive[i]) {  // Only move the ghost if it's active

				if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
					pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE); // Koordinaten för positionen

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
			ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
			ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);

		}
	}

	public void updateNumGhosts() {
		this.N_GHOSTS = ghostActive.length;
	}

	public void removeGhost(int currentGhostIndex) {

		// Shift all elements down one index
		for (int i = currentGhostIndex; i < N_GHOSTS - 1; i++) {
			ghost_x[i] = ghost_x[i + 1];
			ghost_y[i] = ghost_y[i + 1];
			ghost_dx[i] = ghost_dx[i + 1];
			ghost_dy[i] = ghost_dy[i + 1];
			ghostSpeed[i] = ghostSpeed[i + 1];
			ghostActive[i] = ghostActive[i + 1];
		}
		// Decrease the number of active ghosts
		N_GHOSTS--;
		updateNumGhosts();
	}


	public void draw(Graphics2D g2d, int[] x, int[] y) {
		for (int i = 0; i < N_GHOSTS; i++) {
			if (ghostActive[i]) {
				g2d.drawImage(ghost, x[i], y[i], null);
			} else {
				System.out.println("This ghost is not active");
			}
		}
	}

	public int getNumGhosts() {
		return N_GHOSTS;
	}

}
