import de.alpacaGamePlayer.Screen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DetectObstaclesTest {

    private BufferedImage loadImg(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    @Test
    void detect_Two_Obstacles() throws IOException {
        Screen screen = new Screen(loadImg("src/test/resources/2_obstacles.png"));

        long start = System.currentTimeMillis();
        var obstacles = screen.findObstacles();
        System.out.println("Performance: " + (System.currentTimeMillis()-start));

        Assertions.assertEquals(2, obstacles.size());
    }

    @Test
    void nearest_obstacle_returns_point() throws IOException {
        Screen screen = new Screen(loadImg("src/test/resources/2_obstacles.png"));

        var obstacle = screen.nearestObstacle();
        Assertions.assertEquals(739, obstacle.getX());
    }
}
