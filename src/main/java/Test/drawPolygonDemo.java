package Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class drawPolygonDemo{
    static int size = 50;
    Polygon hexagon;
    BufferedImage input;

    public drawPolygonDemo(File imageFile) {
        try{
            input = ImageIO.read(imageFile);
        }catch (IOException e){
            System.out.println(e);
        }
        hexagon = createHexagon();
    }

    public BufferedImage getTexture(){
        return getTextureImage(input, hexagon);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame(drawPolygonDemo.class.toString());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 800));

        Polygon hexagon = createHexagon();

        BufferedImage image = null;
        BufferedImage texture = null;
        try {
            image = ImageIO.read(new File("C://Users/User/Desktop/Project_test/Demo/Sprites/test.jpg"));

        }catch (IOException e){
            System.out.println(e);
        }
        texture = getTextureImage(image, hexagon);
        frame.add(new JLabel(new ImageIcon(texture)));
        frame.setVisible(true);
    }

    public static Polygon createHexagon() {
        Polygon temp = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_deg = 60 * i - 30;
            double angle_rad = Math.PI / 180 * angle_deg;
            int _x = (int) (size * Math.cos(angle_rad));
            int _y = (int) (size * Math.sin(angle_rad));
            temp.addPoint(_x, _y);
        }
        return temp;
    }

    public static BufferedImage getTextureImage(BufferedImage src, Polygon hexagon){
        Rectangle r = hexagon.getBounds();
        BufferedImage temp = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = temp.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        AffineTransform centerTranform = AffineTransform.getTranslateInstance(-r.x, -r.y);
        g.setTransform(centerTranform);
        g.setClip(hexagon);
        g.drawImage(src, r.x, r.y, r.width, r.height, null);
        g.dispose();

        return temp;
    }
}
