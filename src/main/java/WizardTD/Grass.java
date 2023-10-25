package WizardTD;
import processing.core.PImage;

public class Grass {
    private int x, y;
    private PImage grassSprite;

    public Grass(PImage grassSprite, int x, int y) {
        this.grassSprite = grassSprite;
        this.x = x;
        this.y = y;
    }

    public void draw(App app) {
        app.image(grassSprite, x, y);
    }
}


