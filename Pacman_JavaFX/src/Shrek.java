import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Arrays;

import javax.swing.ImageIcon;

/**
 * Klassen Shrek representerar Shrek-objekt med rörelse och ritningsfunktioner.
 *
 * @param shrek_x, shrek_y       Arrays med x- och y-koordinater för Shreks.
 * @param shrek_dx, shrek_dy     Arrays med x- och y-riktningar för Shreks.
 * @param dx, dy                 Arrays med möjliga riktningar för Shreks rörelse.
 * @param shrekSpeed             Array med hastigheter för Shreks.
 * @param BLOCK_SIZE             Storleken på varje block.
 * @param N_SHREKS               Antalet Shreks.
 * @param N_BLOCKS               Antalet block på spelplanen.
 * @param screenData             Array som representerar spelplanens data.
 * @param shrek                  Bilden som representerar Shrek.
 * @param shrekActive            Array som håller koll på om varje Shrek är aktivt eller inte.
 */
public class Shrek {
	private int[] shrek_x, shrek_y, shrek_dx, shrek_dy, dx, dy, shrekSpeed;
	protected int BLOCK_SIZE;
	private int N_SHREKS;
	protected int N_BLOCKS;
	private short[] screenData;
	private Image shrek;
	boolean[] shrekActive = new boolean[getN_SHREKS()]; 

	/**
	 * Konstruerar ett Shrek-objekt med givna parametrar.
	 *
	 * @param barrel     Ett Barrel-objekt för att hantera explosioneffekter.
	 * @param screenData Array som representerar spelplanens data.
	 * @param N_BLOCKS   Antalet block på spelplanen.
	 * @param N_SHREKS   Antalet Shreks.
	 * @param BLOCK_SIZE Storleken på varje block.
	 * @param shrek_x    Array med x-koordinater för Shreks.
	 * @param shrek_y    Array med y-koordinater för Shreks.
	 * @param shrek_dx   Array med x-riktningar för Shreks.
	 * @param shrek_dy   Array med y-riktningar för Shreks.
	 * @param dx         Array med x-riktningar för rörliga element.
	 * @param dy         Array med y-riktningar för rörliga element.
	 * @param shrekSpeed Array med hastigheter för Shreks.
	 */	
	public Shrek(Barrel barrel, short[] screenData, int N_BLOCKS, int N_SHREKS, int BLOCK_SIZE, int[] shrek_x, int[] shrek_y, int[] shrek_dx, int[] shrek_dy, int[] dx, int[] dy, int[] shrekSpeed) {
		this.setN_SHREKS(N_SHREKS);
		this.BLOCK_SIZE = BLOCK_SIZE;
		this.setScreenData(screenData);
		this.setShrek_x(shrek_x);
		this.setShrek_y(shrek_y);
		this.shrek_dx = shrek_dx;
		this.shrek_dy = shrek_dy;
		this.N_BLOCKS = N_BLOCKS;
		this.dx = dx;
		this.dy = dy;
		this.shrekSpeed = shrekSpeed;

		this.shrekActive = new boolean[N_SHREKS];
		Arrays.fill(shrekActive, Boolean.TRUE); // Gör alla spöken aktiva direkt - när ett spöke äts upp så tar man bort det spökets aktiva status

		loadImages();
	}

	/**
	 * Laddar bilder för Shrek-objektet.
	 */
	private void loadImages() {
		shrek = new ImageIcon("C:\\Users\\simeo\\Downloads\\Shrek-ezgif.com-resize (1).gif").getImage();
	}

	/**
	 * Rör på Shreks och hanterar deras kollision med väggar på spelplanen.
	 */
	public void move() {
		int pos;
		int count;
		for (int i = 0; i < getN_SHREKS(); i++) {   

			if (shrekActive[i]) {  // Om spöket är aktivt så ska det röra sig

				if (getShrek_x()[i] % BLOCK_SIZE == 0 && getShrek_y()[i] % BLOCK_SIZE == 0) {
					pos = getShrek_x()[i] / BLOCK_SIZE + N_BLOCKS * (int) (getShrek_y()[i] / BLOCK_SIZE); // Koordinaten för positionen

					count = 0;

					if ((getScreenData()[pos] & 1) == 0 && shrek_dx[i] != 1) {
						dx[count] = -1;
						dy[count] = 0;
						count++;
					}

					if ((getScreenData()[pos] & 2) == 0 && shrek_dy[i] != 1) {
						dx[count] = 0;
						dy[count] = -1;
						count++;
					}

					if ((getScreenData()[pos] & 4) == 0 && shrek_dx[i] != -1) {
						dx[count] = 1;
						dy[count] = 0;
						count++;
					}

					if ((getScreenData()[pos] & 8) == 0 && shrek_dy[i] != -1) {
						dx[count] = 0;
						dy[count] = 1;
						count++;
					}

					if (count == 0 || (getScreenData()[pos] & 15) == 15) {
						if ((getScreenData()[pos] & 15) == 15) {
							shrek_dx[i] = 0;
							shrek_dy[i] = 0;
						} else {
							shrek_dx[i] = -shrek_dx[i];
							shrek_dy[i] = -shrek_dy[i];
						}
					} else {
						count = (int) (Math.random() * count);
						if (count > 3) {
							count = 3;
						}
						shrek_dx[i] = dx[count];
						shrek_dy[i] = dy[count];
					}
				}
			}
			getShrek_x()[i] = getShrek_x()[i] + (shrek_dx[i] * shrekSpeed[i]);
			getShrek_y()[i] = getShrek_y()[i] + (shrek_dy[i] * shrekSpeed[i]);
		}
	}

	/**
	 * Uppdaterar antalet Shreks baserat på deras aktivitetsstatus.
	 */
	public void updateNumShreks() {
		this.setN_SHREKS(shrekActive.length);
	}

	/**
	 * Tar bort ett Shrek baserat på dess index i arrayen.
	 *
	 * @param currentShrekIndex Index för det Shrek som ska tas bort.
	 */
	public void removeShrek(int currentShrekIndex) {

		// Här så tar vi bort ett spöke temporärt genom att skifta ner alla element med ett index
		for (int i = currentShrekIndex; i < getN_SHREKS() - 1; i++) {
			getShrek_x()[i] = getShrek_x()[i + 1];
			getShrek_y()[i] = getShrek_y()[i + 1];
			shrek_dx[i] = shrek_dx[i + 1];
			shrek_dy[i] = shrek_dy[i + 1];
			shrekSpeed[i] = shrekSpeed[i + 1];
			shrekActive[i] = shrekActive[i + 1];
		}

		setN_SHREKS(getN_SHREKS() - 1); // Minska antalet aktiva spöken
		updateNumShreks(); // Denna funktion kallar vi på så att spöken inte försvinner helt. De spawnar i ett annat spöke istället.
	}

	/**
	 * Ritar Shreks på grafikkontexten g2d.
	 *
	 * @param g2d Grafikkontexten.
	 * @param x   Array med x-koordinater för Shreks.
	 * @param y   Array med y-koordinater för Shreks.
	 */
	public void draw(Graphics2D g2d, int[] x, int[] y) {
		for (int i = 0; i < getN_SHREKS(); i++) {
			if (shrekActive[i]) {
				g2d.drawImage(shrek, x[i], y[i], null);
			}
		}
	}


	// Alla getters och setters som är nödvändiga: 
	
	public int getNumShreks() {
		return getN_SHREKS();
	}

	public int[] getShrek_x() {
		return shrek_x;
	}

	public void setShrek_x(int[] shrek_x) {
		this.shrek_x = shrek_x;
	}

	public int[] getShrek_y() {
		return shrek_y;
	}

	public void setShrek_y(int[] shrek_y) {
		this.shrek_y = shrek_y;
	}

	public void setShrek_dy(int[] shrek_y) {
		this.shrek_y = shrek_y;
	}

	public int[] getShrek_dy() {
		return shrek_dy;
	}

	public void setShrek_dx(int[] shrek_x) {
		this.shrek_x = shrek_x;
	}

	public int[] getShrek_dx() {
		return shrek_dx;
	}

	public int getN_SHREKS() {
		return N_SHREKS;
	}

	public void setN_SHREKS(int n_SHREKS) {
		N_SHREKS = n_SHREKS;
	}

	public void setShrekActive() {
		shrekActive = Arrays.copyOf(shrekActive, N_SHREKS);
		Arrays.fill(shrekActive, Boolean.TRUE);
	}


	public short[] getScreenData() {
		return screenData;
	}


	public void setScreenData(short[] screenData) {
		this.screenData = screenData;
	}


}
