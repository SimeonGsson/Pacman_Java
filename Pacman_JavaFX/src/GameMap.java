import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class GameMap {
	private final int BLOCK_SIZE = 24;
	private final int N_BLOCKS;
	private final int SCREEN_SIZE;
	private short[] screenData;
	
	private final short mapOne[] = {
			19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 26, 26, 26, 26, 26, 26, 26, 26, 22,
			17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0, 0, 0, 0, 0, 0, 0, 0, 21,
			25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 20, 0, 19, 26, 26, 26, 26, 22, 0, 21,
			1,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 18, 20, 0, 0, 0, 0, 17, 18, 20,
			19, 18, 18, 18, 18, 18, 16, 24, 24, 24, 24, 24, 16, 18, 18, 26, 26, 24, 24, 20,
			17, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0,  0,  17,   16, 20,  0,  0,  0, 0, 21,
			17, 16, 16, 16, 16, 16, 16, 18, 18, 18, 18,  18,  16,   16, 20,  0,  0,  0, 0, 21,
			17, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16,  16,  16,   16, 20,  0,  0,  0, 0, 21,
			17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 18, 18, 18, 18, 20,
			17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 24, 24, 24, 24, 24, 24, 16, 20,
			21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 20, 0, 0, 0, 0, 0, 0, 17, 20,
			17, 18, 18, 22, 0, 19, 26, 18, 16, 16, 16, 16, 26, 26, 26, 26, 26, 26, 16, 20,
			17, 16, 16, 20, 0, 21, 0, 17, 16, 16, 16, 20, 0, 0, 0, 0, 0, 0, 17, 20,
			17, 16, 16, 20, 0, 21, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 18, 18, 16, 20,
			17, 24, 24, 28, 0, 25, 26, 24, 16, 16, 16, 16, 24, 24, 24, 24, 24, 16, 16, 20,
			21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 20, 0, 0, 0, 0, 0, 25, 24, 20,
			17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 26, 26, 26, 30, 0, 0, 0, 21,
			17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 0, 19, 18, 20,
			17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 18, 18, 18, 22, 0, 17, 16, 20,
			25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 26, 24, 24, 28
	};

	private final short mapTwo[] = {
			19, 18, 18, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
			17, 16, 20, 0, 17, 16, 24, 24, 24, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
			17, 24, 28, 0, 17, 20, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
			21,  0,  0,  0,  17,  20, 0, 19, 18, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
			17, 18, 18, 18, 16, 20, 0, 17, 16, 16, 24, 24, 24, 24, 24, 24, 24, 24, 24, 20,
			17, 16, 16, 16, 16, 20, 0, 17, 16, 20, 0,  0,  0,   0, 0,  0,  0,  0, 0, 21,
			17, 16, 16, 16, 16, 16, 18, 16, 16, 20, 0,  0,  0,   0, 0,  0,  0,  0, 0, 21,
			17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 0,  0,  0,  0, 0, 21,
			17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 18, 26, 18, 18, 18, 20,
			17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 24, 24, 16, 20, 0, 17, 16, 16, 20,
			21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 20, 0, 0, 17, 20, 0, 17, 16, 16, 20,
			17, 18, 18, 22, 0, 19, 18, 26, 24, 16, 20, 0, 0, 25, 16, 18, 24, 24, 16, 20,
			17, 16, 16, 20, 0, 17, 20, 0, 0, 17, 20, 0, 0, 0, 17, 20, 0, 0, 17, 20,
			17, 16, 16, 20, 0, 17, 16, 18, 18, 16, 16, 22, 0, 19, 16, 16, 18, 18, 16, 20,
			17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
			21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 24, 16, 18, 16, 16, 16, 24, 24, 24, 20,
			17, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 17, 16, 16, 16, 20, 0, 0, 0, 21,
			17, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 25, 16, 16, 16, 20, 0, 19, 18, 20,
			17, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 0, 17, 16, 16, 20, 0, 17, 16, 20,
			25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 26, 26, 24, 24, 24, 24, 26, 24, 24, 28
	};
	
	private final short TestMap[] = {
			19, 18, 18, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			21,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			21,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			21,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			17,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 26, 26, 24, 24, 24, 24, 26, 24, 24, 28
	};
	// Logik för banan - Gick inte så bra med att bara utgå från koordinaten eftersom pacman då kan åka igenom halva väggen innan det tar stopp. Detta sätt funkar bättre och då fick det bli såhär trots att det är lite komplicerat ibland. 
	// 0 = vägg, 1 = vägg till vänster, 2 = tak vägg, 4 = vägg till höger, 8 = golv vägg, 16 = ätbara platser
	// Gäller att plussa ihop dem för att få dem att fungera. T.ex när det är en vägg både till höger och vänster så får man plussa ihop
	// 16 för en vit prick + 2 för ett tak + 8 för golvet = 26
	
	public GameMap(int n_blocks, short[] screenData) { // Initiera / aktivera variablerna
		this.screenData = screenData;
		this.N_BLOCKS = n_blocks;
		this.SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
	}
	
	public int getBlockSIze() {
		return BLOCK_SIZE;
	}
	public int getNBlocks() {
		return N_BLOCKS;
	}
	public int getScreenSize() {
		return SCREEN_SIZE;
	}
	public short[] getLevelData() {
		return screenData;
	}
	public short[] getMapOne() {
		return mapOne;
	}
	public short[] getMapTwo() {
		return mapTwo;
	}
	public short[] getTestMap() {
		return TestMap;
	}
	
	public void draw(Graphics2D g2d) { // Denna funktion används för att rita ut banan. Kommer repetetivt kallas på i gameloopen i model
		short i = 0;
		int x, y;
		
		for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
			for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {
				
				g2d.setColor(Color.black);
				g2d.setStroke(new BasicStroke(3));
				
				if ((screenData[i] == 0)) { 
					g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
				}
				g2d.setColor(new Color(0,153,0));
				g2d.setStroke(new BasicStroke(3));
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
					drawPellet(g2d, x, y);
	                }
	                i++;
	            }
	        }
	    }

	private void drawPellet(Graphics2D g2d, int x, int y) { // Rita upp de vita prickarna på de platser som ska ha det
		g2d.setColor(new Color(255,255,255));
		g2d.fillOval(x + 10, y + 10, 6, 6);
	}
}
