package de.alpacaGamePlayer;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {
    public static void main(String[] args) {

        WebDriverManager.chromedriver().setup();
        var game = new Game(new ChromeDriver());

        //WebDriverManager.firefoxdriver().setup();
        //var game = new Game(new FirefoxDriver());

        game.start();

        game.stop();

        /*
        document.addEventListener('keydown', function(event) { console.log("Down: " + event.key); });
         */
    }
}
