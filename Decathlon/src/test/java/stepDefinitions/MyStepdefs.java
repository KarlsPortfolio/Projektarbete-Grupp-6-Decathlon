package stepDefinitions;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyStepdefs {
    private WebDriver driver;
    private WebDriverWait wait;
    private By nameField = By.cssSelector("#name2");
    private By eventDropDownList = By.cssSelector("#event");
    private By resultField = By.cssSelector("#raw");
    private By decathlonRadioButton = By.name("mode");
    private By heptathlonToggle = By.cssSelector("#member_firstname");
    private By standingsTable = By.cssSelector("#standings");
    private By calculeBtn = By.cssSelector("#save");

    @Given("I am using {string} as a browser")
    public void iAmUsingAsABrowser(String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        }
    }

    @And("I am at page {string}")
    public void iAmAtPage(String page) {
        if(page.equalsIgnoreCase("calculator")){
            driver.get("http://localhost:8080/");
        }
    }

    @When("I calculate points {string} {string} {string} {string}")
    public void iCalculatePoints(String name, String multievent, String event, String result) {
        waitForElementVisible(driver, nameField).sendKeys(name);
        waitForElementVisible(driver, resultField).sendKeys(result);
        waitForDropDownVisible(driver,eventDropDownList).selectByValue("100m");
        waitForElementVisible(driver, calculeBtn).click();


    }

    @Then("I get points {string}")
    public void iGetPoints(String point) {
        System.out.println("Test");
        //List<WebElement> standings = getElements(driver, standingsTable);

        //for(WebElement cell : standings) {
          //  System.out.println(cell.getText());
        //}

    }

    @After
    public void breakDown() {


        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Driver stängdes abrupt vid driver.quit(): " + e.getMessage());

            }
        }

    }

    private static WebElement waitForElementVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return element;
    }

    private static List<WebElement> getElements(WebDriver driver, By by){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<WebElement> output = driver.findElements(by);

        return output;

    }

    private static Select waitForDropDownVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Select dropDown = new Select(driver.findElement(locator));
        return dropDown;
    }


}

