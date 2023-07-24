package MainWindow;

import CellRenderer.MultiLineCellRenderer;
import FileHandler.IniFile;
import Hexagon.HexGridPanel;
import Test.drawPolygonDemo;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame implements ActionListener {
    public static String DIRECTORY = "C:\\Project HSRPG\\7-project\\DEMO";
    public static String PROJECT_NAME = "Demo";
    //Menu related
    JMenuBar mainMenuBar;
    JMenu projectMenu, mapMenu;
    //projectMenuItem
    JMenuItem newProjectMenuItem, openProjectMenuItem;
    //mapMenuItem
    JMenuItem newMapMenuItem;

    JTabbedPane tabbedPane;
    JButton newMapButton, deleteMapButton;
    JButton newSpriteButton, deleteSpriteButton;
    JButton newUnitButton, deleteUnitButton;
    JList<String> mapList;
    public static Map<String, ImageIcon> spriteMap;
    public static Map<String, ImageIcon> unitMap;
    JList<String> spriteList;
    JList<String> unitList;
    DefaultListModel<String> listModel;
    DefaultListModel<String> spriteListModel;
    DefaultListModel<String> unitModel;
    Map mapMap;
    public static String[] scrollPaneName = {"Overview", "Unit", "Event", "Sprite", "Terrain"};
    public  static String[] subDirectories = {"Maps", "Units", "Items", "Skills", "Sprites", "Events"};
    public static Integer mode = 0;
    String[] spriteNames;
    public static String spriteSelected;
    static String mapSelected;
    static String unitSelected;

    public MainWindow(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Hexagon SRPG Studio");
        this.setPreferredSize(new Dimension(1920, 1080));
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));

        mainMenuBar = new JMenuBar();

        projectMenu = new JMenu("Project");

        newProjectMenuItem = new JMenuItem("New Project");
        newProjectMenuItem.addActionListener(this);
        projectMenu.add(newProjectMenuItem);

        openProjectMenuItem = new JMenuItem("Open Project");
        openProjectMenuItem.addActionListener(this);
        projectMenu.add(openProjectMenuItem);

        mapMenu = new JMenu("Map");

        newMapMenuItem = new JMenuItem("New Map");
        newMapMenuItem.addActionListener(this);
        mapMenu.add(newMapMenuItem);

        mainMenuBar.add(projectMenu);

        this.setJMenuBar(mainMenuBar);

        JPanel leftPanel = new JPanel();
        leftPanel.setMaximumSize(new Dimension(300, 2000));
        leftPanel.setLayout(new GridLayout(2, 1));

        JPanel spritePanel = new JPanel(new BorderLayout());
        newSpriteButton = new JButton("New Sprite");
        deleteSpriteButton = new JButton("Delete Sprite");
        JPanel spriteButtonPanel = new JPanel(new FlowLayout());
        spriteButtonPanel.add(newSpriteButton);
        spriteButtonPanel.add(deleteSpriteButton);

        spritePanel.add(spriteButtonPanel, BorderLayout.NORTH);
        spriteMap = new HashMap<>();
        spriteListModel = new DefaultListModel<>();
        spriteList = new JList<>(spriteListModel);
        spriteList.setCellRenderer(new SpriteListRenderer());

        JScrollPane spriteScrollPane = new JScrollPane(spriteList);
        spritePanel.add(spriteScrollPane, BorderLayout.CENTER);
        leftPanel.add(spritePanel);

        JPanel mapListPanel = new JPanel();
        mapListPanel.setLayout(new BorderLayout());

        newMapButton = new JButton("New Map");
        deleteMapButton = new JButton("Delete Map");


        mapMap = new HashMap();
        listModel = new DefaultListModel<>();
        mapList = new JList<>(listModel);
        mapList.setCellRenderer(new MultiLineCellRenderer());
        mapList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    mapSelected = mapList.getSelectedValue();
                    System.out.println("Selected item: " + mapSelected);
                    /*
                    Click here
                    change the map in tabbedPane based on the name of the map
                     */
                    HexGridPanel[] tempMap = (HexGridPanel[]) mapMap.get(mapSelected);
                    if(tempMap != null) {
                        for (int i = 0; i < 5; i++) {
                            JScrollPane temp = (JScrollPane) tabbedPane.getComponentAt(i);
                            temp.setViewportView(tempMap[i]);
                        }
                    }
                }
            }
        });
        spriteList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    spriteSelected = spriteList.getSelectedValue();
                }
            }
        });
        JScrollPane mapScrollPane = new JScrollPane(mapList);

        JPanel mapButtonPanel = new JPanel(new FlowLayout());
        mapButtonPanel.add(newMapButton);
        mapButtonPanel.add(deleteMapButton);
        mapListPanel.add(mapButtonPanel, BorderLayout.NORTH);
        mapListPanel.add(mapScrollPane, BorderLayout.CENTER);

        JScrollPane leftScrollPane2  = new JScrollPane(mapListPanel);
        leftPanel.add(leftScrollPane2);

        tabbedPane = new JTabbedPane();
        int n = 5;
        JScrollPane[] scrollPane = new JScrollPane[n];
        JScrollBar[] scrollBarX = new JScrollBar[n];
        JScrollBar[] scrollBarY = new JScrollBar[n];

        for(int i = 0; i < n; i++){
            scrollPane[i] = new JScrollPane();
            scrollBarX[i] = scrollPane[i].getHorizontalScrollBar();
            scrollBarY[i] = scrollPane[i].getVerticalScrollBar();
            final int index = i;
            scrollBarX[i].addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    JScrollBar source = (JScrollBar) e.getSource();
                    for(int j = 0; j < n; j++){
                        if(j != index){
                            scrollBarX[j].setValue(source.getValue());
                        }
                    }
                }
            });
            scrollBarY[i].addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    JScrollBar source = (JScrollBar) e.getSource();
                    for(int j = 0; j < n; j++){
                        if(j != index){
                            scrollBarY[j].setValue(source.getValue());
                        }
                    }
                }
            });
        }

        tabbedPane.addTab("Overview", scrollPane[0]);
        tabbedPane.addTab("Unit", scrollPane[1]);
        tabbedPane.addTab("Event", scrollPane[2]);
        tabbedPane.addTab("Sprite", scrollPane[3]);
        tabbedPane.addTab("Terrain", scrollPane[4]);

        JPanel rightPanel = new JPanel(new GridLayout(1, 1));
        newUnitButton = new JButton("New Unit");
        newUnitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField name = new JTextField();
                JTextField hp = new JTextField();
                JTextField atk = new JTextField();
                JTextField range = new JTextField();
                JTextField speed = new JTextField();
                JButton selectImage = new JButton("Select Image");
                JLabel imagePath = new JLabel();
                JPanel messagePanel = new JPanel(new GridLayout(6, 2));
                messagePanel.add(imagePath);
                messagePanel.add(selectImage);
                messagePanel.add(new JLabel("Name:"));
                messagePanel.add(name);
                messagePanel.add(new JLabel("HP"));
                messagePanel.add(hp);
                messagePanel.add(new JLabel("ATK"));
                messagePanel.add(atk);
                messagePanel.add(new JLabel("Range"));
                messagePanel.add(range);
                messagePanel.add(new JLabel("Speed"));
                messagePanel.add(speed);
                messagePanel.setPreferredSize(new Dimension(500, 300));
                JScrollPane scrollMessage = new JScrollPane(messagePanel);
                final String[] unitSprite = {""};

                selectImage.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File source = new File(fileChooser.getSelectedFile().getAbsolutePath());
                            File destination = new File(DIRECTORY + File.separator + "Units" + File.separator + source.getName());
                            unitSprite[0] = source.getName();
                            try {
                                InputStream in = new FileInputStream(source);
                                OutputStream out = new FileOutputStream(destination);

                                // Copy the bits from instream to outstream
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                                in.close();
                                out.close();
                            } catch (FileNotFoundException ex) {
                                throw new RuntimeException(ex);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            imagePath.setIcon(new ImageIcon(new drawPolygonDemo(source).getTexture()));
                        }
                    }
                });

                Object[] message = {
                        imagePath, selectImage,
                        "Name: ", name,
                        "hp: ", hp,
                        "atk: ", atk,
                        "range: ", range,
                        "speed: ", speed
                };
                int option = JOptionPane.showConfirmDialog(getParent(), scrollMessage, "New Unit", JOptionPane.OK_CANCEL_OPTION);
                if(option == JOptionPane.OK_OPTION){
                    try{
                        IniFile file = new IniFile(DIRECTORY + File.separator + "Units" + File.separator + "UnitSprites" + File.separator + name.getText());
                        String section = "main";
                        file.setProperty(section, "name", name.getText());
                        file.setProperty(section, "hp", hp.getText());
                        file.setProperty(section, "atk", atk.getText());
                        file.setProperty(section, "range", range.getText());
                        file.setProperty(section, "speed", speed.getText());
                        file.setProperty(section, "sprite", unitSprite[0]);
                        file.save();

                        unitMap.put(name.getText(), new ImageIcon(new drawPolygonDemo(new File(DIRECTORY + File.separator + "Units" + File.separator + unitSprite[0])).getTexture()));
                        unitModel.addElement(name.getText());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        deleteUnitButton = new JButton("Delete Unit");
        JPanel unitButtonPanel = new JPanel(new FlowLayout());
        unitButtonPanel.add(newUnitButton);
        unitButtonPanel.add(deleteUnitButton);

        JPanel unitPanel = new JPanel(new BorderLayout());
        unitPanel.add(unitButtonPanel, BorderLayout.NORTH);
        unitMap = new HashMap<>();
        unitModel = new DefaultListModel<>();
        unitList = new JList<>(unitModel);
        unitList.setCellRenderer(new UnitListRenderer());
        JScrollPane unitScrollPane = new JScrollPane(unitList);
        unitPanel.add(unitScrollPane, BorderLayout.CENTER);
        rightPanel.add(unitPanel);
        rightPanel.setMaximumSize(new Dimension(300, 2000));

        this.add(leftPanel, BorderLayout.WEST);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(rightPanel, BorderLayout.EAST);
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mode = tabbedPane.getSelectedIndex();
                System.out.println(mode);
            }
        });

        startProject();
        this.setVisible(true);
    }

    void startProject(){
        File directory = new File(DIRECTORY);
        if(!directory.exists()) {
            directory.mkdirs();
        }
        File tempFile;
        for(String subDir : subDirectories){
            tempFile = new File(directory.getAbsolutePath() + File.separator + subDir);
            if(!tempFile.exists()){
                tempFile.mkdirs();
            }
        }
        tempFile = new File(directory.getAbsolutePath() + File.separator + "Units" + File.separator + "UnitSprites");
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
        File[] maps = new File(DIRECTORY + File.separator + "Maps").listFiles();
        try {
            for(File map : maps) {
                IniFile file = new IniFile(map.getAbsolutePath());
                HexGridPanel[] temp = new HexGridPanel[5];
                int cols = Integer.parseInt(file.getProperty("info", "cols"));
                int rows = Integer.parseInt(file.getProperty("info", "rows"));
                String filename = file.getProperty("info", "name");
                for(int i = 0; i < 5; i++){
                    temp[i] = new HexGridPanel(cols, rows,
                            filename, scrollPaneName[i]);
                }
                mapMap.put(filename, temp);
                listModel.addElement(filename);
            }
        }catch (IOException e){
            System.out.println(e);
        }

        File[] sprites = new File(DIRECTORY + File.separator + "Sprites").listFiles();
        spriteNames = new File(DIRECTORY + File.separator + "Sprites").list();
        for(File sprite : sprites){
            spriteMap.put(sprite.getName(), new ImageIcon(new drawPolygonDemo(sprite).getTexture()));
            spriteListModel.addElement(sprite.getName());
        }

        File[] units = new File(DIRECTORY + File.separator + "Units").listFiles();
        for(File unit : units){
            try{
                IniFile file = new IniFile(unit.getAbsolutePath());
                unitMap.put(file.getProperty("main", "name"), new ImageIcon(new drawPolygonDemo(new File(DIRECTORY + File.separator + "Units" + File.separator + "UnitSprites" + File.separator + file.getProperty("main", "sprite"))).getTexture()));
                unitModel.addElement(file.getProperty("main", "name"));
            }catch (IOException e){
                System.out.println(e);
            }
        }

        newMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
                JTextField name = new JTextField();
                JTextField rows = new JTextField();
                JTextField cols = new JTextField();

                Object[] message = {
                        "Name:", name,
                        "Rows:", rows,
                        "Cols:", cols
                };

                int option = JOptionPane.showConfirmDialog(getParent(), message, "New Map", JOptionPane.OK_CANCEL_OPTION);
                if(option == JOptionPane.OK_OPTION){
                    HexGridPanel[] temp = new HexGridPanel[5];
                    for(int i = 0; i < 5; i++){
                        temp[i] = new HexGridPanel(Integer.parseInt(cols.getText()), Integer.parseInt(rows.getText()), name.getText(), scrollPaneName[i]);
                    }
                    mapMap.put(name.getText(), temp);
                    listModel.addElement(name.getText());
                }
            }
        });

        newSpriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    File source = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    File destination = new File(DIRECTORY + File.separator + "Sprites" + File.separator + source.getName());

                    try {
                        InputStream in = new FileInputStream(source);
                        OutputStream out = new FileOutputStream(destination);

                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        in.close();
                        out.close();
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    spriteMap.put(destination.getName(), new ImageIcon(new drawPolygonDemo(destination).getTexture()));
                    spriteListModel.addElement(destination.getName());
                }
            }
        });

        deleteSpriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spriteSelected = spriteList.getSelectedValue();
                if(JOptionPane.showConfirmDialog(getParent(), "Delete " + spriteSelected + "?", "Comfirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
                    File file = new File(DIRECTORY + File.separator + "Sprites" + File.separator + spriteSelected);
                    spriteListModel.removeElement(spriteSelected);
                    file.delete();
                    spriteList.updateUI();

                }

            }
        });

        deleteMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapSelected = mapList.getSelectedValue();
                if(JOptionPane.showConfirmDialog(getParent(), "Delete " + mapSelected + "?", "Comfirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
                    File file = new File(DIRECTORY + File.separator + "Maps" + File.separator + mapSelected);
                    listModel.removeElement(mapSelected);
                    file.delete();
                }
            }
        });

        mainMenuBar.updateUI();
    }

    public class SpriteListRenderer extends DefaultListCellRenderer {
        Font font = new Font("helvitica", Font.BOLD, 24);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(spriteMap.get((String) value));
            label.setHorizontalTextPosition(JLabel.RIGHT);
            label.setFont(font);
            return label;
        }
    }

    public class UnitListRenderer extends DefaultListCellRenderer {
        Font font = new Font("helvitica", Font.BOLD, 24);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(unitMap.get((String) value));
            label.setHorizontalTextPosition(JLabel.RIGHT);
            label.setFont(font);
            return label;
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newProjectMenuItem){
            //Ask for the directory and project name from the user
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Select the project directory");

            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                //Get directory
                String directory = fileChooser.getSelectedFile().getAbsolutePath();
                //Get project name
                String project_name = JOptionPane.showInputDialog("Project name");
                directory += File.separator + project_name;
                File projectFolder = new File(directory);
                if(projectFolder.exists()){
                    JOptionPane.showMessageDialog(this, "There was already the folder with the name in the directory");
                }else{
                    projectFolder.mkdir();
                    DIRECTORY = directory;
                    PROJECT_NAME = project_name;

                    //startProject();
                }
            }
        }

        if(e.getSource() == openProjectMenuItem){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Select the project directory");

            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                //Get directory
                String directory = fileChooser.getSelectedFile().getAbsolutePath();
                PROJECT_NAME = directory.substring(directory.lastIndexOf(File.separator) + 1, directory.length());
                DIRECTORY = directory;

                System.out.println(DIRECTORY);
                System.out.println(PROJECT_NAME);

                //startProject();
            }
        }
    }
}
