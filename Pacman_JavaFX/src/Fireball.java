import java.awt.Graphics2D;

public class Fireball {
    private int x, y; // Position
    private int dx, dy; // Direction

    public Fireball(int startX, int startY, int directionX, int directionY) {
        this.x = startX;
        this.y = startY;
        this.dx = directionX;
        this.dy = directionY;
    }

    public void move() {
        // Update the fireball's position based on its direction
        x += dx;
        y += dy;
    }

    public void draw(Graphics2D g2d) {
        // Draw the fireball on the screen
    }
}

