import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestClass {
    private static WebDriver driver;
    private final String rightUrl="https://mail.google.com/mail/u/0/#inbox";

    @DataProvider
    public Object[][] loginData(){
        return new Object[][]{{"korolkoaleksandra@gmail.com","$123sasha"}};
    }

    @BeforeClass
    public static void setup() {
        File file = new File("C:\\Program Files\\chromedriver\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://accounts.google.com/signin/v2/sl/pwd?service=mail&passive=true&rm=false&continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&ss=1&scc=1&ltmpl=default&ltmplcache=2&emr=1&osid=1&flowName=GlifWebSignIn&flowEntry=ServiceLogin");
    }

    @Test(dataProvider = "loginData")
    public void login(String login,String password){

        WebElement loginField= driver.findElement(By.xpath("//*[@id='identifierId']"));
        loginField.sendKeys(login);
        WebElement butNext=driver.findElement(By.xpath("//*[@id='identifierNext']"));
        butNext.click();

        WebDriverWait wait=new WebDriverWait(driver,30);
        WebElement passwordField= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='password']")));
        passwordField.sendKeys(password);
        passwordField.sendKeys(Keys.ENTER);


        WebDriverWait waitUrl=new WebDriverWait(driver,30);
        waitUrl.until(ExpectedConditions.urlToBe(rightUrl));
        String currentUrl=driver.getCurrentUrl();
        Assert.assertEquals(rightUrl,currentUrl);
    }

    @Test(dependsOnMethods = {"login"})
    public void sendMail(){
        WebElement mailButton= driver.findElement(By.xpath("//div[@role='button' and contains(text(),'НАПИСАТЬ')]"));
        mailButton.click();

        WebElement email= driver.findElement(By.xpath("//textarea[@aria-label='Кому']"));
        email.sendKeys("korolkoaleksandra@gmail.com");


        WebElement text= driver.findElement(By.xpath("//div[@aria-label='Тело письма']"));
        text.sendKeys("Hello, world!");

        WebElement buttonSend=driver.findElement(By.xpath("//div[@role='button' and contains(text(),'Отправить')]"));
        buttonSend.click();

        WebElement inbox=driver.findElement(By.xpath("//a[@title='Входящие']"));
        inbox.click();

        WebDriverWait wait=new WebDriverWait(driver,30);
        wait.until(ExpectedConditions.textToBePresentInElement(By.xpath("//div[@class='Cp']//tbody//child::tr[1]//td[4]//div[2]//span"),"я"));
        WebElement correctEmail=driver.findElement(By.xpath("//div[@class='Cp']//tbody//child::tr[1]//td[4]//div[2]//span"));
        Assert.assertEquals(correctEmail.getText(),"я");

        WebElement correctText= driver.findElement(By.xpath("//div[@class='Cp']//tbody//child::tr[1]//td[6]//div//div//div//span[@class='y2']"));
        Assert.assertEquals(correctText.getText()," - Hello, world!");

    }

    @Test(dependsOnMethods={"sendMail"})
    public void openMail(){

        WebElement emailMessage=driver.findElement(By.xpath("//div[@class='Cp']//tbody//child::tr[1]//td[4]//div[2]//span"));
        emailMessage.click();

        WebElement textMessage=driver.findElement(By.xpath("//div[@class='ii gt']//div[1]//div[1]"));
        Assert.assertEquals(textMessage.getText(),"Hello, world!");

    }

    @AfterClass
    public void exit(){
        driver.quit();
    }
}
