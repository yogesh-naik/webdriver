package com.qtpselenium.core.hybrid.util;

import org.testng.AssertJUnit;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import com.qtpselenium.core.hybrid.util.*;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseTest {

	public WebDriver driver;
	public Properties prop;

	public ExtentReports rep = ExtentManager.getInstance();
	public ExtentTest test;
	boolean gridRun=true;

	SoftAssert sft = new SoftAssert();

	
	public BaseTest() {
		prop = new Properties();
		try {
			System.out.println("HERE -->" + (System.getProperty("user.dir")));
			FileInputStream fs = new FileInputStream(
					System.getProperty("user.dir") + "//src//test//resourcs//project.properties");
			prop.load(fs);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@BeforeSuite
//    public void beforeMethod(){
//        driver = new ChromeDriver();
//        //additional setup
//     }
//
//     @AfterSuite
//     public void afterMethod(){
//         driver.quit();
//         //additional tear down 
//     }
     
	public void openBrowser(String browserType) {

		if(!gridRun){
		if (browserType.equals("Mozilla")){
			System.setProperty("webdriver.gecko.driver","/Users/ynaik/Desktop/Books_and_Q_materials/webdriver_files/geckodriver.exe");
			driver = new FirefoxDriver();
		}
		else if (browserType.equals("IE"))
			driver = new InternetExplorerDriver();
		else if (browserType.equals("Chrome"))
			driver = new ChromeDriver();

		}else{// grid run
			
			DesiredCapabilities cap=null;
			if(browserType.equals("Mozilla")){
				cap = DesiredCapabilities.firefox();
				cap.setBrowserName("firefox");
				cap.setJavascriptEnabled(true);
				cap.setPlatform(org.openqa.selenium.Platform.MAC);
				
			}else if(browserType.equals("Chrome")){
				 cap = DesiredCapabilities.chrome();
				 cap.setBrowserName("chrome");
				 cap.setJavascriptEnabled(true);
				 cap.setPlatform(org.openqa.selenium.Platform.MAC);
			}
			
			try {
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		// long
		// implicitWaitTime=Long.parseLong(CONFIG.getProperty("implicitwait"));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}

	public void navigate(String url) {
		driver.get(prop.getProperty(url));
	}

	public void click(String locator) {
		if (locator.endsWith("_id"))
			getElement(locator).click();
		if (locator.endsWith("_xpath"))
			getElement(locator).click();
		if (locator.endsWith("_link"))
			getElement(locator).click();
		if (locator.endsWith("_name"))
			getElement(locator).click();
		if (locator.endsWith("_class"))
		getElement(locator).click();
	}

	public void type(String xPathKey, String data) {
		if (xPathKey.endsWith("_id"))
			getElement(xPathKey).sendKeys(data);
		else if (xPathKey.endsWith("_xpath"))
			getElement(xPathKey).sendKeys(data);
		else if (xPathKey.endsWith("_link"))
			getElement(xPathKey).sendKeys(data);
		else if (xPathKey.endsWith("_name"))
			getElement(xPathKey).sendKeys(data);
	}

	// Finding the element is present one time only
	public WebElement getElement(String locator) {
		WebElement e = null;

		try {
			if (locator.endsWith("_id"))
				e = driver.findElement(By.id(prop.getProperty(locator)));
			else if (locator.endsWith("_xpath"))
				e = driver.findElement(By.xpath(prop.getProperty(locator)));
			else if (locator.endsWith("_link"))
				e = driver.findElement(By.linkText(prop.getProperty(locator)));
			else if (locator.endsWith("_name"))
				e = driver.findElement(By.name(prop.getProperty(locator)));
			else if (locator.endsWith("_class"))
				e = driver.findElement(By.className(prop.getProperty(locator)));
			else {
				reportFailure("Locator not present" + locator);
				AssertJUnit.fail("Locator not present -" + locator);
			}

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			reportFailure(ex.getMessage());
			ex.printStackTrace();
			AssertJUnit.fail("Failed the test -" + ex.getMessage());
		}
		return e;

	}

	/*************** Reporting *********************/
	protected void reportFailure(String msg) {
		// TODO Auto-generated method stub
		test.log(LogStatus.FAIL, msg);
		takeScreenShot();
		Assert.fail(msg);
	}

	private void reportPass(String message) {
		// TODO Auto-generated method stub
		test.log(LogStatus.PASS, message);

	}

	public void takeScreenShot() {
		// fileName of the screenshot
		Date d = new Date();
		String screenshotFile = d.toString().replace(":", "_").replace(" ", "_") + ".png";
		// store screenshot in that file
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir") + "//screenshots//" + screenshotFile));
		} catch (IOException e) {
			// TODO Auto-generated catcsh block
			e.printStackTrace();
		}
		// put screenshot file in reports
		test.log(LogStatus.INFO, "Screenshot-> "
				+ test.addScreenCapture(System.getProperty("user.dir") + "//screenshots//" + screenshotFile));

	}

	/*************** Validation *********************/
	public boolean verifyTitle() {
		return false;
	}

	public boolean isElementPresent(String locator) {
		List<WebElement> elementList = null;

		if (locator.endsWith("_id"))
			elementList = driver.findElements(By.id(prop.getProperty(locator)));
		else if (locator.endsWith("_xpath"))
			elementList = driver.findElements(By.xpath(prop.getProperty(locator)));
		else if (locator.endsWith("_link"))
			elementList = driver.findElements(By.name(prop.getProperty(locator)));
		else if (locator.endsWith("_name"))
			elementList = driver.findElements(By.name(prop.getProperty(locator)));
		else {
			reportFailure("Locator not present" + locator);
			AssertJUnit.fail("Locator not present -" + locator);
		}
		if (elementList.size() == 0)
			return false;
		else
			return true;
	}

	public boolean verifyText(String locator, String expectedTextkey) {
		String actualText = getElement(locator).getText();
		String expectedText = prop.getProperty(expectedTextkey);

		if (actualText.equals(expectedText))
			return true;
		else
			return false;
	}

	/**************** App function *************/

	public boolean doLogin(String userName, String pwd) {

		// test.log(LogStatus.FAIL, "Screenshot->" +
		// test.addScreenCapture("/Users/ynaik/Desktop/M2M_Order.png"));

		click("gmailLink_xpath");
		// Verfiy text present
		if (verifyText("gmailtext_xpath", "signinText"))
			test.log(LogStatus.PASS, "Text not matching");

		// Softasserts
		sft.assertTrue(false, "Error1");
		sft.assertTrue(false, "Error2");
		sft.assertTrue(!(verifyText("gmailtext_xpath", "signinText")), "Text Matched");

		click("signin_xpath");
		type("emailID_txt_xpath", userName);
		takeScreenShot();
		click("LoginNextbtn_xpath");

		//driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS); 
		type("password_xpath", pwd);
		click("pwdNext_xpath");
		return false;
	}
}
