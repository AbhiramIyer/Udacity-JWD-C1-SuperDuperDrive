package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {
    @Select("SELECT * FROM NOTES WHERE userid=#{userId}")
    List<Note> getAllNotesByUserId(int userId);

    @Insert("INSERT INTO NOTES (noteTitle, noteDescription, userid) VALUES(#{noteTitle}, #{noteDescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    int insertNote(Note note);

    @Update("UPDATE NOTES SET noteTitle=#{noteTitle}, noteDescription=#{noteDescription} WHERE noteId=#{noteId}")
    void updateNote(Note note);

    @Delete("DELETE FROM NOTES WHERE noteId=#{noteId}")
    void deleteNoteById(int noteId);
}

