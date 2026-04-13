package stepDefinitions.playwright;

import com.microsoft.playwright.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.playwright.CalculatorPageObjectModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalcStepdefs {

    private Playwright playwright;
    Browser browser;
    private Page page;
    private CalculatorPageObjectModel calc;
    BrowserContext context;

    @Given("I am using {string} as a browser")
    public void iAmUsingAsABrowser(String browserType) {

        this.playwright = Playwright.create();
        if (browserType.equalsIgnoreCase("chrome") && browser == null) {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            this.context = browser.newContext();
        }else if (browserType.equalsIgnoreCase("firefox") && browser == null) {
            browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            this.context = browser.newContext();
        }else if (browserType.equalsIgnoreCase("edge") && browser == null) {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setChannel("msedge"));
            this.context = browser.newContext();
        }
        //page = context.newPage();
        page = browser.newPage();
    }

    @And("I am on page {string}")
    public void iAmOnPage(String url) {
        if (url.equalsIgnoreCase("calculator")) {
            page.navigate("http://localhost:8080/");
            calc = new CalculatorPageObjectModel(page);
        }
    }

    @When("I calculate points {string} {string} {string} {string}")
    public void iCalculatePoints(String competitorName, String multiEvent, String event, String result) {
        calc.calculateScore(competitorName, multiEvent, event, result);


    }

    @Then("I get the score {string}")
    public void iGetTheScore(String points) {
        boolean isDecathlon = true;

        String score_text = calc.getScore();

        String score[] = score_text.split(" ");

        int actual = Integer.parseInt(score[1]);
        int expected = Integer.parseInt(points);

        assertEquals(expected, actual);

    }

}