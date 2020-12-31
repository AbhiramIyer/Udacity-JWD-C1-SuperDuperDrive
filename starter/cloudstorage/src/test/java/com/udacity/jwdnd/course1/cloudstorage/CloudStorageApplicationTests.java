package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.pages.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
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
		WebDriverManager.firefoxdriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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

	@Test
	public void loggedInUserCanAddANoteAndVerifyIt() {
		doSignup("John", "Doe", "johndoe", "badpassword");
		dologin("johndoe", "badpassword");

		driver.get(HTTP_LOCALHOST + ":" + this.port + "/home");
		WebElement noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();
		NotesTab nt = new NotesTab(driver);
		String testNoteTitle = "Testing Testing";
		String testNoteDescription = "Test all the things";
		nt.addNewNote( testNoteTitle, testNoteDescription);

		noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();

		WebElement noteTitle = driver.findElement(By.xpath("//*[@id=\"userTable\"]/tbody/tr/th"));
		WebElement noteDescription = driver.findElement(By.xpath("//*[@id=\"userTable\"]/tbody/tr/td[2]"));
		Assertions.assertEquals( testNoteTitle, noteTitle.getText());
		Assertions.assertEquals(testNoteDescription, noteDescription.getText());
	}

	@Test
	public void loggedInUserCanEditAnExistingNoteAndVerifyIt() {
		doSignup("John", "Doe", "johndoe", "badpassword");
		dologin("johndoe", "badpassword");

		driver.get(HTTP_LOCALHOST + ":" + this.port + "/home");

		WebElement noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();
		NotesTab nt = new NotesTab(driver);
		nt.addNewNote("Original Title", "Original Content");

		noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();
		nt = new NotesTab(driver);
		String updated_title = "Updated Title";
		String updated_content = "Updated Content";
		nt.editNote("//*[@id=\"userTable\"]/tbody/tr/td[1]/button", updated_title, updated_content);

		noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();

		WebElement noteTitle = driver.findElement(By.xpath("//*[@id=\"userTable\"]/tbody/tr/th"));
		WebElement noteDescription = driver.findElement(By.xpath("//*[@id=\"userTable\"]/tbody/tr/td[2]"));
		Assertions.assertEquals(updated_title, noteTitle.getText());
		Assertions.assertEquals(updated_content, noteDescription.getText());
	}

	@Test
	public void loggedInUserCanDeleteANoteAndVerifyIt() {
		doSignup("John", "Doe", "johndoe", "badpassword");
		dologin("johndoe", "badpassword");

		driver.get(HTTP_LOCALHOST + ":" + this.port + "/home");

		WebElement noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();
		NotesTab nt = new NotesTab(driver);
		nt.addNewNote("Original Title", "Original Content");

		noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();
		nt = new NotesTab(driver);
		nt.deleteNote("//*[@id=\"userTable\"]/tbody/tr/td[1]/a");

		noteTab = driver.findElement(By.id("nav-notes-tab"));
		noteTab.click();

		WebElement noteTitle = driver.findElement(By.xpath("//*[@id=\"userTable\"]/tbody/tr/th"));
		WebElement noteDescription = driver.findElement(By.xpath("//*[@id=\"userTable\"]/tbody/tr/td[2]"));
		Assertions.assertEquals("Example Note Title", noteTitle.getText());
		Assertions.assertEquals("Example Note Description", noteDescription.getText());

	}

	@Test
	public void loggedInUserCanAddACredentialAndVerifyIt() {
		doSignup("John", "Doe", "johndoe", "badpassword");
		dologin("johndoe", "badpassword");

		driver.get(HTTP_LOCALHOST + ":" + this.port + "/home");
		WebElement credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();
		CredentialsTab ct = new CredentialsTab(driver);
		String testUrl = "Testing.com";
		String testUsername = "breaker1";
		String testPassword = "break123";
		ct.addNewCredential( testUrl, testUsername, testPassword);

		credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();

		WebElement resultUrl = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/th"));
		WebElement resultUsername = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[2]"));
		WebElement resultEncryptedPassword = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[3]"));
		Assertions.assertEquals(testUrl, resultUrl.getText());
		Assertions.assertEquals(testUsername, resultUsername.getText());
		Assertions.assertNotEquals(testPassword, resultEncryptedPassword.getText());
	}

	@Test
	public void loggedInUserCanEditACredentialAndVerifyIt() {
		doSignup("John", "Doe", "johndoe", "badpassword");
		dologin("johndoe", "badpassword");

		driver.get(HTTP_LOCALHOST + ":" + this.port + "/home");
		WebElement credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();
		CredentialsTab ct = new CredentialsTab(driver);
		String testUrl = "Testing.com";
		String testUsername = "breaker1";
		String testPassword = "break123";
		ct.addNewCredential( testUrl, testUsername, testPassword);

		credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();
		ct = new CredentialsTab(driver);
		String updatedUrl = "Testing2.com";
		String updatedUsername = "breaker2";
		String updatedPassword = "break12345";
		ct.editCredential("//*[@id=\"credentialTable\"]/tbody/tr/td[1]/button", updatedUrl, updatedUsername, updatedPassword);

		credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();

		WebElement resultUrl = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/th"));
		WebElement resultUsername = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[2]"));
		WebElement resultEncryptedPassword = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[3]"));
		Assertions.assertEquals(updatedUrl, resultUrl.getText());
		Assertions.assertEquals(updatedUsername, resultUsername.getText());
		Assertions.assertNotEquals(updatedPassword, resultEncryptedPassword.getText());
	}

	@Test
	public void loggedInUserCanDeleteACredentialAndVerifyIt() {
		doSignup("John", "Doe", "johndoe", "badpassword");
		dologin("johndoe", "badpassword");

		driver.get(HTTP_LOCALHOST + ":" + this.port + "/home");
		WebElement credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();
		CredentialsTab ct = new CredentialsTab(driver);
		String testUrl = "Testing.com";
		String testUsername = "breaker1";
		String testPassword = "break123";
		ct.addNewCredential( testUrl, testUsername, testPassword);

		credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();
		ct.deleteCredential("//*[@id=\"credentialTable\"]/tbody/tr/td[1]/a");

		credentialTab = driver.findElement(By.id("nav-credentials-tab"));
		credentialTab.click();

		WebElement resultUrl = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/th"));
		WebElement resultUsername = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[2]"));
		WebElement resultEncryptedPassword = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[3]"));
		Assertions.assertEquals("Example Credential URL", resultUrl.getText());
		Assertions.assertEquals("Example Credential Username", resultUsername.getText());
		Assertions.assertEquals("Example Credential Password", resultEncryptedPassword.getText());
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
