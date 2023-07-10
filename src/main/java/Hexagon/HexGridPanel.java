package Hexagon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import FileHandler.IniFile;
import MainWindow.MainWindow;

import static MainWindow.MainWindow.*;

public class HexGridPanel extends JPanel {
    private int ROWS = 0, COLS = 0, SIZE = 30;
    private HexGrid[][] grids;
    int lastMouseX = 0, lastMouseY = 0;
    String filePath = "";
    String filename = "test";
    String type = "";

    public HexGridPanel() throws IOException {
        super();
        initialize();
        setDoubleBuffered(true);
    }

    public HexGridPanel(int cols, int rows){
        super();
        ROWS = rows;
        COLS = cols;
        initialize();
        setDoubleBuffered(true);
    }

    public HexGridPanel(int cols, int rows, String name, String type){
        super();
        ROWS = rows;
        COLS = cols;
        filename = name;
        this.type = type;
        initialize();
        setDoubleBuffered(true);
    }

    public void initialize(){
        filePath = DIRECTORY + File.separator + "Maps" + File.separator + filename;
        try{
            IniFile temp = new IniFile(filePath);
            temp.setProperty("info", "cols", String.valueOf(COLS));
            temp.setProperty("info", "rows", String.valueOf(ROWS));
            temp.setProperty("info", "name", filename);
            temp.save();
        }catch (IOException e){
            System.out.println(e);
        }
        grids = new HexGrid[ROWS][COLS];
        setBackground(Color.WHITE);
        double height = SIZE * 2;
        double width = SIZE * Math.sqrt(3);
        double x = width, y = 1.5 * SIZE;
        for (int i = 0; i < ROWS; i++) {
            x = width;
            if (i % 2 == 1) {
                x += width / 2;
            }
            for (int j = 0; j < COLS; j++) {
                grids[i][j] = new HexGrid(x, y, SIZE);
                x += width;
            }
            y += 1.5 * SIZE;
        }

        try {
            IniFile temp = new IniFile(filePath);
            if (type == "Terrain" && !Boolean.parseBoolean(temp.getProperty("Terrain", "initialized"))){
                for (Integer i = 0; i < grids.length; i++) {
                    for (Integer j = 0; j < grids[i].length; j++) {
                        String key = Integer.toString(j) + "," + Integer.toString(i);
                        temp.setProperty(type, key, String.valueOf(true));
                    }
                }
                temp.setProperty("Terrain", "initualized", String.valueOf(true));
                temp.save();
            }
        }catch (IOException e){
            System.out.println(e);
        }

        this.addMouseListener(new MyMouseListener());
        this.addMouseMotionListener(new MyMouseMotionListener());
        this.setPreferredSize(new Dimension((int)(width * (COLS + 2)), (int)(ROWS * height * 0.75) + 2 * SIZE));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // smooth graphics

        // Get the clip bounds of the graphics object
        Rectangle clipBounds = g.getClipBounds();

        for(Integer i = 0; i < grids.length; i++){
            for(Integer j = 0; j < grids[i].length; j++){
                // Check if the grid's hexagon intersects with the clip bounds
                HexGrid grid = grids[i][j];
                String key = Integer.toString(j) + "," + Integer.toString(i);
                if (grid.getHexagon().intersects(clipBounds)) {
                    Stroke stroke = new BasicStroke(SIZE / 20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    if (type == "Overview" || type == "Event") {
                        try{
                            IniFile temp = new IniFile(filePath);
                            if(temp.getProperty("Event", key).length() > 0){
                                g2.setColor(Color.YELLOW);
                                g2.fill(grid.getHexagon());
                            }
                        }catch (IOException e){
                            System.out.println(e);
                        }
                    }
                    if (type == "Overview" || type == "Terrain") {
                        try {
                            IniFile temp = new IniFile(filePath);
                            Boolean canPassed = Boolean.valueOf(temp.getProperty("Terrain", key));
                            if (canPassed)  g2.setColor(Color.GREEN);
                            else g2.setColor(Color.RED);
                            g2.fill(grid.getHexagon());
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                    if (type == "Overview" || type == "Sprite") {
                        try {
                            IniFile temp = new IniFile(filePath);
                            ImageIcon imageIcon = spriteMap.get(temp.getProperty("Sprite", key));
                            if(imageIcon != null){
                                Image image = imageIcon.getImage();
                                BufferedImage bufferedImage = (BufferedImage) image;
                                g2.setClip(grid.getHexagon());
                                Rectangle r = grid.getHexagon().getBounds();
                                g.drawImage(bufferedImage, r.x, r.y, r.width, r.height, null);
                                g2.setClip(null);
                            }
                        }catch (IOException e){
                            System.out.println(e);
                        }
                    }

                    g2.setStroke(stroke);
                    g2.setColor(Color.BLACK);
                    g2.draw(grid.getHexagon());
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Test HexGridPanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new HexGridPanel());
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class MyMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            Integer keyX = null;
            Integer keyY = null;
            for(Integer i = 0; i < grids.length; i++){
                for(Integer j = 0; j < grids[i].length; j++){
                    if (grids[i][j].getHexagon().contains(e.getPoint())){
                        keyY = i;
                        keyX = j;
                    }
                }
            }
            String key = Integer.toString(keyX) + "," + Integer.toString(keyY);
            if(mode == 2 && e.getClickCount() == 2){
                try{
                    IniFile temp = new IniFile(filePath);
                    String old = temp.getProperty("Event", key);
                    JTextArea event = new JTextArea(40, 100);
                    event.setText(old);
                    Object[] message = {
                            "event:", event
                    };
                    int option = JOptionPane.showConfirmDialog(getParent(), message, "Event " + key, JOptionPane.OK_CANCEL_OPTION);
                    if(option == JOptionPane.OK_OPTION){
                        temp.setProperty("Event", key, event.getText());
                        temp.save();
                        repaint();
                    }
                }catch (IOException ex){
                    System.out.println(ex);
                }
            }
            if(mode == 3){
                if (keyX != null && keyY != null){
                    try{
                        IniFile temp = new IniFile(filePath);
                        temp.setProperty("Sprite", key, String.valueOf(spriteSelected));
                        temp.save();
                        repaint();
                    }catch (IOException ex){
                        System.out.println(ex);
                    }
                }
            }
            if(mode == 4){
                if (keyX != null && keyY != null){
                    try {
                        IniFile temp = new IniFile(filePath);
                        Boolean last = Boolean.valueOf(temp.getProperty("Terrain", key));
                        temp.setProperty("Terrain", key, String.valueOf(!last));
                        temp.save();
                        repaint();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON2){
                lastMouseX = e.getXOnScreen();
                lastMouseY = e.getYOnScreen();
            }
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
    }

    private class MyMouseMotionListener implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
            if(SwingUtilities.isMiddleMouseButton(e)){
                int dx = e.getXOnScreen() - lastMouseX;
                int dy = e.getYOnScreen() - lastMouseY;
                lastMouseX = e.getXOnScreen();
                lastMouseY = e.getYOnScreen();

                JViewport viewport = (JViewport) getParent();
                JScrollPane scrollPane = (JScrollPane) viewport.getParent();
                JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                horizontalScrollBar.setValue(horizontalScrollBar.getValue() - dx);
                verticalScrollBar.setValue(verticalScrollBar.getValue() - dy);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }
}