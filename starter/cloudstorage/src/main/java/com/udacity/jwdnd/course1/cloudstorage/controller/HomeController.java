package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
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
    private UserService userService;

    public HomeController(NotesService notesService, UserService userService) {
        this.notesService = notesService;
        this.userService = userService;
    }

    @RequestMapping("/home")
    public String getHomePage(Model model) {
        List<Note> notes = notesService.getNotesForUser(getCurrentUserName());

        Note note;
        if (notes == null || notes.size() == 0) {
            note = new Note();
        } else {
            note = notes.get(0);
        }

        model.addAttribute("note", note);
        model.addAttribute("notes", notes);
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
        notesService.deleteNote(id);
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
