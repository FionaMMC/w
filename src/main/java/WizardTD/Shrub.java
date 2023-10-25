package WizardTD;
import processing.core.PImage;

public class Shrub {
    private int x, y;
    private PImage shrubSprite;

    public Shrub(PImage shrubSprite, int x, int y) {
        this.shrubSprite = shrubSprite;
        this.x = x;
        this.y = y;
    }

    public void draw(App app) {
        app.image(shrubSprite, x, y);
    }
}

