package Hexagon;

import java.awt.*;

public class HexGrid{
    private Polygon hexagon;
    Integer size;

    public HexGrid(double x, double y, int size){
        super();
        this.size = size;
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

    public void move(int dx, int dy) {
        int[] newXPoints = new int[6];
        int[] newYPoints = new int[6];

        for (int i = 0; i < 6; i++) {
            newXPoints[i] = hexagon.xpoints[i] + dx;
            newYPoints[i] = hexagon.ypoints[i] + dy;
        }

        hexagon = new Polygon(newXPoints, newYPoints, 6);
    }

    public float getX(){
        return hexagon.getBounds().x + (size / 2);
    }
    public  float getY(){
        return hexagon.getBounds().y + (size / 2);
    }
}
