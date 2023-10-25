package WizardTD;

import static org.junit.jupiter.api.Assertions.*;

import processing.core.PApplet;
public class TowerTest {
    private App app;
    void testTowerCreation() {
        Tower tower = new Tower(app,10, 2.0, 5, 100, 100, null);
        assertNotNull(tower, "Tower should be created.");
        assertEquals(100, tower.getX(), "X-coordinate of tower is not as expected.");
        assertEquals(100, tower.getY(), "Y-coordinate of tower is not as expected.");
        assertFalse(tower.isChosen(), "Newly created tower should not be chosen by default.");
    }

}

