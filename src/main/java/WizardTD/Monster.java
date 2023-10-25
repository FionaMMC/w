package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

public class Monster {

    private PImage type;
    private int hp;
    private double fullhp;
    private double speed;
    private double armour;
    private double mana_gained_on_killed;
    private double pre_wave_pause;
    private ArrayList<Point> path;

    private boolean isDead;

    private int pathIndex;
    private State state = State.ALIVE;
    private int deathAnimationFrame = 0;
    private PImage[] deathAnimationFrames;

    private App app;
    private int frameDuration = 5;
    private int currentFrameCount = 0;

    public double getArmour() {
        return armour;
    }

    public enum State {
        ALIVE, DYING, DEAD
    }

    public double getMana_gained_on_killed() {
        return mana_gained_on_killed;
    }

    private Position position;

    public Monster(App app, PImage type, int hp, double speed, double armour, double mana_gained_on_killed, ArrayList<Point> path, double pre_wave_pause) {
        this.type = type;
        this.hp = hp;
        this.fullhp = hp;
        this.speed = speed;
        this.armour = armour;
        this.mana_gained_on_killed = mana_gained_on_killed;
        this.path = path;
        this.pre_wave_pause = pre_wave_pause;
        this.pathIndex = 0;
        this.app = app;

        this.isDead = false;
        this.position = new Position(path.get(0).getX() * 32, path.get(0).getY() * 32 + 40);

        this.deathAnimationFrames = new PImage[] {
                app.loadImage("src/main/resources/WizardTD/gremlin1.png"),
                app.loadImage("src/main/resources/WizardTD/gremlin1.png"),
                app.loadImage("src/main/resources/WizardTD/gremlin2.png"),
                app.loadImage("src/main/resources/WizardTD/gremlin3.png"),
                app.loadImage("src/main/resources/WizardTD/gremlin4.png"),
                app.loadImage("src/main/resources/WizardTD/gremlin5.png"),
                app.loadImage("src/main/resources/WizardTD/gremlin5.png"),
                app.loadImage("src/main/resources/WizardTD/gremlin5.png")

        };
    }

    public float getX() {
        return position.getX()+8;
    }


    public float getY() {
        return position.getY()+40;
    }

    public boolean isDead() {
        return isDead;
    }
    public void DeathAnimation() {
        if (currentFrameCount < frameDuration) {
            currentFrameCount++;
            return;
        }

        if (deathAnimationFrame < deathAnimationFrames.length - 3) {
            deathAnimationFrame++;
        } else {
            state = State.DEAD;
            setIsDead(true);
        }

        currentFrameCount = 0;
    }
    public PImage getCurrentDeathAnimationFrame() {
        return deathAnimationFrames[deathAnimationFrame];
    }
    public boolean isDying() {
        return this.state == State.DYING;
    }

    public void startDying() {
        this.state = State.DYING;
        System.out.println("Monster is dying");
    }






    public void setIsDead(boolean dead){
        this.isDead = dead;
        this.state = State.DEAD;

    }

    public int getHp() {
        return hp;
    }

    public void draw(App app, int numfram) {
        if (numfram >= pre_wave_pause * 60) {
            // Only move the Monster if it's ALIVE
            if (state == State.ALIVE && pathIndex < path.size()) {
                Point targetPoint = path.get(pathIndex);
                float targetX = targetPoint.getX() * 32;
                float targetY = targetPoint.getY() * 32;

                float dx = targetX - position.getX();
                float dy = targetY - position.getY();
                float distance = PApplet.dist(position.getX(), position.getY(), targetX, targetY);

                if (distance > speed) {
                    position.setX((float) (position.getX() + (dx / distance) * speed));
                    position.setY((float) (position.getY() + (dy / distance) * speed));
                } else {
                    position.setX(targetX);
                    position.setY(targetY);
                    if (pathIndex < path.size() - 1) {
                        pathIndex += 1;
                    }else{
                        if(!isDead){
                            app.mana -= getHp();
                            setIsDead(true);
                        }
                    }
                }
            }

            // Drawing the health bar
            int totalWidth = 30;
            int height = 3;
            int w1 = (int) ((hp / fullhp) * totalWidth);
            float healthBarY = position.getY() + 8 + type.height + 5;

            app.fill(255, 0, 0);
            app.noStroke();
            app.rect(position.getX(), healthBarY, totalWidth, height);

            app.fill(0, 128, 0);
            app.rect(position.getX(), healthBarY, w1, height);

            app.stroke(0);
            app.noFill();
            app.rect(position.getX(), healthBarY, totalWidth, height);

            // Determine which image to draw, and handle the death animation logic
            PImage imgToDraw;
            if (state == State.DYING) {
                DeathAnimation();  // Update the death animation frame counter
                imgToDraw = getCurrentDeathAnimationFrame();
                System.out.println("play death animation");
            } else {
                imgToDraw = type;
            }

            app.image(imgToDraw, position.getX() + 8, position.getY() + 40);
        }
    }



    public void setHp(int i) {
        this.hp = i;
        if (this.hp <= 0 && this.state == State.ALIVE) {
            this.startDying();
        }
    }


}





