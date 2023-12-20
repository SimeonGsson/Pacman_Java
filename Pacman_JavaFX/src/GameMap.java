import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class GameMap {
	private final int BLOCK_SIZE = 24;
	private final int N_BLOCKS;
	private final int SCREEN_SIZE;
	private short[] levelData; // Layout of the map
	
	public GameMap(int n_blocks, short[] levelData) {
		this.levelData = levelData;
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
		return levelData;
	}
	
	public void draw(Graphics2D g2d) {
		short i = 0;
		int x, y;
		 for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
	            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {
	                g2d.setColor(new Color(0,153,0));
	                g2d.setStroke(new BasicStroke(3));
	                if ((levelData[i] == 0)) { 
	                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
	                }
	                if ((levelData[i] & 1) != 0) { 
	                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
	                }
	                if ((levelData[i] & 2) != 0) { 
	                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
	                }
	                if ((levelData[i] & 4) != 0) { 
	                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
	                            y + BLOCK_SIZE - 1);
	                }
	                if ((levelData[i] & 8) != 0) { 
	                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
	                            y + BLOCK_SIZE - 1);
	                }
	                if ((levelData[i] & 16) != 0) { 
	                    g2d.setColor(new Color(255,255,255));
	                    g2d.fillOval(x + 10, y + 10, 6, 6);
	                }
	                i++;
	            }
	        }
	    }

}
