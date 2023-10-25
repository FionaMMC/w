package WizardTD;

import processing.data.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {
    private  App app;

    @Test
    public void testGenerateMonster() {
        App appInstance = new App();
        Wave wave = new Wave(10, 1.0, new ArrayList<>());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quantity", 2);
        jsonObject.put("type", "monsterType");
        jsonObject.put("hp", 100);
        jsonObject.put("speed", 1.5);
        jsonObject.put("armour", 10);
        jsonObject.put("mana_gain_on_kill", 5);

        ArrayList<Point> path = new ArrayList<>();
        appInstance.generateMonster(wave, jsonObject, path);

        assertEquals(1, appInstance.monsters.size());
        assertFalse(path.isEmpty(), "Path should not be empty");
    }

    @Test
    void testTowerCreation() {
        Tower tower = new Tower(app,10, 2.0, 5, 100, 100, null);
        assertNotNull(tower, "Tower should be created.");
        assertEquals(100, tower.getX(), "X-coordinate of tower is not as expected.");
        assertEquals(100, tower.getY(), "Y-coordinate of tower is not as expected.");
        assertFalse(tower.isChosen(), "Newly created tower should not be chosen by default.");
    }
    @Test
    public void testBFSPathFinder() {
        App appInstance = new App();

        // 假设的起始和结束点
        Point start = new Point(0, 0);
        Point end = new Point(6, 3);

        // 假设的布局板
        char[][] layoutBoard = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                layoutBoard[i][j] = ' '; // 初始化为空格
            }
        }
        layoutBoard[0][0] = 'X';
        layoutBoard[0][1] = 'X';
        layoutBoard[0][2] = 'X';
        layoutBoard[0][3] = 'X';
        layoutBoard[1][3] = 'X';
        layoutBoard[2][3] = 'X';
        layoutBoard[3][3] = 'X';
        layoutBoard[4][3] = 'X';
        layoutBoard[5][3] = 'X';


        ArrayList<Point> path = new ArrayList<>();
        appInstance.BFSPathFinder(path, layoutBoard, start, end);

        assertFalse(path.isEmpty(), "Path should not be empty");
        assertTrue(path.contains(start), "Path should contain the start point");
        assertTrue(path.contains(end), "Path should contain the end point");
    }

}
