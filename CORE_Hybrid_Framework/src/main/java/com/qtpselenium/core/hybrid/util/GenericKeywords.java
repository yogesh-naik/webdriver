package com.qtpselenium.core.hybrid.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class GenericKeywords {
	
	public WebDriver driver;
	public Properties prop;
	
	 public GenericKeywords(){
		prop = new Properties();
		try {
			System.out.println("HERE -->"+(System.getProperty("user.dir")));
			FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resourcs//project.properties");
			prop.load(fs);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void openBrowser(String browserType){		
		
		if(browserType.equals("Mozilla"))
			driver=new FirefoxDriver();
		else if(browserType.equals("IE"))
			driver=new InternetExplorerDriver();
		else if(browserType.equals("Chrome"))
			driver=new ChromeDriver();
		
		//long implicitWaitTime=Long.parseLong(CONFIG.getProperty("implicitwait"));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	public void navigate(String url){
		driver.get(prop.getProperty(url));
	}

	public void click(String locator){
		if(locator.endsWith("id"))
			driver.findElement(By.id(prop.getProperty(locator))).click();
		if(locator.endsWith("xpath"))
			driver.findElement(By.xpath(prop.getProperty(locator))).click();
		if(locator.endsWith("link"))
			driver.findElement(By.name(prop.getProperty(locator))).click();
	}
	
	public void txtinput(String locator ,String data){
		
	}
	
public void verifyInput(String locator ,String expectedText){
		
	}
}
