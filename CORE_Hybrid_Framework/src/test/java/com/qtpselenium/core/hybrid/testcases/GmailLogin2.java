//package com.qtpselenium.core.hybrid.testcases;
//
//import java.lang.reflect.Method;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.concurrent.TimeUnit;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.testng.ITestContext;
//import org.testng.Reporter;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import com.ebay.data.utils.spreadsheet.EasyFilter;
//import com.ebay.data.utils.spreadsheet.SpreadSheetUtil;
//import com.ebay.data.utils.spreadsheet.TestObject;
//import com.qtpselenium.core.hybrid.util.BaseTest;
//import com.qtpselenium.core.hybrid.util.ExtentManager;
//import com.qtpselenium.core.hybrid.util.GenericKeywords;
//import com.qtpselenium.core.hybrid.util.User;
//import com.relevantcodes.extentreports.ExtentReports;
//import com.relevantcodes.extentreports.ExtentTest;
//import com.relevantcodes.extentreports.LogStatus;
//
//
//public class GmailLogin2 extends BaseTest{
//	
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
////		classMap.put("ShippingAddressImpl", ShippingAddressImpl.class);
////		classMap.put("CheckoutFlow", String.class);
////		classMap.put("status", Integer.class);
////		classMap.put("errordata", com.ebay.marketplaces.checkout.copr2.data.CheckoutErrorMessage.class);
////		classMap.put("CreditCard", CreditCard.class);
////		classMap.put("BillingAddressImpl", BillingAddressImpl.class);
////		classMap.put("UpdateShipAddress", ShippingAddressImpl.class);
//		return SpreadSheetUtil.getEntitiesFromSpreadsheet(this.getClass(), classMap, "checkout.xls", 0, null, null);
//	}
//	@Test(groups = { "checkoutFlows2" },dataProvider = "CheckoutTests")
//	public void doLogin(User sp) throws InterruptedException{
//		
//		test = rep.startTest("Test WITHOUT screenshot");
//		
//		test.log(LogStatus.INFO, "Starting the test Gmail Login");
//		openBrowser("Chrome");
//		navigate("app_url");
//		test.log(LogStatus.INFO, "Opened the google.com");
//		click("gmailLink_xpath");
//		click("signin_xpath");
//		type("emailID_txt_xpath", "nike.yogesh@gmail.com");
//		
//	}
//	
//	@AfterMethod
//	public void quit(){
//		rep.endTest(test);
//		rep.flush();
//	}
//}
