package Fix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

public class MapPanel extends JPanel {
    int rows, cols, SIZE = 30;
    Map<String, ImageIcon> mapSprites;
    Map<String, ImageIcon> unitSprites;
    Map<String, Grid> grids;
    String[][] position;
    ArrayList<Grid> canMoveTo, canAttack;
    String mode = "SelectUnit";
    Grid begin, destination;
    Unit oringin, victim;
    ArrayList<Unit> playerUnit, enemyUnit;

    public ArrayList<Grid> move_range(int x, int y, int mov){
        System.out.println(x + ", " + y);
        ArrayList<Grid> temp = new ArrayList<Grid>();
        if(mov <= 0)    return temp;
        int[] dx, dy;
        if(y % 2 == 0){
            dx = new int[]{-1, 0, 1, 0, -1, -1};
            dy = new int[]{-1, -1, 0, 1, 1, 0};
        }else{
            dx = new int[]{0, 1, 1, 1, 0, -1};
            dy = new int[]{-1, -1, 0, 1, 1, 0};
        }

        for(int i = 0; i < dx.length; i++){
            int tx = x + dx[i];
            int ty = y + dy[i];
            String key = Integer.toString(tx) + "," + Integer.toString(ty);
            if(grids.containsKey(key)){
                Grid grid = grids.get(key);
                if(grid.unit == null && grid.canPassed) {
                    temp.add(grid);
                    ArrayList<Grid> next = move_range(tx, ty, mov - 1);
                    System.out.println(next);
                    temp.addAll(next);
                }
            }
        }

        return temp;
    }

    public ArrayList<Grid> seach_range(int x, int y, int range){
        ArrayList<Grid> temp = new ArrayList<Grid>();
        if(range <= 0)    return temp;
        int[] dx, dy;
        if(y % 2 == 0){
            dx = new int[]{-1, 0, 1, 0, -1, -1};
            dy = new int[]{-1, -1, 0, 1, 1, 0};
        }else{
            dx = new int[]{0, 1, 1, 1, 0, -1};
            dy = new int[]{-1, -1, 0, 1, 1, 0};
        }

        for(int i = 0; i < dx.length; i++){
            int tx = x + dx[i];
            int ty = y + dy[i];
            String key = Integer.toString(tx) + "," + Integer.toString(ty);
            if(grids.containsKey(key)){
                Grid grid = grids.get(key);
                temp.add(grid);
                ArrayList<Grid> next = seach_range(tx, ty, range - 1);
                System.out.println(next);
                temp.addAll(next);
            }
        }

        return temp;
    }

    public ArrayList<Grid> attack_range(int x, int y, int range, int type){
        ArrayList<Grid> temp = new ArrayList<Grid>();
        if(range <= 0)    return temp;
        int[] dx, dy;
        if(y % 2 == 0){
            dx = new int[]{-1, 0, 1, 0, -1, -1};
            dy = new int[]{-1, -1, 0, 1, 1, 0};
        }else{
            dx = new int[]{0, 1, 1, 1, 0, -1};
            dy = new int[]{-1, -1, 0, 1, 1, 0};
        }

        for(int i = 0; i < dx.length; i++){
            int tx = x + dx[i];
            int ty = y + dy[i];
            String key = Integer.toString(tx) + "," + Integer.toString(ty);
            if(grids.containsKey(key)){
                Grid grid = grids.get(key);
                if(grid.unit != null && grid.unit.type != type) {
                    temp.add(grid);
                }
                ArrayList<Grid> next = attack_range(tx, ty, range - 1, type);
                System.out.println(next);
                temp.addAll(next);
            }
        }

        return temp;
    }
    public MapPanel(Map<String, ImageIcon> mapSprites, Map<String, ImageIcon> unitSprites, Map<String, Grid> grids, String[][] position){
        super();
        this.mapSprites = mapSprites;
        this.unitSprites = unitSprites;
        this.grids = grids;
        this.position = position;
        this.cols = position[0].length;
        this.rows = position.length;
        this.canMoveTo = new ArrayList<Grid>();
        this.canAttack = new ArrayList<>();
        this.playerUnit = new ArrayList<>();
        this.enemyUnit = new ArrayList<>();

        for(int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                Grid grid = grids.get(position[i][j]);
                if(grid.unit != null){
                    if(grid.unit.type == 0){
                        playerUnit.add(grid.unit);
                    }else{
                        enemyUnit.add((grid.unit));
                    }
                }
            }
        }

        setBackground(Color.WHITE);
        setDoubleBuffered(true);

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Integer keyX = null;
                Integer keyY = null;
                for(Integer i = 0; i < rows; i++){
                    for(Integer j = 0; j < cols; j++){
                        if (grids.get(position[i][j]).getHexagon().contains(e.getPoint())){
                            keyY = i;
                            keyX = j;
                        }
                    }
                }
                String key = Integer.toString(keyX) + "," + Integer.toString(keyY);
                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(mode.equals("SelectUnit")){
                        System.out.println(mode);
                        Grid target = grids.get(key);
                        if(target.unit != null){
                            if(target.unit.type == 0){
                                System.out.println(target.unit.canMove);
                                if(target.unit.canMove){
                                    begin = target;
                                    oringin = target.unit;

                                    ArrayList<Grid> temp = move_range(keyX, keyY, oringin.mov);
                                    for(Grid grid : temp){
                                        if(!canMoveTo.contains(grid)){
                                            canMoveTo.add(grid);
                                        }
                                    }

                                    mode = "SelectDestination";
                                    repaint();
                                    return;
                                }
                            }
                        }
                    }
                    if(mode.equals("SelectDestination")){
                        System.out.println(mode);
                        Grid target = grids.get(key);
                        if(canMoveTo.contains(target)){
                            target.unit = oringin;
                            begin.unit = null;
                            target.unit.canMove = false;
                            canMoveTo.clear();

                            ArrayList<Grid> temp = attack_range(keyX, keyY, oringin.range, oringin.type);
                            for(Grid grid : temp){
                                if(!canAttack.contains(grid)){
                                    canAttack.add(grid);
                                }
                            }
                            temp = seach_range(keyX, keyY, oringin.range);
                            for(Grid grid : temp){
                                if(!canMoveTo.contains(grid)){
                                    canMoveTo.add(grid);
                                }
                            }

                            if(canAttack.isEmpty()){
                                mode = "SelectUnit";
                                canAttack.clear();
                                canMoveTo.clear();
                            }else{
                                mode = "SelectTarget";
                            }
                            repaint();
                            return;
                        }
                        else{
                            if(target.unit == oringin){
                                target.unit.canMove = false;
                                canMoveTo.clear();

                                ArrayList<Grid> temp = attack_range(keyX, keyY, oringin.range, oringin.type);
                                for(Grid grid : temp){
                                    if(!canAttack.contains(grid)){
                                        canAttack.add(grid);
                                    }
                                }
                                temp = seach_range(keyX, keyY, oringin.range);
                                for(Grid grid : temp){
                                    if(!canMoveTo.contains(grid)){
                                        canMoveTo.add(grid);
                                    }
                                }
                                if(canAttack.isEmpty()){
                                    mode = "SelectUnit";
                                    canAttack.clear();
                                    canMoveTo.clear();
                                }else {
                                    mode = "SelectTarget";
                                }
                                repaint();
                                return;
                            }
                        }
                    }
                    if(mode.equals("SelectTarget")){
                        System.out.println(mode);

                        Grid target = grids.get(key);
                        if(canAttack.isEmpty() || target.unit == oringin){
                            mode = "SelectUnit";
                            canMoveTo.clear();
                            canAttack.clear();
                            repaint();
                            return;
                        }
                        if(canAttack.contains(target)){
                            oringin.attack(target.unit);
                            if(target.unit.hp <= 0){
                                System.out.println(enemyUnit);
                                enemyUnit.remove(target.unit);
                                target.unit = null;
                                System.out.println(enemyUnit);
                            }
                            canAttack.clear();
                            canMoveTo.clear();
                            repaint();
                            mode = "SelectUnit";
                            return;
                        }



                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Draw");
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

                    ImageIcon mapSprite = grid.sprite;
                    if(mapSprite != null){
                        Image image = mapSprite.getImage();
                        BufferedImage bufferedImage = (BufferedImage) image;
                        g2.setClip(grid.getHexagon());
                        Rectangle r = grid.getHexagon().getBounds();
                        g.drawImage(bufferedImage, r.x, r.y, r.width, r.height, null);
                        g2.setClip(null);
                    }


                    if(grid.unit != null){
                        ImageIcon unitSprite = grid.unit.sprite;
                        if(unitSprite != null) {
                            Image image = unitSprite.getImage();
                            BufferedImage bufferedImage = (BufferedImage) image;
                            g2.setClip(grid.getHexagon());
                            Rectangle r = grid.getHexagon().getBounds();

                            g.drawImage(bufferedImage, r.x, r.y, r.width, r.height, null);
                            if(!grid.unit.canMove){
                                g2.setColor(Color.BLACK);
                                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                                g2.fill(grid.getHexagon());
                                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
                            }
                            g2.setClip(null);

                        }
                    }

                    if(canMoveTo.contains(grid) && !canAttack.contains(grid)){
                        g2.setColor(Color.CYAN);
                        g2.setClip(grid.getHexagon());
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                        g2.fill(grid.getHexagon());
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
                        g2.setClip(null);
                    }

                    if(canAttack.contains(grid)){
                        g2.setColor(Color.RED);
                        g2.setClip(grid.getHexagon());
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                        g2.fill(grid.getHexagon());
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
                        g2.setClip(null);
                    }

                    g2.setStroke(stroke);
                    g2.setColor(Color.BLACK);
                    g2.draw(grid.getHexagon());
                }
            }
        }
    }
}
