import static org.junit.Assert.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.After;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Keys;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.List;


public class stepdefs {

    WebDriver driver = new ChromeDriver();
    WebDriverWait wait = new WebDriverWait(driver,10);
    String etsyUrl = "";
    String etsyUser = "";
    String etsyMail = "";
    String etsyPasswd = "";
    String etsyFirstItem = "";
    String etsySecondItem = "";
    String gistUrl = "";
    String gistUser = "";
    String gistPasswd = "";
    String gistId = "";
    String createGistBody = "{\"description\":\"Created via API\",\"public\":\"true\",\"files\":{\"file1.txt\":{\"content\":\"Demo\"}}";

    public stepdefs() {
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream("./config.properties");
            prop.load(input);
            gistUser = prop.getProperty("gistUser");
            gistPasswd = prop.getProperty("gistPasswd");
            gistUrl = prop.getProperty("gistUrl");
            etsyUser = prop.getProperty("etsyUser");
            etsyMail = prop.getProperty("etsyMail");
            etsyPasswd = prop.getProperty("etsyPasswd");
            etsyUrl = prop.getProperty("etsyUrl");
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @After
    public void doSomethingAfter(){				
        driver.close();
    }

    @When("user is created")
    public void user_is_created() {
        driver.get(etsyUrl);
        driver.findElement(By.xpath("//button[@data-gdpr-single-choice-accept]")).click();
        driver.findElement(By.xpath("//a[@id='register']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@id,'neu_email')]")));
        driver.findElement(By.xpath("//input[contains(@id,'neu_email')]")).sendKeys(etsyMail);
        driver.findElement(By.xpath("//input[contains(@id,'neu_first')]")).sendKeys(etsyUser);
        driver.findElement(By.xpath("//input[contains(@id,'neu_password')]")).sendKeys(etsyPasswd);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@value='register'])[1]")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class,'alert-success')]")));
        driver.findElement(By.xpath("(//button[@value='register'])[1]")).sendKeys(Keys.RETURN);
    }

    @When("user login")
    public void user_login() {
        driver.manage().deleteAllCookies();
        driver.get(etsyUrl);
        driver.findElement(By.xpath("//button[@data-gdpr-single-choice-accept]")).click();
        driver.findElement(By.xpath("//a[@id='sign-in']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@id,'neu_email')]")));
        driver.findElement(By.xpath("//input[contains(@id,'neu_email')]")).sendKeys(etsyMail);
        driver.findElement(By.xpath("//input[contains(@id,'neu_password')]")).sendKeys(etsyPasswd);
        try {
            WebElement captcha = driver.findElement(By.xpath("//@class='recaptcha-checkbox-checkmark'"));
            captcha.click();  
        } catch(NoSuchElementException e) {
        }
        driver.findElement(By.xpath("//button[@value='sign-in']")).sendKeys(Keys.RETURN);
    }
    
    @Then("user is logged in the page")
    public void user_is_logged_in_the_page() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@class,'nav-icon-image')]")));
        driver.findElement(By.xpath("//span[contains(@class,'nav-icon-image')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='name']")));
        String userTxt = driver.findElement(By.xpath("//p[@class='name']")).getText();
        assertEquals(userTxt, etsyUser);
    }
    
    @When("user search for {string}")
    public void user_search_for(String elementToSearch) {
        driver.get(etsyUrl);
        driver.findElement(By.xpath("//input[contains(@id,'search-query')]")).sendKeys(elementToSearch);
        driver.findElement(By.xpath("//button[@value='Search']")).sendKeys(Keys.RETURN);
    }
    
    @When("user sort by price ascending")
    public void user_sort_by_price_ascending() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@aria-labelledby='dropdown-label'])[1]")));
        driver.findElement(By.xpath("(//button[@aria-labelledby='dropdown-label'])[1]")).click();
        driver.findElement(By.xpath("(//button[@aria-labelledby='dropdown-label'])[1]/..//li[3]")).click();
    }
    
    @Then("items are sorted")
    public void items_are_sorted() {
        List<WebElement> allPrices = driver.findElements(By.xpath("(//li//a[@data-query])//span[@class='currency-value']"));
        Float previous = new Float(99999999);
        for(WebElement w : allPrices) {
            String number = w.getText().replace(".", "").replace(",", ".");
            float f = Float.parseFloat(number);
            if (f>previous) {
                throw new AssertionError("Items are not sorted; previous item is '" + previous 
                                        + "' current item value is '" + f + "'");
            }
            previous = f;
        }
    }
    
    @When("user add most expensive item")
    public void user_add_most_expensive_item() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//li//a[@data-query])[1]")));
        String itemUrl = driver.findElement(By.xpath("(//li//a[@data-query])[1]")).getAttribute("href");
        driver.get(itemUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(@class,'buy') and contains(@class,'primary')])")));
        etsyFirstItem = driver.findElement(By.xpath("//span[@itemprop='title']")).getText();
        driver.findElement(By.xpath("(//button[contains(@class,'buy') and contains(@class,'primary')])")).click();    
    }
    
    @When("add any item to cart")
    public void add_any_item_to_cart() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//li//a[@data-query])[1]")));
        String itemUrl = driver.findElement(By.xpath("(//li//a[@data-query])[1]")).getAttribute("href");
        driver.get(itemUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(@class,'buy') and contains(@class,'primary')])")));
        etsySecondItem = driver.findElement(By.xpath("//span[@itemprop='title']")).getText();
        driver.findElement(By.xpath("(//button[contains(@class,'buy') and contains(@class,'primary')])")).click();
    }
    
    @Then("validate cart content have added items")
    public void validate_cart_content_have_added_items() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class,'icon-cart')]")));
        driver.findElement(By.xpath("//span[contains(@class,'icon-cart')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'shop-details')]//a[contains(@class,'text-large')]")));
        String firstText = driver.findElement(By.xpath("//div[contains(@class,'shop-details')]//a[contains(@class,'text-large')]")).getText();
        String secondText = driver.findElement(By.xpath("//div[contains(@class,'shop-details')]//a[contains(@class,'text-large')]")).getText();
        assertThat(firstText, anyOf(is(etsyFirstItem), is(etsySecondItem)));
        assertThat(secondText, anyOf(is(etsyFirstItem), is(etsySecondItem)));
    }

    @When("^create gist$")
    public void gist_is_created() {
        gistId = given().auth().preemptive().basic(gistUser, gistPasswd).body(createGistBody).when().post(gistUrl).then().statusCode(201).extract().path("id");
    }
    
    @Then("^gist is succesfully created$")
    public void gist_is_succesfully_created() {
        given().auth().preemptive().basic(gistUser, gistPasswd).when().get(gistUrl + "/" + gistId).then()
            .body("$", hasKey("url"))
            .body("$", hasKey("forks_url"))
            .body("$", hasKey("commits_url"))
            .body("$", hasKey("id"))
            .body("$", hasKey("node_id"))
            .body("$", hasKey("git_pull_url"))
            .body("$", hasKey("git_push_url"))
            .body("$", hasKey("html_url"))
            .body("$", hasKey("files"))
            .body("$", hasKey("url"))
            .body("$", hasKey("public"))
            .body("$", hasKey("created_at"))
            .body("$", hasKey("updated_at"))
            .body("$", hasKey("description"))
            .body("$", hasKey("comments"))
            .body("$", hasKey("user"))
            .body("$", hasKey("comments_url"))
            .body("$", hasKey("owner"))
            .body("$", hasKey("forks"))
            .body("$", hasKey("history"))
            .body("$", hasKey("truncated"));
    }
    
    @When("^delete gist$")
    public void gist_is_deleted() {
        given().auth().preemptive().basic(gistUser, gistPasswd).when().delete(gistUrl + "/" + gistId).then().statusCode(204);
    }
    
    @Then("^gist is succesfully deleted$")
    public void gist_is_succesfully_deleted() {
        given().auth().preemptive().basic(gistUser, gistPasswd).when().get(gistUrl + "/" + gistId).then().statusCode(404);
    }
	
}