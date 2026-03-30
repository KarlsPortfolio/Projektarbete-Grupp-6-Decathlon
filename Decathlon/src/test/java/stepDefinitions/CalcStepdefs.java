package stepDefinitions;

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

public class CalcStepdefs {
    private WebDriver driver;

    private By nameField = By.cssSelector("#name2");
    private By eventDropDown = By.cssSelector("#event");
    private By resultField = By.cssSelector("#raw");
    private By decathlonHepaRadioBtn = By.name("mode");
    private By standingsTable = By.cssSelector("#standings");
    private By calculateBtn = By.cssSelector("#save");
    private By confirmMsg = By.cssSelector("#msg");

    @Given("I am using {string} as a browser")
    public void iAmUsingAsABrowser(String browser) {
        if(browser.equalsIgnoreCase("chrome")){
            driver = new ChromeDriver();

        }else if(browser.equalsIgnoreCase("edge")){
            driver = new EdgeDriver();

        }else if(browser.equalsIgnoreCase("firefox")){
            driver = new FirefoxDriver();
        }
    }

    @And("I am on page {string}")
    public void iAmOnPage(String page) {
        if(page.equalsIgnoreCase("calculator")){
            driver.get("http://localhost:8080/");
        }
    }

    @When("I calculate points {string} {string} {string} {string}")
    public void iCalculatePoints(String competitorName, String multiEvent, String event, String result) {
        waitForElementVisible(driver, nameField).sendKeys(competitorName);
        waitForDropDownVisible(driver,eventDropDown).selectByValue(event);
        waitForElementVisible(driver, resultField).sendKeys(result);
        waitForElementVisible(driver, calculateBtn).click();




    }

    @Then("I get points <points>")
    public void iGetPointsPoints(double points) {
        System.out.println(waitForElementVisible(driver, confirmMsg).getText());

    }

    @After
    public void breakDown(){
        driver.quit();
    }

    private static WebElement waitForElementVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return element;
    }


    private static Select waitForDropDownVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Select dropDown = new Select(driver.findElement(locator));
        return dropDown;
    }
}
