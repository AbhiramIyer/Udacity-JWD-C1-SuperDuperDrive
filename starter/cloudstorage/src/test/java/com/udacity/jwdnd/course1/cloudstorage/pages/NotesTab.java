package com.udacity.jwdnd.course1.cloudstorage.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class NotesTab {
    private WebDriver webDriver;

    @FindBy(id = "addNewNoteButton")
    WebElement addNewNoteButton;

    @FindBy(id = "note-title")
    WebElement noteTitle;

    @FindBy(id = "note-description")
    WebElement noteDescription;

    @FindBy(id = "noteSubmitButton")
    WebElement noteSubmitButton;

    public NotesTab(WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(this.webDriver, this);
    }

    public void addNewNote(String noteTitle, String noteDescription) {
        addNewNoteButton.click();
        this.noteTitle.sendKeys(noteTitle);
        this.noteDescription.sendKeys(noteDescription);
        noteSubmitButton.click();
    }

    public void editNote(String xpath, String updatedTitle, String updatedContent) {
        WebElement editButton = webDriver.findElement(By.xpath(xpath));
        editButton.click();
        noteTitle.clear();
        noteTitle.sendKeys(updatedTitle);
        noteDescription.clear();
        noteDescription.sendKeys(updatedContent);
        noteSubmitButton.click();
    }

    public void deleteNote(String xpath){
        WebElement deleteButton = webDriver.findElement(By.xpath(xpath));
        deleteButton.click();
    }
}
