package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.pages.HomePage;
import com.udacity.jwdnd.course1.cloudstorage.pages.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.pages.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//
// Using DirtiesContext and AutoConfigureTestDatabase to reset the database after each test
//
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private static final String HTTP_LOCALHOST = "http://localhost";

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void anyoneCanSeeLoginPage() {
		driver.get(HTTP_LOCALHOST + ":" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	public void anyoneCanSeeSignupPage() {
		driver.get(HTTP_LOCALHOST + ":" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}

	@Test
	public void newUserCanSignUpAndSeeHomePageOnSuccessfulLoginAndCannotAccessHomeAfterLogout() {
		doSignup("John", "Doe", "johndoe", "badpassword");
		WebElement signupSuccessfulDiv = new WebDriverWait(driver, 5).until(driver -> driver.findElement(By.id("signupSuccessfulDiv")));
		Assertions.assertTrue(signupSuccessfulDiv.isDisplayed());

		dologin("johndoe", "badpassword");

		HomePage hp = new HomePage(driver);
		Assertions.assertEquals("Home", driver.getTitle());
		WebElement logoutButton = new WebDriverWait(driver, 5).until(driver -> driver.findElement(By.id("logoutButton")));
		Assertions.assertTrue(logoutButton.isDisplayed());

		hp.logout();
		WebElement logoutStatusDiv = new WebDriverWait(driver, 5).until(driver -> driver.findElement(By.id("logoutStatusDiv")));
		Assertions.assertTrue(logoutStatusDiv.isDisplayed());

		driver.get(HTTP_LOCALHOST + ":" + this.port + "/home");
		// should not be able to access Home. Should be redirected to Login
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	public void userCannotSignupIfUsernameIsAlreadyTaken() {
		// Claim a user id
		doSignup("John", "Doe", "johndoe", "badpassword");
		WebElement signupSuccessfulDiv = new WebDriverWait(driver, 5).until(driver -> driver.findElement(By.id("signupSuccessfulDiv")));
		Assertions.assertTrue(signupSuccessfulDiv.isDisplayed());

		// Try to signup again with same user name
		doSignup("John", "Another Doe", "johndoe", "somepassword");
		WebElement signupErrorDiv = new WebDriverWait(driver, 5).until(driver -> driver.findElement(By.id("signupErrorDiv")));
		Assertions.assertTrue(signupErrorDiv.isDisplayed());
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "/", "/home", "/notes", "/notes/delete/1", "/credentials", "/credentials/delete/1", "/files", "/files/delete/1", "/abcd", "/logout", "/error"})
	public void unregisteredUserIsAlwaysRedirectedFromOtherPathsToLogin(String path) {
		driver.get(HTTP_LOCALHOST + ":" + this.port + path);
		Assertions.assertEquals("Login", driver.getTitle());
	}

	private SignupPage doSignup(String firstName, String lastName, String username, String password) {
		driver.get(HTTP_LOCALHOST + ":" + this.port + "/signup");
		SignupPage sp = new SignupPage(driver);
		sp.signup(firstName, lastName, username, password);
		return sp;
	}

	private LoginPage dologin(String username, String password) {
		driver.get(HTTP_LOCALHOST + ":" + this.port + "/login");
		LoginPage lp = new LoginPage(driver);
		lp.login(username, password);
		return lp;
	}

}
