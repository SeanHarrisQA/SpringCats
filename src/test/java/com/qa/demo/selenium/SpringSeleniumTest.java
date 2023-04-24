package com.qa.demo.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@Sql(scripts = { "classpath:cat-schema.sql",
		"classpath:cat-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringSeleniumTest {

	private WebDriver driver;

	@LocalServerPort
	private int port;

	private WebDriverWait wait;

	@BeforeEach
	void init() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		this.driver = new ChromeDriver(options);
		this.driver.manage().window().maximize();
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
	}

	@AfterEach
	void tearDown() {
		this.driver.close();
	}

	@Test
	void testTitle() throws InterruptedException {
		// Throw exceptions if using Thread.sleep
		this.driver.get("http://localhost:" + port + "/");
		Thread.sleep(Duration.ofSeconds(3));
		WebElement title = this.driver.findElement(By.cssSelector("body > header > h1"));

		assertTrue(title.getText().contains("CATS"));
	}

	@Test
	void testGetAll() {
		this.driver.get("http://localhost:" + port + "/");
		WebElement card = this.wait
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#output > div > div")));
		assertTrue(card.getText().contains("Mr Bigglesworth"));
	}

	@Test
	void testDelete() throws InterruptedException {
		this.driver.get("http://localhost:" + port + "/");
		WebElement button = this.wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector("#output > div:nth-child(1) > div > div > button:nth-child(6)")));
		button.click();
		// Thread.sleep(Duration.ofSeconds(3));
		this.wait.until(ExpectedConditions.not(ExpectedConditions
				.elementToBeClickable(By.cssSelector("#output > div:nth-child(1) > div > div > button:nth-child(6)"))));
		List<WebElement> cards = driver.findElements(By.className("card"));
		assertTrue(cards.isEmpty());
	}

	@Test
	void testCreate() {
		this.driver.get("http://localhost:" + port + "/");
		WebElement name = this.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#catName")));
		name.sendKeys("Manny");
		WebElement length = this.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#catLength")));
		length.sendKeys("26");
		name.sendKeys(Keys.ENTER);
		WebElement card = this.wait.until(
				ExpectedConditions.elementToBeClickable(By.cssSelector("#output > div:nth-child(2) > div > div")));
		assertTrue(card.getText().contains("Manny"));
	}

	@Test
	void testUpdate() {
		this.driver.get("http://localhost:" + port + "/");
		WebElement update = this.wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector("#output > div:nth-child(1) > div > div > button:nth-child(5)")));
		update.click();
		WebElement submit = this.wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector("#updateForm > div.mt-3 > button.btn.btn-success")));
		WebElement name = this.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#catName")));
		name.sendKeys("Wagwan kitty");
		submit.click();
		WebElement card = this.wait.until(
				ExpectedConditions.elementToBeClickable(By.cssSelector("#output > div:nth-child(1) > div > div")));
		assertTrue(card.getText().contains("Wagwan kitty"));
	}

}
