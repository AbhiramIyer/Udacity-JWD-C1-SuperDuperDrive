package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import com.udacity.jwdnd.course1.cloudstorage.services.NotesService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    private NotesService notesService;
    private CredentialsService credentialsService;
    private UserService userService;

    public HomeController(NotesService notesService, CredentialsService credentialsService, UserService userService) {
        this.notesService = notesService;
        this.credentialsService = credentialsService;
        this.userService = userService;
    }

    @RequestMapping("/home")
    public String getHomePage(Model model) {
        String currentUserName = getCurrentUserName();
        List<Note> notes = notesService.getAllNotesByUser(currentUserName);
        List<Credential> credentials = credentialsService.getAllCredentialsByUser(currentUserName);

        model.addAttribute("note", new Note());
        model.addAttribute("notes", notes);
        model.addAttribute("credential", new Credential());
        model.addAttribute("credentials", credentials);

        return "home";
    }

    @PostMapping("/notes")
    public String addOrUpdateNote(@ModelAttribute("note") Note note, Model model) {
        if (note.getNoteId() == null) {
            note.setOwnerUserId(userService.getUser(getCurrentUserName()).getUserId());
            notesService.addNewNote(note);
        } else {
            notesService.updateNote(note);
        }
        return "redirect:/home";
    }

    /* Using GET even though I suppose I should be using POST instead for state modification
    *  Need to modify the Delete button in home.html from link to form
    * */
    @GetMapping("/notes/delete/{id}")
    public String deleteNote(@PathVariable Integer id) {
        notesService.deleteNoteById(id);
        return "redirect:/home";
    }

    @PostMapping("/credentials")
    public String addOrUpdateCredential(@ModelAttribute("credential")Credential credential, Model model) {
        if (credential.getCredentialId() == null) {
            credential.setOwnerUserId(userService.getUser(getCurrentUserName()).getUserId());
            credentialsService.addNewCredential(credential);
        } else {
            credentialsService.updateCredential(credential);
        }
        return "redirect:/home";
    }

    @GetMapping("/credentials/delete/{id}")
    public String deleteCredential(@PathVariable Integer id) {
        credentialsService.deleteCredentialById(id);
        return "redirect:/home";
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return null;
    }
}
