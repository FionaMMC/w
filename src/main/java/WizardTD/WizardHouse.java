package WizardTD;

import processing.core.PImage;

public class WizardHouse {
    private int x, y;
    private PImage wizardHouseSprite;

    public WizardHouse(PImage pathSprite, int x, int y) {
        this.wizardHouseSprite = pathSprite;
        this.x = x;
        this.y = y;
    }

    public void draw(App app) {
        app.image(wizardHouseSprite, x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
