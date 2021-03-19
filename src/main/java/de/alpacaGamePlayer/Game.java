package de.alpacaGamePlayer;

import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Game {
    final WebDriver browser;

    WebElement canvas;
    Player player;

    public Game(WebDriver browser) {
        this.browser = browser;
    }

    private BufferedImage getCanvasImage(WebElement canvas) throws IOException {
        var dataURL = (String)((JavascriptExecutor)browser).executeScript("return arguments[0].playerData;", canvas);

        try (ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(dataURL.substring(22)))) {
            return ImageIO.read(in);
        }
    }

    private void save(BufferedImage image, String action) throws IOException {
        File s = new File("screens/screen"+ (new SimpleDateFormat("HHmmssSSS")).format(new Date()) +"_" + action + ".png");
        ImageIO.write(image, "png", s);
    }

    private boolean isInit(BufferedImage screen) {
        int w = screen.getWidth();
        int h = screen.getHeight();

        for ( int x = 50; x < w; x++ ) {
            for (int y = 0; y < h; y++) {
                if (screen.getRGB(x, y) > 0xff000000) return true;
            }
        }
        return false;
    }

    private void waitUntilPageLoaded() {
        var wait = new WebDriverWait(browser, 20);
        wait.until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    private void injectCanvasDataCollector( JavascriptExecutor javascriptExecutor ) {
        javascriptExecutor.executeScript("""
                function saveCanvasData() { 
                    let canvasElem = document.getElementsByTagName('canvas')[0];
                    canvasElem.playerData = canvasElem.toDataURL('image/png');
                    window.requestAnimationFrame(saveCanvasData);
                }
                window.requestAnimationFrame(saveCanvasData);
                """);
    }

    private void info(int loopNumber, Screen curScreen, Screen lastScreen, int madeWay) {
        long sinceLastTime = 0;
        if ( lastScreen != null ) sinceLastTime = curScreen.getCreationTime()-lastScreen.getCreationTime();
        System.out.println("Loop " + loopNumber + ", Time passed: " + sinceLastTime + ", Alpaca: " + curScreen.findAlpaca().y + ", Obstacle way " + madeWay);
    }

    private void loop() throws Exception {
        int loop = 0;
        Point obstacle = null;
        Screen lastScreen = null;
        int madeWay = 0;

        while ( loop < 2000 ) {
            var screen = new Screen(getCanvasImage(canvas));
            String action = null;
            player.resetAction();

            var playButton = screen.findPlayButton();
            if (playButton != null) {
                player.clickAt(playButton.getX(), playButton.getY(), screen.getWidth(), screen.getHeight());
                action = "clickStart";
            }

            if ( !player.hadAction() && screen.hasCallCloseButton() ) {
                Point p = screen.findCallCloseButton();

                if ( screen.farthestObstacle() != null && screen.farthestObstacle().x > 1000) {
                    // Wait
                }
                else {
                    player.clickAt(p.x, p.y, screen.getWidth(), screen.getHeight());
                    action = "clickCall";
                }
            }

            if ( !player.hadAction() ) {
                if ( screen.nearestObstacle() != null ) {
                    int lastX = screen.getWidth();
                    if (lastScreen != null && lastScreen.nearestObstacle() != null) lastX = lastScreen.nearestObstacle().x;

                    madeWay = lastX-screen.nearestObstacle().x;
                }

                if ( screen.nearestObstacle() != null && screen.nearestObstacle().x <= 900) { // Close obstacle!
                    player.jump();
                    action = "jump";
                }
            }

            info(loop, screen, lastScreen, madeWay);
            //save(screen.getImg(), action);

            loop++;
            lastScreen = screen;
        }
    }

    public void start() {
        browser.get("https://alpakagame.ch/");

        try {
            waitUntilPageLoaded();

            injectCanvasDataCollector((JavascriptExecutor) browser);

            canvas = browser.findElement(By.tagName("canvas"));
            player = new Player(browser, canvas);

            while (!isInit(getCanvasImage(canvas))) {
                Thread.sleep(100);
            }

            loop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        browser.close();
    }
}
