package Fix;

import javax.swing.*;
import java.awt.*;

public class Grid {
    double x, y;
    Unit unit;
    ImageIcon sprite;
    Boolean canPassed;
    Polygon hexagon;
    Integer size = 30;

    public Grid(double x, double y, Boolean canPassed, ImageIcon sprite, Unit unit){
        this.x = x;
        this.y = y;
        this.canPassed = canPassed;
        this.sprite = sprite;
        this.unit = unit;
        hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_deg = 60 * i - 30;
            double angle_rad = Math.PI / 180 * angle_deg;
            int _x = (int) (x + size * Math.cos(angle_rad));
            int _y = (int) (y + size * Math.sin(angle_rad));
            hexagon.addPoint(_x, _y);
        }
    }



    public Polygon getHexagon() {
        return hexagon;
    }
}
