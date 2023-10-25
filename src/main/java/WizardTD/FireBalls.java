package WizardTD;

import processing.core.PImage;

public class FireBalls {
    private App app;
    private float targetX;
    private float targetY;
    private int x;
    private int y;
    private PImage fireSprite;
    private int damage;
    private Monster target;
    public boolean vanish;
    private float speed = 5;

    public FireBalls(App app, int x, int y, int damage, Monster target) {
        this.x = x;
        this.y = y;
        this.targetX = target.getX();
        this.targetY = target.getY();
        this.damage = damage;
        this.target = target;
        this.vanish = false;
        this.fireSprite = app.loadImage("src/main/resources/WizardTD/fireball.png");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(App app) {
        move();
        app.image(fireSprite, x, y);
        if(hasReachedTarget()){
            hitTarget();
            vanish = true;
        }
    }
    public void move() {
        float angle = app.atan2(targetY - y, targetX - x);
        x += app.cos(angle) * speed;
        y += app.sin(angle) * speed;
    }

    public boolean hasReachedTarget() {
        float distance = app.dist(x, y, targetX, targetY);
        return distance < speed;
    }

    private void hitTarget() {
        target.setHp((int) (target.getHp() - damage*target.getArmour()));
        if (target.getHp()<0){
            target.setHp(0);
        }
    }


}


