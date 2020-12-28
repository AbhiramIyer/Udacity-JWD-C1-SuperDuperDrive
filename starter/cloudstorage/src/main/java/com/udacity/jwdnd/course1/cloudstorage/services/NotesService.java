package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesService {
    private NoteMapper mapper;
    private UserService userService;

    public NotesService(NoteMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    public List<Note> getAllNotesByUser(String username) {
        User user = userService.getUser(username);
        return mapper.getAllNotesByUserId(user.getUserId());
    }

    public int addNewNote(Note note) {
        Note newNote = new Note(null, note.getNoteTitle(), note.getNoteDescription(), note.getOwnerUserId());
        return mapper.insertNote(newNote);
    }

    public void updateNote(Note note) {
        mapper.updateNote(note);
    }

    public void deleteNoteById(Integer noteId) {
        mapper.deleteNote(noteId);
    }
}
