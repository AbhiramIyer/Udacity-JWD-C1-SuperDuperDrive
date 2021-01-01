package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileStorageService;
import com.udacity.jwdnd.course1.cloudstorage.services.NotesService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class HomeController {

    private NotesService notesService;
    private CredentialsService credentialsService;
    private UserService userService;
    private FileStorageService fileStorageService;

    public HomeController(NotesService notesService, CredentialsService credentialsService, UserService userService, FileStorageService fileStorageService) {
        this.notesService = notesService;
        this.credentialsService = credentialsService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @RequestMapping({"/","/home"})
    public String getHomePage(Model model) {
        String currentUserName = getCurrentUserName();
        List<Note> notes = notesService.getAllNotesByUser(currentUserName);
        List<Credential> credentials = credentialsService.getAllCredentialsByUser(currentUserName);
        List<File> files = fileStorageService.getAllFilesByUser(currentUserName);

        model.addAttribute("note", new Note());
        model.addAttribute("notes", notes);
        model.addAttribute("credential", new Credential());
        model.addAttribute("credentials", credentials);
        model.addAttribute("files", files);

        return "home";
    }

    @PostMapping("/notes")
    public String addOrUpdateNote(@ModelAttribute("note") Note note, Model model, RedirectAttributes redirectAttributes) {
        String flashMessage;
        if (note.getNoteId() == null) {
            note.setUserId(userService.getUser(getCurrentUserName()).getUserId());
            notesService.addNewNote(note);
            flashMessage = "Note created successfully!";
        } else {
            notesService.updateNote(note);
            flashMessage = "Note saved successfully!";
        }
        redirectAttributes.addFlashAttribute("flashMessage", flashMessage);
        return "redirect:/home";
    }

    /* Using GET even though I suppose I should be using POST instead for state modification
    *  Need to modify the Delete button in home.html from link to form
    * */
    @GetMapping("/notes/delete/{id}")
    public String deleteNote(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        notesService.deleteNoteById(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Note deleted successfully!");
        return "redirect:/home";
    }

    @PostMapping("/credentials")
    public String addOrUpdateCredential(@ModelAttribute("credential")Credential credential, Model model, RedirectAttributes redirectAttributes) {
        String flashMessage;
        if (credential.getCredentialId() == null) {
            credential.setUserId(userService.getUser(getCurrentUserName()).getUserId());
            credentialsService.addNewCredential(credential);
            flashMessage = "Credential created successfully!";
        } else {
            credentialsService.updateCredential(credential);
            flashMessage = "Credential updated successfully!";
        }
        redirectAttributes.addFlashAttribute("flashMessage", flashMessage);
        return "redirect:/home";
    }

    @GetMapping("/credentials/delete/{id}")
    public String deleteCredential(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        credentialsService.deleteCredentialById(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Credential deleted successfully!");
        return "redirect:/home";
    }

    @PostMapping("/file-upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload, Model model, RedirectAttributes redirectAttributes) throws IOException {
        String fileName = fileUpload.getOriginalFilename();
        String flashMessage;
        if (StringUtils.isEmpty(fileName)) {
            flashMessage = "Select a file to upload by clicking the choose button";
        }else if(fileStorageService.fileAlreadyExists(fileName)) {
            flashMessage = "File already exists!";
        } else {
            File newFile = new File();
            newFile.setFileName(fileUpload.getOriginalFilename());
            newFile.setContentType(fileUpload.getContentType());
            newFile.setFileSize(Long.toString(fileUpload.getSize()));
            newFile.setUserId(userService.getUser(getCurrentUserName()).getUserId());
            newFile.setFileData(fileUpload.getBytes());
            fileStorageService.uploadNewFile(newFile);
            flashMessage = "File uploaded successfully!";
        }

        redirectAttributes.addFlashAttribute("flashMessage", flashMessage);
        return "redirect:/home";
    }

    @GetMapping("/files/delete/{id}")
    public String deleteFile(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        fileStorageService.deleteFileById(id);
        redirectAttributes.addFlashAttribute("flashMessage", "File deleted successfully!");
        return "redirect:/home";
    }

    @GetMapping("/files/{id}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable Integer id) {
        // https://spring.io/guides/gs/uploading-files/
        File file = fileStorageService.getFileById(id);
        Resource fileResource = new ByteArrayResource(file.getFileData());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" +  file.getFileName() + "\"").body(fileResource);
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }
}
