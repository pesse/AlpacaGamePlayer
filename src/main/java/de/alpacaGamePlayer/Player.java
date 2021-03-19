package de.alpacaGamePlayer;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Player {

    final WebDriver browser;
    final WebElement canvas;

    private boolean hadAction = false;

    public Player(WebDriver browser, WebElement canvas) {
        this.browser = browser;
        this.canvas = canvas;
    }

    public void clickAt(int x, int y, int sourceWidth, int sourceHeight) {
        hadAction = true;

        float ratioX = (float)canvas.getSize().getWidth()/(float)sourceWidth;
        float ratioY = (float)canvas.getSize().getHeight()/(float)sourceHeight;

        int clickX = Math.round(x*ratioX) - canvas.getSize().getWidth()/2 + 10;
        int clickY = Math.round(y*ratioY) - canvas.getSize().getHeight()/2 + 10;

        new Actions(browser)
                .moveToElement(canvas, clickX, clickY)
                .click()
                .build()
                .perform();
    }

    public void jump() {
        System.out.println("Jump!");
        var random = new Random();
        hadAction = true;
        var actions = new Actions(browser)
                .sendKeys(canvas, Keys.SPACE)
                .moveToElement(canvas, random.nextInt(50), random.nextInt(25))
                //.click()
                .build();

        actions.perform();
        /*try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        actions.perform();*/
    }

    public void resetAction() {
        hadAction = false;
    }

    public boolean hadAction() {
        return hadAction;
    }
}
