package com.udacity.jwdnd.course1.cloudstorage.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CredentialsTab {
    private WebDriver webDriver;

    @FindBy(id = "addNewCredentialButton")
    WebElement addNewCredentialButton;

    @FindBy(id = "credential-url")
    WebElement credentialUrl;

    @FindBy(id = "credential-username")
    WebElement credentialUsername;

    @FindBy(id = "credential-password")
    WebElement credentialPassword;

    @FindBy(id = "credentialSubmitButton")
    WebElement credentialSubmitButton;

    public CredentialsTab(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(this.webDriver, this);
    }

    public void addNewCredential(String credentialUrl, String credentialUsername, String credentialPassword) {
        addNewCredentialButton.click();
        this.credentialUrl.sendKeys(credentialUrl);
        this.credentialUsername.sendKeys(credentialUsername);
        this.credentialPassword.sendKeys(credentialPassword);
        credentialSubmitButton.click();
    }

    public void editCredential(String xpath, String updatedUrl, String updatedUsername, String updatedPassword) {
        WebElement editButton = webDriver.findElement(By.xpath(xpath));
        editButton.click();
        credentialUrl.clear();
        credentialUrl.sendKeys(updatedUrl);
        credentialUsername.clear();
        credentialUsername.sendKeys(updatedUsername);
        credentialPassword.clear();
        credentialPassword.sendKeys(updatedPassword);
        credentialSubmitButton.click();
    }

    public void deleteCredential(String xpath){
        WebElement deleteButton = webDriver.findElement(By.xpath(xpath));
        deleteButton.click();
    }
}

