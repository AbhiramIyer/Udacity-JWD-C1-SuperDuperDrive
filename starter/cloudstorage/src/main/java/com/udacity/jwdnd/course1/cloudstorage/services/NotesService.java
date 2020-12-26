package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesService {
    private NoteMapper noteMapper;
    private UserService userService;

    public NotesService(NoteMapper noteMapper, UserService userService) {
        this.noteMapper = noteMapper;
        this.userService = userService;
    }

    public List<Note> getNotesForUser(String username) {
        User user = userService.getUser(username);
        return noteMapper.getUserNotes(user.getUserId());
    }

    public int addNewNote(Note newNote) {
        return noteMapper.insertNote(new Note(null, newNote.getNoteTitle(), newNote.getNoteDescription(), newNote.getOwnerUserId()));
    }

    public void updateNote(Note note) {
        noteMapper.updateNote(note);
    }

    public void deleteNote(Integer noteId) {
        noteMapper.deleteNote(noteId);
    }
}
