import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.Timer;

public class BarrelShrek extends Shrek {
	private List<Barrel> barrels;
	private int N_BSHREKS;
	private int barrelx, barrely, barreldx, barreldy = 0;
	private int tempx, tempy, tempdx, tempdy = 0;
	private Image barrelShrek;
	private Image barrelLeft;
	private Image barrelRight;
	private Image barrelUp;
	private Image barrelDown;
	private Timer shootingBarrelTimer;
	private Timer barrelRollingDuration;
	private Timer CopyIncomingShootingBarrelValues;
	private int barrelSpeed;
	private boolean shouldBarrelRollOrNot = false;
	private Model model;
	int xbarrel;
	int ybarrel;

	public BarrelShrek(Barrel barrel, short[] screenData, int N_BLOCKS, int N_SHREKS, int BLOCK_SIZE, int[] fghost_x, int[] fghost_y, int[] ghost_dx, int[] ghost_dy, int[] dx, int[] dy, int[] ghostSpeed) {
		super(barrel, screenData, N_BLOCKS, N_SHREKS, BLOCK_SIZE, fghost_x, fghost_y, ghost_dx, ghost_dy, dx, dy, ghostSpeed);
		barrels = new ArrayList<>();
		N_BSHREKS = N_SHREKS/2;
		barrelSpeed = 2;
		loadImages();
		xbarrel = 700;

		CopyIncomingShootingBarrelValues = new Timer(9900, new ActionListener() { // Kopiera relevanta värden sålänge som tunnan ska rulla
			@Override
			public void actionPerformed(ActionEvent e) {
				TemporaryValuesToDrawRollingBarrel();
				System.out.println("Nu tilldelas tempvärden");
				CopyIncomingShootingBarrelValues.stop();
				CopyIncomingShootingBarrelValues.setRepeats(false);
			}
		});

		shootingBarrelTimer = new Timer(10000, new ActionListener() { // Var 20:e sekund så ska alla barrelShreks kasta en tunna	
			@Override
			public void actionPerformed(ActionEvent e) {
				shouldBarrelRollOrNot = true;
				barrelRollingDuration.start(); // Låt tunnan börja rulla
				CopyIncomingShootingBarrelValues.start(); // Anledningen till att detta behövs är eftersom tunnan annars riskerar att hamna på utanför en cell
				System.out.println("Nu börjar barrelRollingDuration timern");
				System.out.println("BarrelX = " + xbarrel);
				System.out.println("BarrelY = " + ybarrel);
			}
		});

		barrelRollingDuration = new Timer(4000, new ActionListener() { // Tunnan ska rulla i 2 sekunder
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					System.out.println("Nu slutar barrelRollingDuration");
					barrel.setShouldExplode(true);
					System.out.println("Nu sattes barrekClass.SetShouldExplode till true - från barrelRollingDuration funktionen");
					System.out.println(barrel.getShouldItExplodeOrNah() + " - Värdet för getShouldItExplodeOrNah i BarrelRollingDuration");
					shouldBarrelRollOrNot = false;
					barrelRollingDuration.stop(); // Fattar inte varför vi behöver lägga till denna för att den inte ska köras två gånger. Men greatness. Nu funkar det // Simeon
					barrelRollingDuration.setRepeats(false);
				}
			}
		});

		CopyIncomingShootingBarrelValues.start();
		shootingBarrelTimer.start();
	}

	public void TemporaryValuesToDrawRollingBarrel() {

		if (barrelx < 24 || barrely < 24 || barrelx % BLOCK_SIZE != 0 || barrely % BLOCK_SIZE != 0) {
			System.out.println("Barrel coordinates not fully on a cell");
			Timer tryAgainTimer = new Timer(40, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TemporaryValuesToDrawRollingBarrel();
					((Timer) e.getSource()).stop();
				}
			});
			tryAgainTimer.setRepeats(false);
			tryAgainTimer.start();
		} else {
			tempx = barrelx;
			tempy = barrely;
			tempdx = barreldx;
			tempdy = barreldy;
			System.out.println("Barrelx = " + barrelx);
			System.out.println("Barrely = " + barrely);
			System.out.println("Barrel coordinates assigned");
		}
	}


	public void ChangeTemporaryValues(int x, int y, int dx, int dy) {
		barrelx = x;
		barrely = y;
		barreldx = dx;
		barreldy = dy;
	}


	private void loadImages() {
		barrelShrek = new ImageIcon("C:\\Users\\simeo\\Downloads\\fireMonster-ezgif.com-resize (1).gif").getImage();
		barrelLeft = new ImageIcon("C:\\Users\\simeo\\Downloads\\barrel.left.gif").getImage();
		barrelUp = new ImageIcon("C:\\Users\\simeo\\Downloads\\barrel.up.gif").getImage();
		barrelDown = new ImageIcon("C:\\Users\\simeo\\Downloads\\barrel.down.gif").getImage();
		barrelRight = new ImageIcon("C:\\Users\\simeo\\Downloads\\barrel.right.gif").getImage();
	}


	public void drawRollingBarrel(Graphics2D g2d) {
		int cellState;
		int pos = tempx / BLOCK_SIZE + N_BLOCKS * (int) (tempy / BLOCK_SIZE);

		cellState = getScreenData()[pos];
		if (tempdx != 0 || tempdy != 0) {
			if (!((tempdx == -1 && tempdy == 0 && (cellState & 1) != 0) // 1 för vägg till vänster
					|| (tempdy == 1 && tempdy == 0 && (cellState & 4) != 0) // ftempyra för vägg till höger
					|| (tempdx == 0 && tempdy == -1 && (cellState & 2) != 0) // två för tak
					|| (tempdx == 0 && tempdy == 1 && (cellState & 8) != 0))) { // åtta för golv
				xbarrel = tempx + tempdx * barrelSpeed;
				ybarrel = tempy + tempdy * barrelSpeed;
				//				System.out.println("xbarrel = " + xbarrel);
				//				System.out.println("ybarrel = " + ybarrel);
				if (tempx < 590 || tempy < 555) {
					if (tempdx == -1) {
						g2d.drawImage(barrelLeft, tempx + 1, tempy + 1, model );
	//					System.out.println("Left Barrel ritas ut");
					} else if (tempdx == 1) {
						g2d.drawImage(barrelRight, tempx + 1, tempy + 1, model);
	//					System.out.println("Right Barrel ritas ut");
					} else if (tempdy == -1) {
						g2d.drawImage(barrelUp, tempx + 1, tempy + 1, model);
	//					System.out.println("Up Barrel ritas ut");
					} else {
						g2d.drawImage(barrelDown, tempx + 1, tempy + 1, model);
	//					System.out.println("Down Barrel ritas ut");
					} 
				} else {
				//	System.out.println("Utanför banan");
				}
			} else {
			//	System.out.println("Vägg");
			}
		}
	}


	public void draw(Graphics2D g2d, int[] x, int[] y) {
		for (int i = 0; i < getN_BSHREKS(); i++) {
			if (shrekActive[i]) {
				g2d.drawImage(barrelShrek, x[i], y[i], model);
			} else {
				System.out.println("Detta spöke är ej aktiverat");
			}
		}
	}

	public int getBarrelCordX() {
		return tempx;
	}

	public int getBarrelCordY() {
		return tempy;
	}

	public int getN_BSHREKS() {
		return N_BSHREKS;
	}

	public void setN_BSHREKS(int bShreks) {
		N_BSHREKS = bShreks;
	}

	public boolean getShouldBarrelRollOrNot() {
		return shouldBarrelRollOrNot;
	}
}
