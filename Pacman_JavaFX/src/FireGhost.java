import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class FireGhost extends Ghost {
    private List<Fireball> fireballs;
    private Image fireGhost;
    
    public FireGhost(/* parameters */) {
        super(/* appropriate parameters */);
        fireballs = new ArrayList<>();
    }
    
    private void loadImages() {
		fireGhost = new ImageIcon("C:\\Users\\simeo\\Downloads\\fireMonster.gif").getImage();
	}

 // In your FireGhost class
    public void shootFireball(Pacman pacman) {
        Point pacmanLocation = pacman.getPacManLocation();
        Fireball fireball = new Fireball(this.getGhost_x()[0], this.getGhost_y()[0], pacmanLocation.x, pacmanLocation.y);
        fireballs.add(fireball);
    }


    public void move() {
        super.move(); // Call the move method from Ghost to move the FireGhost

        // Move each fireball
        for (Fireball fireball : fireballs) {
            fireball.move();
        }
    }

    public void draw(Graphics2D g2d, int[] x, int[] y) {
		for (int i = 0; i < getN_GHOSTS(); i++) {
			if (ghostActive[i]) {
				g2d.drawImage(fireGhost, x[i], y[i], null);
			} else {
				System.out.println("This ghost is not active");
			}
		}
	}

    @Override
    public void draw(Graphics2D g2d, int[] x, int[] y) {
        super.draw(g2d, x, y); // Call the superclass draw method with correct parameters

        // Draw each fireball
        for (Fireball fireball : fireballs) {
            fireball.draw(g2d);
        }
    }
    
    // Additional methods as needed...
}
