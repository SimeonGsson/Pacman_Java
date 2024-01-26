import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * Pacman-klassen representerar huvudfiguren i Pacman-spelet.
 * Den hanterar rörelse, kollisioner, power-ups och andra spelrelaterade funktioner.
 */
public class Pacman {
	private int x, y, dx, dy, req_dx, req_dy, N_SHREKS;
	private final int BLOCK_SIZE;
	private int PACMAN_SPEED;
	private final int N_BLOCKS;
	private Model model;
	private Image up, down, left, right, fireMode;
	private short[] screenData;
	private int[] shrek_x;
	private int[] shrek_y;
	private boolean dying;
	private boolean inGame;
	private boolean pacmanEatingMode;
	private Timer pacmanEatingModeTimer;
	private Timer increaseSpeed;

	 /**
     * Konstruktorn för Pacman-klassen.
     * @param screenData En array som representerar spelbanans data.
     * @param N_BLOCKS Antal block i spelbanan.
     * @param model En instans av Model-klassen som hanterar spelets övergripande logik.
     * @param shrek_x En array som innehåller x-koordinaterna för spökena.
     * @param shrek_y En array som innehåller y-koordinaterna för spökena.
     * @param dying En flagga som indikerar om Pacman håller på att dö.
     * @param N_SHREKS Antal spöken i spelet.
     * @param inGame En flagga som indikerar om spelet är igång.
     */
	public Pacman(short[] screenData, int N_BLOCKS, Model model, int[] shrek_x, int[] shrek_y, boolean dying, int N_SHREKS, boolean inGame) {
		this.screenData = screenData;
		this.N_BLOCKS = N_BLOCKS;
		this.BLOCK_SIZE = 24;
		this.PACMAN_SPEED = 3;
		this.model = model;
		this.shrek_x = shrek_x;
		this.shrek_y = shrek_y;
		this.dying = dying;
		this.inGame = inGame;
		this.N_SHREKS = N_SHREKS;
		this.x = 17 * BLOCK_SIZE; 
		this.y = 17 * BLOCK_SIZE; 
		pacmanEatingMode = false;
		loadImages();
	}

	/**
     * Laddar bilderna för Pacman och power-ups.
     */
	private void loadImages() {
		down = new ImageIcon("C:\\Users\\simeo\\Downloads\\down.gif").getImage();
		up = new ImageIcon("C:\\Users\\simeo\\Downloads\\up.gif").getImage();
		right = new ImageIcon("C:\\Users\\simeo\\Downloads\\right.gif").getImage();
		left = new ImageIcon("C:\\Users\\simeo\\Downloads\\left.gif").getImage();
		fireMode = new ImageIcon("C:\\Users\\simeo\\Downloads\\y8 (1).gif\\").getImage();
	}

	 /**
     * Hanterar Pacmans rörelse och kollisioner.
     */
	public void move() { // Denna funktion hanterar pacmans rörelselogik. Vilket håll går pacman? Finns det en vägg? Käkar pacman en prick? Ska någon powerup aktiveras?

		int pos;
		short cellState; // denna variabel representerar nuvarande tillståndet av en cell från banan, dvs vad en specifik koordinat innehåller.
		inGame = true;

		if (x % BLOCK_SIZE == 0 && y % BLOCK_SIZE == 0) { // Om pacman står på en koordinat och inte mellan två stycken.
			pos = x / BLOCK_SIZE + N_BLOCKS * (int) (y / BLOCK_SIZE);
			cellState = screenData[pos];


			if ((cellState & 16) !=0) { // Går du på en koordinat med en prick på? Isåfall tar vi bort den pricken och ökar poängen.
				screenData[pos] = (short) (cellState & 15);
				model.incrementScore();
			}

			// Denna metod kollar om det är en vägg eller ej
			if (req_dx != 0 || req_dy != 0) { // Om pacman inte står still
				if (!((req_dx == -1 && req_dy == 0 && (cellState & 1) != 0) // 1 för vägg till vänster
						|| (req_dy == 1 && req_dy == 0 && (cellState & 4) != 0) // fyra för vägg till höger
						|| (req_dx == 0 && req_dy == -1 && (cellState & 2) != 0) // två för tak
						|| (req_dx == 0 && req_dy == 1 && (cellState & 8) != 0))) { // åtta för golv
					dx = req_dx; // Om det inte är en vägg så godkänns den riktning pacman vill åt och direktion koordinater tilldelas
					dy = req_dy;
				}
			}
			// Om pacman går på en eatingMode powerup
			if (x == model.get_randomCoordinate_x() * BLOCK_SIZE && y == model.get_randomCoordinate_y() * BLOCK_SIZE)  {
				pacmanEatingMode = true;
				model.set_randomCoordinate_x(15);
				model.set_randomCoordinate_y(20);
				System.out.println("PacmanEatingMode aktiverad för 10 sekunder");
				pacmanEatingModeTimer = new Timer(10000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						pacmanEatingMode = false;
						System.out.println("PacmanEatingMode är över");
						model.set_randomCoordinate_x(40);
					}
				});
				pacmanEatingModeTimer.setRepeats(false);
				pacmanEatingModeTimer.start();

				// Om pacman går på en extraliv-powerup
			} else if (x == model.get_randomCoordinateTwo_x() * BLOCK_SIZE && y == model.get_randomCoordinateTwo_y() * BLOCK_SIZE) {
				model.addLife();
				model.set_randomCoordinateTwo_x(30);
				System.out.println("AddLife");

				// Om pacman går på en speed-powerup
			} else if (x == model.get_randomCoordinateThree_x() * BLOCK_SIZE && y == model.get_randomCoordinateThree_y() * BLOCK_SIZE) {
				if (PACMAN_SPEED <= 3) { // Om pacmans hastighet är mindre än 3 så går den att plocka, annars ligger den kvar. 
					increaseSpeed();
					model.set_randomCoordinateThree_x(14);
					model.set_randomCoordinateThree_y(20);
					System.out.println("IncreaseSpeed");
				}
			}

			// Checker för att se till att Pacman inte kan gå in i en vägg efter att pacman stått stilla.
			if ((dx == -1 && dy == 0 && (cellState & 1) != 0)
					|| (dx == 1 && dy == 0 && (cellState & 4) != 0)
					|| (dx == 0 && dy == -1 && (cellState & 2) != 0)
					|| (dx == 0 && dy == 1 && (cellState & 8) != 0)) {
				dx = 0;
				dy = 0;
			}
		}
		
		// This shiet works now - Kollar efter en kollision mellan spöke och mr pac 
		// Det gick inte från början eftersom att vi inte tänkte på att ett block är 24 storlek därmed måste man kolla tolv pixlar upp, ner, höger och vänster efter en kollektion. 
		// En kvadratmeter kan man tänka istället för endast mitten av kvadratmetern så är kollisionen med en hel låda
		for (int i = 0; i < N_SHREKS; i++) {
			if (x > (shrek_x[i] - 12) && x < (shrek_x[i] + 12)
					&& y > (shrek_y[i] - 12) && y < (shrek_y[i] + 12)
					&& inGame) {
				if (pacmanEatingMode()) { // Sålänge pacman är i eatingMode så går det att käka spöken annars dör man vid en kollaps.
					eatShrek(i);
				} else {
					dying = true;
					setDeath(dying);
				}
			}
		}
		x = x + PACMAN_SPEED * dx; // Här tar vi nuvarande koordinat plus pacmans hastsighet multiplicerat med den riktning som man vill gå mot.
		y = y + PACMAN_SPEED * dy;
	}


	/**
     * Ritar Pacman på spelplanen med rätt bild beroende på rörelseriktning och power-up-läge.
     * @param g2d Graphics2D-objekt för att rita Pacman.
     */
	public void drawPac(Graphics2D g2d) { // Ritar ut rätt pacmanbild olika beroende på vilket håll pacman e påväg mot
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
		} else { // Om pacman är i eatingmode så ritas detta ut
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

	/**
     * Uppdaterar antalet spöken i spelet.
     * @param numShreks Antal spöken.
     */
	public void updateNumShreks(int numShreks) {
		this.N_SHREKS = numShreks;
	}

	  /**
     * Hanterar när Pacman äter ett spöke.
     * @param shrekIndex Index för det spöke som Pacman äter.
     */
	public void eatShrek(int shrekIndex) {
		// Ät spöket
		model.getShrekClass().removeShrek(shrekIndex);

		// Uppdatera antalet spöken för stunden
		updateNumShreks(model.getShrekClass().getNumShreks());

		// Öka poäng med 5
		model.incrementScoreByFive();
	}

	 /**
     * Ökar Pacmans hastighet under en begränsad tid.
     */
	public void increaseSpeed() {
		PACMAN_SPEED += 1; //
		System.out.println("SpeedMode aktiverad för 10 sekunder");
		increaseSpeed = new Timer(10000, new ActionListener() { // Denna gäller endast 10 sekunder
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((x % BLOCK_SIZE == 0) && (y % BLOCK_SIZE == 0)) { // Om pacman inte står mellan två koordinater
					PACMAN_SPEED -= 1;
					dx = 0;
					dy = 0;
					req_dx = 0;
					req_dy = 0;
					move();
					System.out.println("SpeedMode är över. Pacman stannar upp tills du börjar röra på karaktären igen.");
					model.set_randomCoordinateThree_x(40);
				} else {
					increaseSpeed.setInitialDelay(100); // Denna checker finns med eftersom att det blir strul om pacman står mellan två koordinater
					increaseSpeed.setRepeats(false); // Checker för att göra så den inte uppdaterar oändligt med gånger
					increaseSpeed.restart(); // Vi kör om timern för att kika om pacman nu står rätt
				}
			}
		});
		increaseSpeed.setRepeats(false); // Om dessa setrepeats false inte fanns så skulle timern fortsätta att köras
		increaseSpeed.start(); 
	}
	
	// Relevanta getters och setters som behövs
	
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
	
	public int getX() {
		return x;
	}
	
	public int gety() {
		return y;
	}

	public boolean getDeath() {
		return dying;
	}

	public boolean getPacmanEatingMode() {
		return pacmanEatingMode;
	}
}
