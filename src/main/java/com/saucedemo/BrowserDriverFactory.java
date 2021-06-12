package com.saucedemo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BrowserDriverFactory {


	public BrowserDriverFactory() {
		
	}
	public WebDriver browserFactory() {
		System.setProperty("webdriver.chrome.driver", "/Users/prabu/Documents/eclipse21/test-automation/src/main/resources/chromedriver");
		WebDriver driver = new ChromeDriver();
		String url = "https://www.saucedemo.com/";
		driver.get(url);
		return driver;
	}

}
