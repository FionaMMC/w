package WizardTD;
public class PathInfo {
    public final int openDirections;
    public final boolean up;
    public final boolean down;
    public final boolean left;
    public final boolean right;

    public PathInfo(int openDirections, boolean up, boolean down, boolean left, boolean right) {
        this.openDirections = openDirections;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }
}