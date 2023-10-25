package WizardTD;

import processing.core.PImage;

public class Path {
    private int x, y;
    private PImage pathSprite;

    public Path(PImage pathSprite, int x, int y) {
        this.pathSprite = pathSprite;
        this.x = x;
        this.y = y;
    }

    public void draw(App app) {
        app.image(pathSprite, x, y);
    }
}

