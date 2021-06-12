//$Id$

package com.saucedemo;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestLoginPositive {

	WebDriver driver;
	
	@BeforeMethod
	public void launchDriver() {
		BrowserDriverFactory factory = new BrowserDriverFactory();
		 this.driver = factory.browserFactory();
	}

	@Parameters({"posUserName","posPassword"})
	@Test
	public void postiveLoginTest(String userName, String password) throws Exception {
		String expectedURL = "https://www.saucedemo.com/inventory.html";
		login(userName,password);
		String actualURL = this.driver.getCurrentUrl();
		Assert.assertEquals(actualURL, expectedURL, "Login Failed");	
	}
	
	@Parameters({"negUserName","negPassword"})
	@Test
	
	public void lockedOutUserTest(String userName,String password) throws Exception{
		login(userName,password);
		WebElement lockedOutElement = this.driver.findElement(By.xpath("//div[@id='login_button_container']//form//h3"));
		String errorMessage = lockedOutElement.getText();
		Assert.assertTrue(errorMessage.contains("Epic sadface: Sorry, this user has been locked out."),"Not a locked out user");	
	}
	
	public void login(String userName,String password) throws Exception{
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement userNameelement = this.driver.findElement(By.id("user-name"));
		WebElement passwordElement = this.driver.findElement(By.id("password"));
		userNameelement.sendKeys(userName);
		passwordElement.sendKeys(password);
		WebElement buttonElement = driver.findElement(By.id("login-button"));
		wait.until(ExpectedConditions.elementToBeClickable(buttonElement));
		buttonElement.click();
	}
	
	@Parameters({"probUserName","probPassword"})
	@Test
	public void problemUserTest(String userName,String password) throws Exception{
		login(userName,password);
		WebElement outerImageElement = this.driver.findElement(By.xpath("/html//div[@id='inventory_container']/div/div[@id='inventory_container']/div/div[1]/div[@class='inventory_item_img']/a[@href='#']/img[@alt='Sauce Labs Backpack']"));
		String outerImage=outerImageElement.getAttribute("src");
		WebElement outerProductLink = this.driver.findElement(By.xpath("/html//div[@id='inventory_container']/div/div[@id='inventory_container']/div/div[1]/div[@class='inventory_item_description']/div[@class='inventory_item_label']/a[@href='#']/div[@class='inventory_item_name']"));
		outerProductLink.click();
		WebElement innerImageElement = this.driver.findElement(By.xpath("/html//div[@id='inventory_item_container']//img[@alt='Sauce Labs Fleece Jacket']"));
		String innerImage = innerImageElement.getAttribute("src");
		Thread.sleep(3000);
		Assert.assertNotEquals(outerImage, innerImage,"Not a problem user");
	}
	
	@Parameters({"perfUserName","perfPassword"})
	@Test
	public void performanceUserTest(String userName,String password) throws Exception{
		Long time = System.currentTimeMillis();
		login(userName, password);
		Long afterTime = System.currentTimeMillis(); 
		Assert.assertTrue(afterTime-time>=3000,"Not a Performance user");
	}
	
	@Parameters({"posUserName","posPassword"})
	@Test
	public void addOneProductToCart(String userName,String password) throws Exception{
		login(userName, password);
		WebElement productElement = this.driver.findElement(By.xpath("/html//div[@id='inventory_container']/div/div[@id='inventory_container']/div/div[1]/div[@class='inventory_item_description']/div[@class='inventory_item_label']/a[@href='#']/div[@class='inventory_item_name']"));
		String productName = productElement.getText();
		String price = this.driver.findElement(By.xpath("//*[@id=\"inventory_container\"]/div/div[1]/div[2]/div[2]/div")).getText();
		WebElement addToCartButton = this.driver.findElement(By.xpath("/html//button[@id='add-to-cart-sauce-labs-backpack']"));
		addToCartButton.click();
		WebElement removeButton = this.driver.findElement(By.xpath("/html//button[@id='remove-sauce-labs-backpack']"));
		Assert.assertTrue(removeButton!=null&&removeButton.getText().equals("REMOVE"),"Add cart button not changed to remove");
		WebElement cartLink = this.driver.findElement(By.xpath("//div[@id='shopping_cart_container']//span[@class='shopping_cart_badge']"));
		cartLink.click();
		WebElement cartProductElement = this.driver.findElement(By.xpath("//a[@id='item_4_title_link']/div[@class='inventory_item_name']"));
		String productNameInCart = cartProductElement.getText();
		Assert.assertEquals(productNameInCart,productName,"Add One Product to cart is not working");
		
		
		// checkout flow
		WebElement checkOut = this.driver.findElement(By.id("checkout"));
		checkOut.click();
		
		this.driver.findElement(By.id("first-name")).sendKeys("Mark");
		this.driver.findElement(By.id("last-name")).sendKeys("Antony");
		this.driver.findElement(By.id("postal-code")).sendKeys("600028");
		this.driver.findElement(By.id("continue")).click();
		Thread.sleep(2000);
		
		String checkOutItemName = this.driver.findElement(By.xpath("//a[@id='item_4_title_link']/div[@class='inventory_item_name']")).getText();
		Assert.assertEquals(checkOutItemName,productName,"Name mismatch in checkout view");

		
		String totalPrice = this.driver.findElement(By.xpath("/html//div[@id='checkout_summary_container']//div[@class='summary_subtotal_label']")).getText().replace("Item total: ", "");
		
		System.out.println(totalPrice);
		Assert.assertEquals(totalPrice, price,"Price mismatch");
		this.driver.findElement(By.id("finish")).click();
		
		
		String message = this.driver.findElement(By.xpath("//div[@id='checkout_complete_container']/h2[@class='complete-header']")).getText();
	
		Assert.assertTrue(message.contains("THANK YOU FOR YOUR ORDER"),"Checkout is not working");
		
		Thread.sleep(2000);
	}
	
	
	@Parameters({"posUserName","posPassword"})
	@Test
	
	public void addMultipleProducts(String userName,String password) throws Exception{
		login(userName,password);
		String productName1 = this.driver.findElement(By.xpath("//a[@id='item_4_title_link']/div[@class='inventory_item_name']")).getText();
		String productName2 = this.driver.findElement(By.xpath("/html//div[@id='inventory_container']/div/div[@id='inventory_container']/div/div[3]/div[@class='inventory_item_description']/div[@class='inventory_item_label']/a[@href='#']/div[@class='inventory_item_name']")).getText();
		String product1Price = this.driver.findElement(By.xpath("/html//div[@id='inventory_container']/div/div[@id='inventory_container']/div[@class='inventory_list']/div[1]/div[@class='inventory_item_description']/div[@class='pricebar']/div[@class='inventory_item_price']")).getText();
		String product2Price = this.driver.findElement(By.xpath("/html//div[@id='inventory_container']/div/div[@id='inventory_container']/div[@class='inventory_list']/div[3]/div[@class='inventory_item_description']/div[@class='pricebar']/div[@class='inventory_item_price']")).getText();
		
		this.driver.findElement(By.xpath("/html//button[@id='add-to-cart-sauce-labs-backpack']")).click();
		this.driver.findElement(By.xpath("/html//button[@id='add-to-cart-sauce-labs-bolt-t-shirt']")).click();
		String removeButton1 = this.driver.findElement(By.xpath("/html//button[@id='remove-sauce-labs-backpack']")).getText();
		Assert.assertTrue(removeButton1.equals("REMOVE"),"Add cart button not changed to remove");
		String removeButton2 = this.driver.findElement(By.xpath("/html//button[@id='remove-sauce-labs-bolt-t-shirt']")).getText();
		Assert.assertTrue(removeButton2.equals("REMOVE"),"Add cart button not changed to remove");
		
		String noOfItem = this.driver.findElement(By.xpath("//*[@id=\"shopping_cart_container\"]/a/span")).getText();
		System.out.println(noOfItem);
		Assert.assertEquals("2", noOfItem,"Cart number mistach");
		System.out.println("Tow items added in cart");
		
		WebElement cartLink = this.driver.findElement(By.xpath("//div[@id='shopping_cart_container']//span[@class='shopping_cart_badge']"));
		cartLink.click();
		
		
		String cartProductName1 = this.driver.findElement(By.xpath("/html//div[@id='cart_contents_container']//div[@class='cart_list']/div[3]/div[@class='cart_item_label']/a[@href='#']/div[@class='inventory_item_name']")).getText();
		String cartProductName2 = this.driver.findElement(By.xpath("/html//div[@id='cart_contents_container']//div[@class='cart_list']/div[4]/div[@class='cart_item_label']/a[@href='#']/div[@class='inventory_item_name']")).getText();
		Assert.assertEquals(cartProductName1, productName1,"Product missing in cart");
		Assert.assertEquals(cartProductName2,productName2, "Product missing in cart");
		
		
		WebElement checkOut = this.driver.findElement(By.id("checkout"));
		checkOut.click();
		
		this.driver.findElement(By.id("first-name")).sendKeys("Mark");
		this.driver.findElement(By.id("last-name")).sendKeys("Antony");
		this.driver.findElement(By.id("postal-code")).sendKeys("600028");
		this.driver.findElement(By.id("continue")).click();
		Thread.sleep(2000);
		
		String totalPrice = this.driver.findElement(By.xpath("/html//div[@id='checkout_summary_container']//div[@class='summary_subtotal_label']")).getText().replace("Item total: ", "");
		System.out.println(totalPrice);
		Double total = Double.parseDouble(product1Price.replace("$", ""))+Double.parseDouble(product2Price.replace("$", ""));
		System.out.println("Total:: "+total);
		Assert.assertEquals(totalPrice,"$"+total, "Price mismatch in checkout View");
		
		this.driver.findElement(By.id("finish")).click();
		
		
		String message = this.driver.findElement(By.xpath("//div[@id='checkout_complete_container']/h2[@class='complete-header']")).getText();
	
		Assert.assertTrue(message.contains("THANK YOU FOR YOUR ORDER"),"Checkout is not working");
		
		Thread.sleep(2000);	
	}

	
	@AfterMethod
	public void shutDownDriver() {
		this.driver.quit();
	}

}
