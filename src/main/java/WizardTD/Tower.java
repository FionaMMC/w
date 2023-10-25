package WizardTD;

import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Iterator;



public class Tower {
    private boolean isChosen;
    private int towerRange;
    private int speed;
    private int damage;
    private int x;
    private int y;
    private PImage towerImage;
    private ArrayList<FireBalls>f;
    private App app;


    public Tower(App app,int towerRange, double speed, int damage, int x, int y, PImage towerImage) {
        this.app = app;
        this.towerRange = towerRange;
        this.towerImage = towerImage;
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.speed = (int) (60/speed);
        isChosen = false;
        this.f = new ArrayList<>();

    }
    public void setIsChosen(boolean chosen) {
        this.isChosen = chosen;
    }
    public int getX(){
        return  this.x;
    }
    public int getY(){
        return  this.y;
    }
    public boolean isChosen() {
        return isChosen;
    }

    public void draw(App app, int numfram) {
        if(isChosen){
            //edit the ellipse to circle with holo
            app.stroke(255,255,0);
            app.noFill();
            app.ellipse(x,y,2*towerRange,2*towerRange);
        }
        app.image(towerImage, x-16, y-16);
        //System.out.println(numfram);
        if(numfram%speed == 0){
            Monster target = findOneTarget();
            if(target != null){
                FireBalls fire = new FireBalls(app, getX(), getY(),damage,target);
                f.add(fire);
            }
        }

        Iterator<FireBalls> iterator = f.iterator();
        while (iterator.hasNext()) {
            FireBalls fire = iterator.next();
            fire.draw(app);
            if (fire.vanish) {
                iterator.remove();
                //System.out.println("fireball vanished!");
            } else {
                //System.out.println("fire!!");
            }
        }
    }

    private  Monster findOneTarget(){
        //System.out.println("start to find target");
        for(Wave w : app.waves){
            //System.out.println("start to load waves");
            for(Monster m: w.getMonsters()){
                if(inrange(m,x,y)&&!m.isDying()&&!m.isDead()/*&&!m.getIsFired()*/){
                    //System.out.println("target found");
                    return m;
                    }
                }
            }
        System.out.println("target not found");
        return null;
    }
    public boolean inrange(Monster m,int x,int y){
        int monsterX = (int) m.getX();
        int monsterY = (int) m.getY();
        double distance = Math.sqrt((monsterX - x) * (monsterX - x) + (monsterY - y) * (monsterY - y));
        if (distance <= towerRange) {
            //System.out.println("monster in range");
            return true;
        }


        return false;




    }

}
