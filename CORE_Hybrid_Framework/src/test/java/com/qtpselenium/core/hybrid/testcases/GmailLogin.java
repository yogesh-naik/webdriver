package com.qtpselenium.core.hybrid.testcases;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

//import com.ebay.data.utils.spreadsheet.EasyFilter;
//import com.ebay.data.utils.spreadsheet.SpreadSheetUtil;
//import com.ebay.data.utils.spreadsheet.TestObject;
import com.qtpselenium.core.hybrid.util.BaseTest;
import com.qtpselenium.core.hybrid.util.ExtentManager;
import com.qtpselenium.core.hybrid.util.GenericKeywords;
import com.qtpselenium.core.hybrid.util.User;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


public class GmailLogin extends BaseTest{
		
	SoftAssert sft = new SoftAssert();
//	@DataProvider(name = "CheckoutTests", parallel = true)
//	public Iterator<Object[]> getCheckoutData(Method m, ITestContext testContex) throws Exception {
////		EasyFilter filter = EasyFilter.equalsIgnoreCase(TestObject.TEST_METHOD, m.getName());
////		filter = EasyFilter.and(filter, EasyFilter.not(EasyFilter.equalsIgnoreCase(TestObject.TEST_CASE_ID, "28")));
////
////		// filter = EasyFilter.and(filter, EasyFilter.equalsIgnoreCase(TestObject.TEST_SITE, "US"));
////		// filter = EasyFilter.and(filter,EasyFilter.equalsIgnoreCase("Active","true"));
////		// filter = EasyFilter.and(filter, EasyFilter.equalsIgnoreCase(TestObject.TEST_CASE_ID, "56"));
//
//		LinkedHashMap<String, Class<?>> classMap = new LinkedHashMap<String, Class<?>>();
//		classMap.put("User", User.class);
////		classMap.put("CreateGuestCheckoutSessionRequest", CreateGuestCheckoutSessionRequest.class);
////		classMap.put("CheckoutFlow", String.class);
//
//		return SpreadSheetUtil.getEntitiesFromSpreadsheet(this.getClass(), classMap, "checkout.xls", 0, null, null);
//	}
	@Test(groups = { "checkoutFlows2" })//,dataProvider = "CheckoutTests")
	public void doLogin(User sp) throws InterruptedException{
		
		test = rep.startTest("Test with screenshot");
		test.log(LogStatus.INFO, "Starting the test Gmail Login");
		openBrowser("Chrome");
		navigate("app_url");
		doLogin("nike.yogesh@gmail.com", "edcwsxqaz");
		test.log(LogStatus.INFO, "Opened the google.com");
	}
	
	
	@Test(groups = { "checkoutFlows2" })//,dataProvider = "CheckoutTests")
	public void yahoo_test(User sp) throws InterruptedException{
		
		test = rep.startTest("Test with screenshot");
		test.log(LogStatus.INFO, "Starting the yahoo.com");
		openBrowser("Chrome");
		navigate("app_url2");
		//doLogin("nike.yogesh@gmail.com", "edcwsxqaz");
		test.log(LogStatus.INFO, "Opened the yahoo.com");
		takeScreenShot();
	}
	
	@AfterMethod
	public void quit(){
		
		try{
			sft.assertAll();
		}catch(Error e){
			test.log(LogStatus.FAIL, e.getMessage());
		}
		rep.endTest(test);
		rep.flush();
		
		if(driver !=null){
			driver.quit();
		}
	}
}
