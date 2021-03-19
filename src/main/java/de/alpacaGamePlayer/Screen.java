package de.alpacaGamePlayer;

import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Screen {

    private final BufferedImage img;
    long creationTime;

    private List<Point> obstacles;
    private Point alpaca;
    private Point callCloseButton;

    public Screen(BufferedImage img) {
        this.img = img;
        this.creationTime = System.currentTimeMillis();
    }


    private Point findInArea(int[][] pattern, Rectangle area) {
        int w = Math.min(area.x+area.width, img.getWidth());
        int h = Math.min(area.y+area.height, img.getHeight());

        for ( int x = area.x; x < w; x++ ) {
            for (int y = area.y; y < h; y++) {
                boolean match = true;
                for (int py = 0; py < pattern.length; py++ ) {
                    for ( int px = 0; px < pattern[py].length; px++) {
                        match = match &&
                                (img.getRGB(x+px, y+py) == pattern[py][px]);
                    }
                }
                if ( match ) return new Point(x, y);
            }
        }
        return null;
    }

    public Point findPlayButton() {
        int[][] pattern = new int[2][1];
        pattern[0][0] = 0xffffffff;
        pattern[1][0] = 0xff1e2746;

        return findInArea(pattern, new Rectangle(600, 390, 40, 1));
    }


    public Point findObstacle(int startX) {
        for (int x = startX-50; x < startX; x++) {
            var c = new Color(img.getRGB(x, 523));
            var hsb = new float[3];
            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
            float deg = hsb[0]*360;
            if ( deg >= 270 && deg <= 360) {
                //System.out.println("Obstacle at " + x + ": " + c.toString());
                return new Point(x, 523);
            }
        }
        return null;
    }

    public Point findAlpaca() {
        if ( alpaca == null ) {
            int footColor = 0xffef038d;
            int startX = 550;
            int startY = 479;

            for (int y = startY; y > startY - 400; y--) {
                int p = img.getRGB(startX, y);
                if (p == footColor) {
                    alpaca = new Point(startX, y);
                    break;
                }
            }

            if (alpaca == null) alpaca = new Point(0,0);
        }

        return alpaca;
    }

    public Point findCallCloseButton() {
        if ( callCloseButton == null ) {
            int searchColor = 0xffff0000;
            for (int x = 50; x < img.getWidth() - 100; x = x + 20) {
                for (int y = 100; y < 300; y = y + 20) {
                    if (img.getRGB(x, y) == searchColor) return new Point(x, y);
                }
            }
            if (callCloseButton == null) callCloseButton = new Point(0, 0);
        }
        return callCloseButton;
    }

    public boolean hasCallCloseButton() {
        return findCallCloseButton().x > 0;
    }

    public int getWidth() {
        return img.getWidth();
    }

    public int getHeight() {
        return img.getHeight();
    }

    public BufferedImage getImg() {
        return img;
    }

    public long getCreationTime() {
        return creationTime;
    }

    private boolean isMagenta(Color c) {
        var hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
        float deg = hsb[0]*360;
        return deg >= 270 && deg <= 360;
    }

    public Collection<Point> findObstacles() {
        if ( obstacles == null ) {
            obstacles = new ArrayList<>(2);
            int x = img.getWidth() / 2 + 100;
            int y = 523;
            while (x < img.getWidth()) {
                if (isMagenta(new Color(img.getRGB(x, y)))) {
                    obstacles.add(new Point(x, y));
                    x += 200;
                }
                x++;
            }
        }
        return obstacles;
    }

    public Point nearestObstacle() {
        return findObstacles().stream().findFirst().orElse(null);
    }

    public Point farthestObstacle() {
        return findObstacles().stream().max(Comparator.comparing(Point::getX)).orElse(null);
    }
}
