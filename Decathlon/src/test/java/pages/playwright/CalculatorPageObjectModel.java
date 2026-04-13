package pages.playwright;


import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

import java.util.List;

public class CalculatorPageObjectModel {
    //Locators
    private String nameField = "#name2";
    private String eventDropDown = "#event";
    private String resultField = "#raw";
    private String decathlonHepaRadioBtn = "input[name = 'mode']";
    private String standingsTable = "#standings";
    private String calculateBtn = "#save";
    private String confirmMsg = "#msg";
    private Page page;
    private String pageTitle = "#pageTitle";




    public CalculatorPageObjectModel(Page page) {
        this.page = page;

    }


    public void calculateScore(String competitorName, String multiEvent, String event, String result) {
        getElement(page, nameField).fill(competitorName);
        selectLabelDropdownList(page, eventDropDown, event);
        getElement(page, resultField).fill(result);
        getElement(page, calculateBtn).click();

    }

    public String getScore() {


            //String output = waitForElement(page, confirmMsg).textContent();
            String output1 = getElement(page, confirmMsg).textContent();
            //getElement(page,decathlonHepaRadioBtn).click();
            return output1;




    }

    private static Locator getElement(Page page, String selector) {
        return page.locator(selector);

    }
    private static ElementHandle waitForElement(Page page, String selector) {
        return page.waitForSelector(selector);

    }

    private static List selectLabelDropdownList(Page page, String selector, String event) {
        return page.selectOption(selector, event);
    }
}
