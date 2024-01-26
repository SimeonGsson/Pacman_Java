import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * Klassen Barrel representerar en tunna-entitet i ett spel.
 *
 * @param explosion            Bilden som representerar explosionen.
 * @param shouldExplode        Flagga som indikerar om tunnan ska explodera.
 * @param explosionDurationTimer Tidtagare för explosionens varaktighet.
 * @param model                Modellreferens för grafiska operationer.
 * @param mrpac                Referens till Pacman-objektet.
 * @param BShrek               Referens till BarrelShrek-objektet.
 */

public class Barrel {
    private Image explosion;
    private boolean shouldExplode;
    private Timer explosionDurationTimer;
    private Model model;
    private Pacman mrpac;
    private BarrelShrek BShrek;
    
    /**
     * Konstruerar ett Barrel-objekt med den angivna Pacman.
     *
     * @param pacman Pacman-objektet som är kopplat till tunnan.
     */

    public Barrel(Pacman pacman) {
    	
    	mrpac = pacman;
        setShouldExplode(false); // Det är inte denna som spammar om värdet till false
        loadImages();
        
        explosionDurationTimer = new Timer(1300, new ActionListener() { // Denna gäller endast 1,3 sekunder
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("explosionDurationTimer körs");
                shouldExplode = false; // Det är inte denna som spammar om värdet till false
                explosionDurationTimer.stop();
                explosionDurationTimer.setRepeats(false);
            }
        });
        
    }
    
    /**
     * Laddar bilder, inklusive explosionsbilden.
     */
    
    private void loadImages() {
    	explosion = new ImageIcon("C:\\Users\\simeo\\Downloads\\3iCN-ezgif.com-resize (1).gif").getImage();
    }

    /**
     * Ritar explosionen vid angivna koordinater.
     *
     * @param g2d Grafikkontexten.
     * @param x   x-koordinaten för ritning.
     * @param y   y-koordinaten för ritning.
     */
    public void drawExplosion(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(explosion, x-35, y-40, model);
    	explosionDurationTimer.start();
    }
    
    /**
     * Sätter om tunnan ska explodera eller inte.
     *
     * @param explodeYesOrNo Sant om tunnan ska explodera, annars falskt.
     */
	public void setShouldExplode(boolean ExplodeYesOrNo) {
		shouldExplode = ExplodeYesOrNo;
	}
	
	 /**
     * Hämtar det aktuella explosionsstatuset för tunnan.
     *
     * @return Sant om tunnan ska explodera, annars falskt.
     */
	public boolean getShouldItExplodeOrNah() {
		return shouldExplode;
	}

	 /**
     * Räknar och returnerar koordinaterna för explosionsområdet baserat på tunnans position.
     *
     * @param barrelExplosionCordX x-koordinaten för tunnans explosionscentrum.
     * @param barrelExplosionCordY y-koordinaten för tunnans explosionscentrum.
     * @return En lista med koordinater som representerar explosionsområdet.
     */
	public List<int[]> CountExplosionArea(int barrelExplosionCordX, int barrelExplosionCordY) {
		int startX = Math.max(0, barrelExplosionCordX - 24);  // Sätter start ett block till vänster om barrel
		int endX = Math.min(barrelExplosionCordX + 48, 480);  // Sätter end ett block till höger om barrel
		int startY = Math.max(0, barrelExplosionCordY - 24);  // Sätter start ett block över barrel
		int endY = Math.min(barrelExplosionCordY + 48, 480);  // Sätter start ett block under barrel

		startX = Math.max(0, barrelExplosionCordX - 24);
		endX = Math.min(barrelExplosionCordX + 24, 480);

		startY = Math.max(0, barrelExplosionCordY - 24);
		endY = Math.min(barrelExplosionCordY + 24, 480);

		List<int[]> explosionArea = new ArrayList<>();
		for (int i = startX; i < endX; i+= 24) { 
			for (int j = startY; j < endY; j+= 24) { 
				explosionArea.add(new int[]{i, j});
//				System.out.println("cord added");
//				System.out.println(explosionArea.size());
			}
		}
		return explosionArea;
	}

	 /**
     * Initierar explosionen vid angivna koordinater och kontrollerar om Pacman påverkas.
     *
     * @param barrelExplosionCordX x-koordinaten för tunnans explosionscentrum.
     * @param barrelExplosionCordY y-koordinaten för tunnans explosionscentrum.
     */
	
	public void Explode(int barrelExplosionCordX, int barrelExplosionCordY) {
		List<int[]> ExplosionArea;
		int[] MrPacCoordinates = {mrpac.getX(), mrpac.gety()};
		ExplosionArea = CountExplosionArea(barrelExplosionCordX, barrelExplosionCordY);
		for (int i = 0; i < ExplosionArea.size(); i++) {
			int[] currentExplosionCoordinate = ExplosionArea.get(i);
			if (currentExplosionCoordinate[0] == MrPacCoordinates[0] && currentExplosionCoordinate[1] == MrPacCoordinates[1]) {
				System.out.println("Explosionen har eliminerat mrPAC");
				mrpac.setDeath(true);
				break;
			}
		}
	}
}
