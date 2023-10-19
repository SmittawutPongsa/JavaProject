package Fix;

import FileHandler.IniFile;
import Hexagon.HexGrid;
import Test.drawPolygonDemo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainWindow {
    public MainWindow(String directory, String mapName){
        File mapFile = new File(directory + File.separator + "Maps" + File.separator + mapName);
        File[] mapSpriteFiles = new File(directory + File.separator + "Sprites").listFiles();
        File[] unitFiles = new File(directory + File.separator + "Units").listFiles();

        File[] unitSpriteFiles = new File(directory + File.separator + "Units").listFiles();
        File[] enemyFiles = new File(directory + File.separator + "Units" + File.separator + "AttackerSprites").listFiles();
        File[] playerFiles = new File(directory + File.separator + "Units" + File.separator + "PlayerSprites").listFiles();

        Map<String, ImageIcon> mapSprites = new HashMap<String, ImageIcon>();
        Map<String, ImageIcon> unitSprites = new HashMap<String, ImageIcon>();
        Map<String, Unit> units = new HashMap<String, Unit>();
        Map<String, Grid> grids = new HashMap<String, Grid>();

        for(File file : mapSpriteFiles){
            ImageIcon sprite = new ImageIcon(new drawPolygonDemo(file).getTexture());
            mapSprites.put(file.getName(), sprite);
        }
        for(File file : unitSpriteFiles){
            ImageIcon sprite = new ImageIcon(new drawPolygonDemo(file).getTexture());
            unitSprites.put(file.getName(), sprite);
        }
/*
        for(File file : unitFiles){
            try {
                IniFile data = new IniFile(file.getAbsolutePath());
                String section = "main";
                String name = data.getProperty(section, "name");
                int type = Integer.parseInt(data.getProperty(section, "type"));
                int maxHp = Integer.parseInt(data.getProperty(section, "hp"));
                int atk = Integer.parseInt(data.getProperty(section, "atk"));
                int mov = Integer.parseInt(data.getProperty(section, "speed"));
                int range = Integer.parseInt(data.getProperty(section, "range"));
                ImageIcon sprite = unitSprites.get(data.getProperty(section, "sprite"));

                Unit unit = new Unit(name, sprite, maxHp, atk, mov, range,type);
                units.put(name, unit);
            }catch (IOException e){
                System.out.println(e);
            }
        }
*/

        for(File file : playerFiles){
            try{
                IniFile data = new IniFile(file.getAbsolutePath());
                String section = "main";
                String name = data.getProperty(section, "name");
                int type = Integer.parseInt(data.getProperty(section, "type"));
                int maxHp = Integer.parseInt(data.getProperty(section, "hp"));
                int atk = Integer.parseInt(data.getProperty(section, "atk"));
                int mov = Integer.parseInt(data.getProperty(section, "speed"));
                int range = Integer.parseInt(data.getProperty(section, "range"));
//                int type = 0;
//                int maxHp = 10;
//                int atk = 5;
//                int mov = 2;
//                int range = 2;
                ImageIcon sprite = unitSprites.get(data.getProperty(section, "sprite"));

                Unit unit = new Unit(name, sprite, maxHp, atk, mov, range, type);
                units.put(name, unit);

            }catch (IOException e){
                System.out.println(e);
            }
        }
        for(File file : enemyFiles){
            try{
                IniFile data = new IniFile(file.getAbsolutePath());
                String section = "main";
                String name = data.getProperty(section, "name");
                int type = Integer.parseInt(data.getProperty(section, "type"));
                int maxHp = Integer.parseInt(data.getProperty(section, "hp"));
                int atk = Integer.parseInt(data.getProperty(section, "atk"));
                int mov = Integer.parseInt(data.getProperty(section, "speed"));
                int range = Integer.parseInt(data.getProperty(section, "range"));
//                int type = 1;
//                int maxHp = 10;
//                int atk = 5;
//                int mov = 2;
//                int range = 2;
                ImageIcon sprite = unitSprites.get(data.getProperty(section, "sprite"));

                Unit unit = new Unit(name, sprite, maxHp, atk, mov, range, type);
                units.put(name, unit);

            }catch (IOException e){
                System.out.println(e);
            }
        }


        String[][] position = new String[0][0];

        try{
            IniFile mapData = new IniFile(mapFile.getAbsolutePath());
            String section = "info";
            int cols = Integer.parseInt(mapData.getProperty(section, "cols"));
            int rows = Integer.parseInt(mapData.getProperty(section, "rows"));

            position = new String[cols][rows];

            int SIZE = 30;
            double height = SIZE * 2;
            double width = SIZE * Math.sqrt(3);
            double x = width, y = 1.5 * SIZE;

            for(int i = 0; i < cols; i++){
                x = width;
                if (i % 2 == 1) {
                    x += width / 2;
                }
                for(int j = 0; j < rows; j++){
                    String key = Integer.toString(j) + "," + Integer.toString(i);
                    position[i][j] = key;
                    Boolean canPassed = Boolean.valueOf(mapData.getProperty("Terrain", key));
                    ImageIcon sprite = mapSprites.get(mapData.getProperty("Sprite", key));
                    Unit unit = null;
                    if(units.get(mapData.getProperty("Unit", key)) != null){
                        unit = new Unit(units.get(mapData.getProperty("Unit", key)));
                    }

                    grids.put(position[i][j], new Grid(x, y, canPassed, sprite, unit));
                    x += width;
                }
                y += 1.5 * SIZE;
            }
        }catch (IOException e){
            System.out.println(e);
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));

        MapPanel panel = new MapPanel(mapSprites, unitSprites, grids, position);

        frame.add(panel);

        frame.setVisible(true);
    }
}
