package Fix;

import FileHandler.IniFile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MapPanel extends JPanel {
    int rows, cols, SIZE = 30;
    Map<String, ImageIcon> mapSprites;
    Map<String, ImageIcon> unitSprites;
    Map<String, Grid> grids;
    String[][] position;
    ArrayList<Grid> canMoveTo;


    public MapPanel(Map<String, ImageIcon> mapSprites, Map<String, ImageIcon> unitSprites, Map<String, Grid> grids, String[][] position){
        this.mapSprites = mapSprites;
        this.unitSprites = unitSprites;
        this.grids = grids;
        this.position = position;
        this.canMoveTo = new ArrayList<Grid>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // smooth graphics

        // Get the clip bounds of the graphics object
        Rectangle clipBounds = g.getClipBounds();
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                Grid grid = grids.get(position[i][j]);
                String key = position[i][j];

                if (grid.getHexagon().intersects(clipBounds)) {
                    Stroke stroke = new BasicStroke(SIZE / 20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    ImageIcon mapSprite = mapSprites.get(grid.sprite);
                    if(mapSprite != null){
                        Image image = mapSprite.getImage();
                        BufferedImage bufferedImage = (BufferedImage) image;
                        g2.setClip(grid.getHexagon());
                        Rectangle r = grid.getHexagon().getBounds();
                        g.drawImage(bufferedImage, r.x, r.y, r.width, r.height, null);
                        g2.setClip(null);
                    }
                    ImageIcon unitSprite = unitSprites.get(grid.unit.sprite);
                    if(unitSprite != null){
                        Image image = unitSprite.getImage();
                        BufferedImage bufferedImage = (BufferedImage) image;
                        g2.setClip(grid.getHexagon());
                        Rectangle r = grid.getHexagon().getBounds();
                        g.drawImage(bufferedImage, r.x, r.y, r.width, r.height, null);
                        g2.setClip(null);
                    }
                }
            }
        }
    }
}
