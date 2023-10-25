package WizardTD;

import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;


public class App extends PApplet {

    public ArrayList<Grass> grass = new ArrayList<Grass>();
    public ArrayList<Shrub> shrubs = new ArrayList<Shrub>();
    public ArrayList<Path> paths = new ArrayList<Path>();
    public ArrayList<Wave> waves = new ArrayList<Wave>();
    public ArrayList<Monster> monsters = new ArrayList<Monster>();
    public ArrayList<Tower> towers = new ArrayList<>();
    public WizardHouse wizardHouse;
    public char[][] layoutBoard = new char[20][20];

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE + TOPBAR;
    public static final int FPS = 60;

    public String configPath;

    //public Random random = new Random();
    private int countfram = 0;
    private int towerrange;
    private double speed;
    private int damage;
    public int mana;
    public int manaCap;
    private int initial_mana_gained_per_second;
    private int towerCoast;

    private boolean isTPressed = false;


    public App() {
        this.configPath = "config.json";
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        background(96, 56, 17);
        frameRate(FPS);

        //draw the grass
        int x = 0, y;
        while (x < 640) {
            y = 40;
            while (y < 680) {
                Grass g = new Grass(loadImage("src/main/resources/WizardTD/grass.png"), x, y);
                grass.add(g);
                y += 32;
            }
            x += 32;
        }

        // Read layout from config
        JSONObject jsonobject = null;
        try {
            FileReader f = new FileReader(this.configPath);
            jsonobject = new JSONObject(f);
            String layout = jsonobject.getString("layout");
            // Get the layout content
            File file = new File(layout);
            BufferedReader layoutReader = new BufferedReader(new FileReader(file));
            String line;
            int counter = 0;

            while ((line = layoutReader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    layoutBoard[counter][i] = line.charAt(i);
                }
                counter++;
            }
            layoutReader.close();
        } catch (FileNotFoundException e) {
            System.err.print("erro");
        } catch (IOException e) {
            System.err.print("erro2");
        }
        //draw path house shurbs
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                char c = layoutBoard[i][j];
                if (c == 'S') {
                    shrubs.add(new Shrub(loadImage(("src/main/resources/WizardTD/shrub.png")), 32 * j, 40 + 32 * i));
                }
                if (c == 'W') {
                    wizardHouse = new WizardHouse(loadImage(("src/main/resources/WizardTD/wizard_house.png")), 32 * j, 40 + 32 * i);

                }
                if (c == 'X') {
                    PathInfo pathInfo = getPathInfo(i, j);
                    if (pathInfo != null) {
                        PImage pathImage = null;
                        double rotation = 0.0;

                        if (pathInfo.openDirections == 1) {
                            pathImage = loadImage("src/main/resources/WizardTD/path0.png");
                            if (pathInfo.up || pathInfo.down) {
                                rotation = 90.0;
                            }
                        } else if (pathInfo.openDirections == 2) {
                            if (pathInfo.up && pathInfo.down) {
                                pathImage = loadImage("src/main/resources/WizardTD/path0.png");
                                rotation = 90.0;
                            } else if (pathInfo.left && pathInfo.right) {
                                pathImage = loadImage("src/main/resources/WizardTD/path0.png");
                                rotation = 0.0;
                            } else {
                                pathImage = loadImage("src/main/resources/WizardTD/path1.png");
                                if (pathInfo.down && pathInfo.right) rotation = 270.0;
                                else if (pathInfo.up && pathInfo.right) rotation = 180.0;
                                else if (pathInfo.up && pathInfo.left) rotation = 90.0;
                                else if (pathInfo.down && pathInfo.left) rotation = 0.0;
                            }
                        } else if (pathInfo.openDirections == 3) {
                            pathImage = loadImage("src/main/resources/WizardTD/path2.png");
                            if (!pathInfo.down) rotation = 180.0;
                            else if (!pathInfo.right) rotation = 90.0;
                            else if (!pathInfo.left) rotation = 270.0;
                            else if (!pathInfo.up) rotation = 0.0;
                        } else if (pathInfo.openDirections == 4) {
                            pathImage = loadImage("src/main/resources/WizardTD/path3.png");
                        }

                        assert pathImage != null;
                        pathImage = rotateImageByDegrees(pathImage, rotation);
                        paths.add(new Path(pathImage, 32 * j, 40 + 32 * i));
                    }
                }
            }
        }
        assert jsonobject != null;
        JSONArray jasonArray = (JSONArray) jsonobject.get("waves");
        double preWavePause = 0;
        for (int i = 0; i < jasonArray.size(); i++) {
            JSONObject SingleWave = (JSONObject) jasonArray.get(i);
            int durartion = SingleWave.getInt("duration");
            preWavePause += SingleWave.getDouble("pre_wave_pause");
            ArrayList<Point> points = new ArrayList<>();
            for (int k = 0; k < 20; k++) {
                if (layoutBoard[0][k] == 'X') {
                    points.add(new Point(0, k));
                }
                if (layoutBoard[19][k] == 'X') {
                    points.add(new Point(19, k));
                }
                if (layoutBoard[k][0] == 'X') {
                    points.add(new Point(k, 0));
                }
                if (layoutBoard[k][19] == 'X') {
                    points.add(new Point(k, 19));
                }
            }
            Random r = new Random();
            int num = r.nextInt(points.size());
            Point start = points.get(num);
            //printPoint(start);

            ArrayList<Point> path = new ArrayList<>();
            int endx = wizardHouse.getX();
            endx = endx / 32;
            int endy = wizardHouse.getY();
            endy = (endy - 40) / 32;
            Point end = new Point(endy, endx);
            //printPoint(end);

            char[][] layoutboardForMonster = fixTheMap(layoutBoard);

            BFSPathFinder(path, layoutBoard, start, end);


            Wave wave = new Wave(durartion, preWavePause, monsters);

            JSONArray monsterArray = (JSONArray) SingleWave.get("monsters");
            for (int j = 0; j < monsterArray.size(); j++) {
                generateMonster(wave, (JSONObject) monsterArray.get(j), path);
            }
            waves.add(wave);
        }
        this.towerrange = jsonobject.getInt("initial_tower_range");
        this.speed = jsonobject.getDouble("initial_tower_firing_speed");
        this.damage = jsonobject.getInt("initial_tower_damage");
        this.mana = jsonobject.getInt("initial_mana");
        this.manaCap = jsonobject.getInt("initial_mana_cap");
        this.initial_mana_gained_per_second = jsonobject.getInt("initial_mana_gained_per_second");
        this.towerCoast = jsonobject.getInt("tower_cost");


    }


    void generateMonster(Wave wave, JSONObject jsonObject, ArrayList<Point> path) {
        double pre_wave_pause = wave.getPreWavePause();
        int quantity = jsonObject.getInt("quantity");
        double interval = (wave.getDuration()) / ((double) quantity);
        double current_pause = pre_wave_pause;
        for (int i = 0; i < quantity; i++) {
            String type = jsonObject.getString("type");
            String pathofImage = String.format("src/main/resources/WizardTD/%s.png", type);
            PImage mon = loadImage(pathofImage);
            int hp = jsonObject.getInt("hp");
            double speed = jsonObject.getDouble("speed");
            double armour = jsonObject.getDouble("armour");
            double mana_gain_on_kill = jsonObject.getDouble("mana_gained_on_kill");

            Monster m = new Monster(this,mon, hp, speed, armour, mana_gain_on_kill, path, current_pause);
            monsters.add(m);

            current_pause += interval;
        }

    }

    void BFSPathFinder(ArrayList<Point> path, char[][] layoutBoard, Point start, Point end) {
        //printLayoutBoard(layoutBoard);
        System.out.println("start path finding");
        Point[][] prev = new Point[20][20];
        boolean[][] visited = new boolean[20][20];

        ArrayList<Point> q = new ArrayList<>();
        q.add(start);
        visited[start.getX()][start.getY()] = true;

        while (!q.isEmpty()) {
            Point p = q.remove(0);
            if (p.equals(end)) {
                break;
            }
            int r = p.getX();
            int c = p.getY();

            // Check all four directions
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] dir : directions) {
                int newR = r + dir[0];
                int newC = c + dir[1];
                if (newR >= 0 && newR < 20 && newC >= 0 && newC < 20 && !visited[newR][newC] && layoutBoard[newR][newC] == 'X') {
                    q.add(new Point(newR, newC));
                    visited[newR][newC] = true;
                    prev[newR][newC] = p;
                }
            }
        }
        //printPrev(prev);

        getPath(start, prev, end, path);


    }

    private void getPath(Point start, Point[][] prev, Point end, ArrayList<Point> path) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        Point current = null;
        Point realPath = null;
        for (int[] dir : directions) {
            int newR = end.getX() + dir[0];
            int newC = end.getY() + dir[1];
            if (newR >= 0 && newR < 20 && newC >= 0 && newC < 20 && prev[newR][newC] != null) {
                current = new Point(newR, newC);
                break;
            }
        }


        if (current == null) {
            System.out.println("End point is not reachable.");
            return;
        }

        while (current != null && !current.equals(start)) {
            realPath = transform(current);
            //printPoint(current);
            path.add(0, realPath);
            current = prev[current.getX()][current.getY()];
        }

        if (current != null) {
            Point realStart = transform(start);
            if(realStart.getX() == 0 ){
                realStart.setX(realStart.getX() - 1);
                printPoint(realStart);
            }
            if(realStart.getX() == 19 ){
                realStart.setX(realStart.getX() + 1);
            }
            if(realStart.getY() == 0){
                realStart.setY(realStart.getY()-1);
                printPoint(realStart);
            }
            if(realStart.getY() == 19 ){
                realStart.setY(realStart.getY() + 1);
            }
            path.add(0, realStart);
        }
    }



    private PathInfo getPathInfo(int i, int j) {
        if (layoutBoard[i][j] != 'X') {
            return null; // Not a path
        }

        boolean up = i > 0 && layoutBoard[i - 1][j] == 'X';
        boolean down = i < 19 && layoutBoard[i + 1][j] == 'X';
        boolean left = j > 0 && layoutBoard[i][j - 1] == 'X';
        boolean right = j < 19 && layoutBoard[i][j + 1] == 'X';

        int openDirections = 0;
        if (up) {
            openDirections++;
        }
        if (down) {
            openDirections++;
        }
        if (left) {
            openDirections++;
        }
        if (right) {
            openDirections++;
        }

        return new PathInfo(openDirections, up, down, left, right);
    }


    @Override
    public void keyPressed() {
        if (key == 'T' || key == 't') {
            isTPressed = true;
            System.out.print("T is pressed");
        }
        if (key == 'R' || key == 'r') {
            if(mana<=0){
                rest();
            }
        }


    }

    @Override
    public void keyReleased() {
        if (key == 'T' || key == 't') {
            isTPressed = false;
            System.out.print("T is not pressed");
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (isTPressed) {
            if (checkValidPlace(x, y)) {
                int boardX = x / 32;
                int boardY = (y - 40) / 32;
                int centerX = boardX * 32 + 16;
                int centerY = boardY * 32 + 56;

                layoutBoard[boardX][boardY] = 'T';
                Tower t = new Tower(this,towerrange, speed, damage, centerX, centerY, loadImage("src/main/resources/WizardTD/tower0.png"));
                //different tower image！
                towers.add(t);
                System.out.println("Tower created at position: " + x + ", " + y);
            }
        }

    }

    public boolean checkValidPlace(int pixelX, int pixelY) {
        int boardX = pixelX / 32;
        int boardY = (pixelY - 40) / 32;

        if (boardX < 0 || boardX >= layoutBoard.length || boardY < 0 || boardY >= layoutBoard[0].length) {
            return false;
        }

        char[][] layOutForTower = fixTheMap(layoutBoard);

        if (boardX < 0 || boardX >= layOutForTower.length || boardY < 0 || boardY >= layOutForTower[0].length) {
            return false;
        }

        return layOutForTower[boardX][boardY] != 'S' && layOutForTower[boardX][boardY] != 'W'
                && layOutForTower[boardX][boardY] != 'X' && layOutForTower[boardX][boardY] != 'T';
    }

    public char[][] fixTheMap(char[][] layoutBoard) {
        char[][] layoutBoardforTower = new char[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                layoutBoardforTower[i][j] = layoutBoard[i][19 - j];
            }
        }

        char[][] temp = new char[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                temp[i][j] = layoutBoardforTower[i][j];
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                layoutBoardforTower[i][j] = temp[j][19 - i];
            }
        }

        return layoutBoardforTower;
    }


    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        for (Tower t : towers) {
            t.setIsChosen(t.getX() >= (x - 16) && t.getX() <= (x + 16) && t.getY() <= (y + 16) && t.getY() >= (y - 16));
        }
    }

    @Override
    public void draw() {

        countfram += 1;
        if(countfram%60 == 0 && mana<manaCap) {
            mana += initial_mana_gained_per_second;
        }

        for (Grass g : grass) {
            g.draw(this);
        }
        for (Shrub s : shrubs) {
            s.draw(this);
        }
        for (Path p : paths) {
            p.draw(this);
        }

        for (Tower t : towers) {
            t.draw(this,countfram);
        }
        stroke(0);
        fill(255,255,255);
        this.rect(450,10,300,20);

        fill(0);
        textSize(24);
        text("Mana:",380,30);

        fill(0);
        textSize(10);
        String t =String.format("%d/%d",mana,manaCap);
        text(t,580,25);

        stroke(0,0,225);
        fill( 0 , 0 , 255);
        this.rect(450,10, (float) mana /manaCap*300,20);


        for (Wave w : waves) {
            w.draw(this, countfram);
        }
        wizardHouse.draw(this);

        if(mana<=0){
            fill(0);
            textSize(50);
            text("YOU LOSE",200,300);
            textSize(25);
            text("Press R to restart",220,350);
            pause();
            return;

        }


    }


    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((double) (newWidth - w) / 2, (double) (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }
        return result;
    }

    public void printLayoutBoard(char[][] layoutBoard) {
        for (int i = 0; i < layoutBoard.length; i++) {
            for (int j = 0; j < layoutBoard[i].length; j++) {
                System.out.print(layoutBoard[i][j] + " ");
            }
            System.out.println();  // Move to the next line after printing each row
        }
    }

    public void printPoint(Point point) {
        if (point != null) {
                System.out.println("X: " + point.getX() + ", Y: " + point.getY());
            }else{
            System.out.println("point = null");
        }

        }

    public static void printPrev(Point[][] prev) {
        if (prev != null) {
            for (int i = 0; i < prev.length; i++) {
                for (int j = 0; j < prev[i].length; j++) {
                    Point point = prev[i][j];
                    if(point != null){
                    System.out.println("prev[" + i + "][" + j + "] = (" + point.getX() + ", " + point.getY() + ")");
                }else{
                        System.out.println("prev[" + i + "][" + j + "] = null");
                    }
                }
            }
        }else{
            System.out.println("prev dose not exist");
        }
    }
    private Point transform(Point p) {
        return new Point( p.getY(), p.getX());
    }

    private void rest(){

    }

}







