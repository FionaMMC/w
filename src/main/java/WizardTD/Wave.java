package WizardTD;

import java.util.ArrayList;
import java.util.Iterator;


public class Wave {
    private int duration;
    private double preWavePause;
    private ArrayList<Monster> monsters;

    public Wave(int duration, double preWavePause, ArrayList<Monster> monsters){
        this.duration = duration;
        this.preWavePause = preWavePause;
        this.monsters = monsters;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public double getPreWavePause() {
        return preWavePause;
    }

    public int getDuration() {
        return duration;

    }
    public void draw(App app, int countfram) {
        if (countfram >= preWavePause * 60) {
            Iterator<Monster> iterator = monsters.iterator();
            while (iterator.hasNext()) {
                Monster m = iterator.next();
                if (m.isDead()) {
                    app.mana += (int) m.getMana_gained_on_killed();
                    iterator.remove();
                } else {
                    if (m.isDying()) {
                        m.DeathAnimation();
                    }
                    m.draw(app, countfram);
                }
            }
        }
    }







}
