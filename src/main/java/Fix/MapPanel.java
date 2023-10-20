package Fix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    boolean playerTurn;

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
        this.playerTurn = true;

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
                                if(target.unit.canMove){
                                    begin = target;
                                    oringin = target.unit;

                                    ArrayList<Grid> temp = move_range(keyX, keyY, oringin.mov, oringin.type);
                                    for(Grid grid : temp){
                                        if(!canMoveTo.contains(grid) && grid.unit == null){
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
                                canAttack.clear();
                                canMoveTo.clear();
                                if(turnEnd()){
                                    enemyTurn();
                                }
                                mode = "SelectUnit";
                                repaint();
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
                                    canAttack.clear();
                                    canMoveTo.clear();
                                    if(turnEnd()){
                                        enemyTurn();
                                    }
                                    mode = "SelectUnit";
                                    repaint();
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
                            canMoveTo.clear();
                            canAttack.clear();
                            if(turnEnd()){
                                enemyTurn();
                            }
                            mode = "SelectUnit";
                            repaint();
                            return;
                        }
                        if(canAttack.contains(target)){
                            oringin.attack(target.unit);
                            if(target.unit.hp <= 0){
                                enemyUnit.remove(target.unit);
                                target.unit = null;
                                if(enemyUnit.isEmpty()){
                                    JFrame frame = new JFrame();
                                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                    JLabel label = new JLabel("You win");
                                    frame.add(label);
                                    frame.setSize(new Dimension(200, 100));
                                    frame.setLocationRelativeTo(null);
                                    frame.setVisible(true);
                                }
                            }
                            canAttack.clear();
                            canMoveTo.clear();
                            if(turnEnd()){
                                enemyTurn();
                            }
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

    public boolean turnEnd(){
        boolean output = true;
        System.out.println(playerUnit);
        for(Unit unit : playerUnit){
            if(unit.canMove){
                output = false;
                break;
            }
        }
        System.out.println("Player turn end: " + output);
        return output;
    }

    public void enemyTurn(){
        playerTurn = false;
        System.out.println("EnemyTurn");
        for(Unit unit : playerUnit){
            unit.canMove = true;
        }
        for(Unit unit : enemyUnit){
            unit.canMove = true;
        }
        repaint();
        ArrayList<Grid> units = new ArrayList<>();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                Grid grid = grids.get(position[i][j]);
                if(grid.unit != null && enemyUnit.contains(grid.unit))    units.add(grid);
            }
        }
        for(Grid grid : units){
            ArrayList<Grid> temp = move_range(grid.gx, grid.gy, grid.unit.mov, grid.unit.type);
            for(Grid move : temp){
                if(!canMoveTo.contains(move) && move.unit == null)   canMoveTo.add(move);
            }
            repaint();
            Random random = new Random();
            Grid moveTo = grid;
            if(!canMoveTo.isEmpty()) {
                int key = 0;
                if(canMoveTo.size() > 1){
                    key = random.nextInt(canMoveTo.size() - 1);
                }
                moveTo = canMoveTo.get(key);
                if(grid != moveTo) {
                    moveTo.unit = grid.unit;
                    grid.unit = null;
                }
                temp = attack_range(moveTo.gx, moveTo.gy, moveTo.unit.range, moveTo.unit.type);
                canMoveTo.clear();
                repaint();
            }
            for(Grid target : temp){
                if(!canAttack.contains(target)) canAttack.add(target);
            }
            repaint();
            if(!canAttack.isEmpty()) {
                System.out.println(canAttack);
                System.out.println(canAttack.size());
                int key = 0;
                if(canAttack.size() > 1) {
                    key = random.nextInt(canAttack.size() - 1);
                }
                Grid target = canAttack.get(key);
                moveTo.unit.attack(target.unit);
                moveTo.unit.canMove = false;
                if (target.unit.hp <= 0) {
                    playerUnit.remove(target.unit);
                    target.unit = null;
                }
                canAttack.clear();
                repaint();
            }
            if(playerUnit.isEmpty()){
                JFrame frame = new JFrame();
                JLabel label = new JLabel("You lose");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLayout(new FlowLayout());
                frame.add(label);
                frame.setSize(new Dimension(200, 100));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                playerTurn = true;
                return;
            }
        }
        for(Unit unit : playerUnit){
            unit.canMove = true;
        }
        for(Unit unit : enemyUnit){
            unit.canMove = true;
        }
        playerTurn = true;
        repaint();
    }


    public ArrayList<Grid> move_range(int x, int y, int mov, int type){
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
                if((grid.unit == null && grid.canPassed) || (grid.unit != null && grid.unit.type == type)){
                    temp.add(grid);
                    ArrayList<Grid> next = move_range(tx, ty, mov - 1, type);
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
                temp.addAll(next);
            }
        }

        return temp;
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

        if(!playerTurn){
            try {
                Thread. sleep(500);
            }catch (InterruptedException e){
                System.out.println(e);
            }

        }
    }
}
