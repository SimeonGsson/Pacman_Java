
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener{

	private Dimension d; // Spelplanens storlek
	private final Font smallFont = new Font("Arial", Font.BOLD, 17); // Font för texten
	private final Font bigFont = new Font("Arial", Font.BOLD, 25); // Font för GameOver
	private boolean inGame = false; // inGame används för att avgöra om GameOverskärmen eller Introskärmen ska visas. När den är true så betyder det att spelet är igång
	private boolean dying = false; // dying används i death funktionen som hanterar vad som händer när pacman blir käkad av spöken
	private boolean gameOver = false; // Avgör om gameOverskrämen ska visas
	private boolean gameStarted = false; // Avgör om Introskärmen ska visas
	
	// Går ej att modifiera
	private final int BLOCK_SIZE = 24; // Storleken på rutorna
	private final int N_BLOCKS = 20; // Antalet rutor per rad eller kolumn. Spelplanen kommer att vara 15x15
	private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // rutornas totala yta. Hur stor yta kommer dessa rutor ta upp? Spelplanen kommer dock vara 15x15
	private final int MAX_GHOSTS = 20; // Totala antalet spöken som kan finns med
	private final int validSpeeds[] = {1,2,3,4,6,8}; // De olika hastigheterna som pacman och karaktärerna kan ha
	private final int maxSpeed = 8; // // maxHastighet
	
	private int currentSpeed = 2; // Pacmands hastighet från början
	private int N_GHOSTS = 2; // Antalet spöken som finns med från början
	private int lives, score;
	private int [] dx, dy; // Behövs för positionen för spökena
	private int [] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // Behövs också för att veta antalet och positionen av spökena 
	private int randomCoordinate_x, randomCoordinate_y, randomCoordinateTwo_x, randomCoordinateTwo_y, randomCoordinateThree_x, randomCoordinateThree_y;
	
	private Image heart, elixir, speed; // Ikoner för objekten
	private GameMap gameMap; // Vi skapar en instans av klassen med banorna. Sedan kommer vi använda selectedMap för att kopiera den bana som ska användas. Vi börjar sätta startvärdet till bana ett men sedan går det att välja mellan ett och två
	private Ghost ghostclass; // Vi skapar en instans av spökklassen
	private Pacman pacmanclass; // VI skapar en instans av pacmanklassen
	private Timer timer;
	
	private short [] selectedMap; // Den valda banan
	private short [] screenData; // Denna kommer ta in datan för hur banan är utformad och kommer användas i en funktion för rita om spelet när något händer
	
	private static final int MAX_HIGH_SCORES = 5; // Antalet resultat som kan visas samtidigt
    private List<Integer> highScores = new ArrayList<>(); // Listan för att lagra bästa resultat


	public Model() {
		loadImages(); // Laddar upp bilderna till bildvariablerna ghost, heart, elixir och speed
		initVariables(); // Initierar variablerna och sätter startvärden
		addKeyListener(new TAdapter()); // Skapar en ny klass för tangentbordslogik
		gameMap = new GameMap(N_BLOCKS, screenData); //Skapar en ny bana genom att kopiera en av banorna från GameMap
		selectedMap = gameMap.getMapOne(); // Dehär blir default map så att det funkar att välja mellan två olika
		pacmanclass = new Pacman(screenData, N_BLOCKS, this, ghost_x, ghost_y, dying, N_GHOSTS, inGame); // Skapar en pacman klass och skickar in startvärden
		ghostclass = new Ghost(screenData, N_BLOCKS, N_GHOSTS, BLOCK_SIZE, ghost_x, ghost_y, ghost_dx, ghost_dy, dx, dy, ghostSpeed); // Skapar en spök klass och skickar in startvärden
		setFocusable(true); // 
		initGame(); // Starta spelet - Startar gameloopen som i sin tur kommer kalla på alla andra viktiga funktioner vardera 40:e millisekund.
	}


	private void loadImages() {
		heart = new ImageIcon("C:\\Users\\simeo\\Downloads\\blueHeart-ezgif.com-resize.gif").getImage();
		elixir = new ImageIcon("C:\\Users\\simeo\\Downloads\\elixir (1).png").getImage();
		speed = new ImageIcon("C:\\Users\\simeo\\Downloads\\sprint_1483660 (1).png").getImage();
	}

	
	private void initVariables() { // Här initierar vi alla variabler som inte är initierade ännu och ger dem ett startvärde
		screenData = new short[N_BLOCKS * N_BLOCKS];
		d = new Dimension(590, 540);
		ghost_x = new int [MAX_GHOSTS];
		ghost_dx = new int [MAX_GHOSTS];
		ghost_y = new int [MAX_GHOSTS];
		ghost_dy = new int [MAX_GHOSTS];
		ghostSpeed = new int [MAX_GHOSTS];
		dx = new int[4];
		dy = new int [4];
		timer = new Timer(40, this); // bestämmer hur ofta allt ritas om
		timer.start();
		coordinateTimer.start();
		randomCoordinate_x = 26;
		randomCoordinate_y = 26;
		randomCoordinateTwo_x = 26;
		randomCoordinateTwo_y = 26;
		randomCoordinateThree_x = 26;
		randomCoordinateThree_y = 26;
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

	
	class TAdapter extends KeyAdapter { // Här styrs tangentbordstrycken
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			char keyChar = e.getKeyChar();

			if (Character.isDigit(keyChar) && !inGame) {
				int mapNumber = Character.getNumericValue(keyChar);
				switch (mapNumber) {
				case 1:
					System.out.println("Bana 1 vald");
					getCurrentMap(gameMap.getMapOne());
					initLevel();
					break;
				case 2:
					System.out.println("Bana 2 vald");
					getCurrentMap(gameMap.getMapTwo());
					initLevel();
					break;
				case 3:
					System.out.println("Testbana vald");
					getCurrentMap(gameMap.getTestMap());
					initLevel();
					break;
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
	
	// Lite draw funktioner:
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

	public void drawScoreAndLives(Graphics2D g) {
		g.setFont(smallFont);
		g.setColor(Color.orange);
		String s = "Poäng: " + score;
		g.drawString(s,  SCREEN_SIZE / 2 + 165, SCREEN_SIZE + 24);

		for (int i = 0; i < lives; i++) {
			g.drawImage(heart,  i * 28 + 8, SCREEN_SIZE + 7 , this);
		}
	}
	
    public void drawHighScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(Color.orange);
        String s = "Resultat :";
        g.drawString(s,  SCREEN_SIZE / 2 + 250, SCREEN_SIZE - 450);

        int yPos = SCREEN_SIZE - 430;
        for (int i = 0; i < Math.min(highScores.size(), 5); i++) {
            String scoreStr = (i + 1) + ". " + highScores.get(i);
            g.drawString(scoreStr,  SCREEN_SIZE / 2 + 250, yPos);
            yPos += 20;
        }
    }
    
    public void drawPowerUpText(Graphics2D g) {
    	g.setFont(smallFont);
        g.setColor(Color.orange);
        String s = "Aktiverade Powerups :";
        g.drawString(s,  SCREEN_SIZE / 2 - 100, SCREEN_SIZE + 23);
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
	
	private void drawEatPowerup(Graphics2D g2d) {
	    g2d.drawImage(elixir, randomCoordinate_x * BLOCK_SIZE, randomCoordinate_y * BLOCK_SIZE, this);
	//    System.out.println("Eatpowerup coordinate x: " + randomCoordinate_x + " + y: " + randomCoordinate_y);
	}
	
	private void drawHealthPowerup(Graphics2D g2d) {
		g2d.drawImage(heart, randomCoordinateTwo_x * BLOCK_SIZE, randomCoordinateTwo_y * BLOCK_SIZE, this);
	//	System.out.println("Health coordinate x: " + randomCoordinate_x + " + y: " + randomCoordinate_y);
	}
	
	private void drawSpeedPowerup(Graphics2D g2d) {
		g2d.drawImage(speed, randomCoordinateThree_x * BLOCK_SIZE, randomCoordinateThree_y * BLOCK_SIZE, this);
	//	System.out.println("Speed coordinate x: " + randomCoordinate_x + " + y: " + randomCoordinate_y);
	}

	
	public void addScore(int newScore) {
        if (highScores.size() < MAX_HIGH_SCORES || newScore > highScores.get(highScores.size() - 1)) {
            highScores.add(newScore);
            Collections.sort(highScores, Collections.reverseOrder());
            while (highScores.size() > MAX_HIGH_SCORES) {
                highScores.remove(highScores.size() - 1);
            }
        }
	}

	
	public void checkMaze() { // Håller reda på prickarna
		int i = 0;
		boolean finished = true;

		// Kikar efter om det fortfarande finns prickar att äta
		for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
			if ((screenData[i] & 16) !=0) {
				finished = false;
				break;
			}
		}
		// Om det ej finns prickar kvar så har pacman klarat av banan och får då 500 poäng och så börjar det om fast med fler spöken
		if (finished) {
			score += 500;

			if (N_GHOSTS * 2 < MAX_GHOSTS) {
				System.out.println(N_GHOSTS + " N_GHOSTS before * 2");
				N_GHOSTS = N_GHOSTS*2;
				System.out.println(N_GHOSTS + " N_GHOSTS");
				ghostclass.setN_GHOSTS(N_GHOSTS);
				ghostclass.setGhostActive();
				System.out.println("Spelets spöken fördubblas");
			} else if (N_GHOSTS + 3 < MAX_GHOSTS) {
				N_GHOSTS += 3;
			} else if (N_GHOSTS + 2 < MAX_GHOSTS) {
				N_GHOSTS += 2;
			} else if (N_GHOSTS + 1 < MAX_GHOSTS) {
				N_GHOSTS += 1;
			}
			if (getCurrentSpeed() < maxSpeed) {
				setCurrentSpeed(getCurrentSpeed() + 1);
			}
			initLevel();
		}
	}


	private void death() { // Denna funktion körs när pacman blir käkad. 
		lives--;

		if (lives == 0) { // om du tappar alla dina liv så skickar vi poängen till resultat tavlan.
			addScore(score);
			gameOver = true; // Sedan så kör vi gameover skärmen.
			inGame = false; // Och slutligen så stänger vi av inGame logiken
		}
		continueLevel();  // Denna kör vi så att spelet inte ska ta slut bara för att man tappar ett liv om man har fler liv.
	}


	Timer coordinateTimer = new Timer(5000, new ActionListener() { // Timer för att hålla koll så att changeCoordinates körs efter en viss specificerad tid
	    @Override
	    public void actionPerformed(ActionEvent e) { // När timern har gått ut så får powerupsen nya koordinater
	        changeCoordinates();
	    }
	});


	private void changeCoordinates() { 	// Funktion för att byta koordinater för powerups. Hur ofta bestäms av Timern ovan.
	    int[][] coordinates = {	
	    		{3, 16}, {9, 3}, {7, 2},
	    		{18, 14}, {2, 14}, {6, 5}, {18, 14}, {13, 18}, {3, 7} ,{3, 16}, {3, 9}, {3, 12}, {3, 12}, {12, 3}, {9, 3}, {7, 2}, {12, 4}, {2, 4}, 
	    		{17, 15}, {3, 14}, {3, 6}, {11, 14}, {11, 17}, {6, 7} ,{2, 16}, {2, 9}, {3, 6}, {5, 12}, {19, 3}, {7, 3}, {2, 2}, {3, 2}, {2, 17}, 
	    };

	    Random rand = new Random();
	    
	    // Här tilldelar jag random koordinater för varje powerup och jag la även till en checker ifall någon av dem skulle hamna på samma koordinat så att de då byter plats.

	    int randomIndex = rand.nextInt(coordinates.length);
	    randomCoordinate_x = coordinates[randomIndex][0];
	    randomCoordinate_y = coordinates[randomIndex][1];
	    
	    int randomIndexTwo;
	    do {
	        randomIndexTwo = rand.nextInt(coordinates.length);
	    } while (coordinates[randomIndexTwo][0] == randomCoordinate_x && coordinates[randomIndexTwo][1] == randomCoordinate_y);
	    randomCoordinateTwo_x = coordinates[randomIndexTwo][0];
	    randomCoordinateTwo_y = coordinates[randomIndexTwo][1];
	    
	    int randomIndexThree;
	    do {
	        randomIndexThree = rand.nextInt(coordinates.length);
	    } while (
	        (coordinates[randomIndexThree][0] == randomCoordinate_x && coordinates[randomIndexThree][1] == randomCoordinate_y) ||
	        (coordinates[randomIndexThree][0] == randomCoordinateTwo_x && coordinates[randomIndexThree][1] == randomCoordinateTwo_y)
	    );
	    randomCoordinateThree_x = coordinates[randomIndexThree][0];
	    randomCoordinateThree_y = coordinates[randomIndexThree][1];
	}


	private void initGame() { // Spelet initieras - sätt ut startvärden
		lives = 1; // Startar med ett liv
		score = 0; // Startar med 0 poäng.
		initLevel(); // initiera banan
		N_GHOSTS = 2; // Startar med åtta spöken
		setCurrentSpeed(2); // Pacmans hastighet initieras
	}


	private void initLevel() { // Kopiera spelplanen från den bana som valts: selectedMap
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
			random = (int) (Math.random() * (getCurrentSpeed() + 1));

			if (random > getCurrentSpeed()) {
				random = getCurrentSpeed();
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


	public void paintComponent(Graphics g) { // Här ritar vi ut allt som ska ritas ut. Detta gör vi genom java.awt.Graphics klassen som importerats
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);

		gameMap.draw(g2d);
		drawScoreAndLives(g2d);
		drawHighScore(g2d);
		drawPowerUpText(g2d);
		pacmanclass.drawPac(g2d);
		ghostclass.draw(g2d, ghost_x, ghost_y);
		drawEatPowerup(g2d);
		drawHealthPowerup(g2d);
		drawSpeedPowerup(g2d);


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


	public void incrementScoreByFive() { // Denna poängökning körs när man äter ett spöke
		score += 5;
	}

	
	public void addLife() { // Säger sig självt. Om du har färre än fem liv så ökar du med ett liv när du tar den powerupen
		if (lives <= 4) {
		lives++;
		}
	}
	
	
	public void updateNumGhosts(int numGhosts) {
		this.N_GHOSTS = numGhosts;
	}

	
	public void removeGhost(int currentGhostIndex) {
		// kallar på removeghost funktionen i ghost klassen
		ghostclass.removeGhost(currentGhostIndex);
		// Uppdaterar antalet spöken i denna klass vilket uppdaterar N_GHOSTS överallt. Detta går eftersom jag skrivit en get funktion för N_GHOSTS
		N_GHOSTS--;
	}
	
	
	public void incrementScore() {  // Ökar med ett poäng, används för att registrera poäng för prickar som blir käkade.
		score++;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	
	public Ghost getGhostClass() {
		return ghostclass;
	}
	
	public short[] getScreenData() {
		// TODO Auto-generated method stub
		return screenData;
	}

	public short[] getCurrentMap(short[] currentMap) {
		selectedMap = currentMap;
		return selectedMap;
	}
	
	public int getCurrentSpeed() {
		return currentSpeed;
	}

	public int get_N_GHOSTS() {
		return N_GHOSTS;
	}
	
	public int get_randomCoordinate_x() {
		return randomCoordinate_x;
	}
	
	public int get_randomCoordinate_y() {
		return randomCoordinate_y;
	}
	
	public int get_randomCoordinateTwo_x() {
		return randomCoordinateTwo_x;
	}
	
	public int get_randomCoordinateTwo_y() {
		return randomCoordinateTwo_y;
	}
	
	public int get_randomCoordinateThree_x() {
		return randomCoordinateThree_x;
	}
	
	public int get_randomCoordinateThree_y() {
		return randomCoordinateThree_y;
	}
	
	public void set_randomCoordinate_x(int newx) {
		this.randomCoordinate_x = newx;
	}
	
	public void set_randomCoordinate_y(int newy) {
		this.randomCoordinate_y = newy;
	}
	
	public void set_randomCoordinateTwo_x(int newx) {
		this.randomCoordinateTwo_x = newx;
	}
	
	public void set_randomCoordinateTwo_y(int newy) {
		this.randomCoordinateTwo_y = newy;
	}
		
	public void set_randomCoordinateThree_x(int newx) {
		this.randomCoordinateThree_x = newx;
	}
	
	public void set_randomCoordinateThree_y(int newy) {
		this.randomCoordinateThree_y = newy;
	}
	
	public void setCurrentSpeed(int currentSpeed) {
		this.currentSpeed = currentSpeed;
	}
	

}
