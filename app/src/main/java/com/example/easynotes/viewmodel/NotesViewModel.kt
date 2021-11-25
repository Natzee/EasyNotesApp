package com.example.easynotes.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.example.easynotes.data.Note
import com.example.easynotes.database.DbHelper
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private var noteLiveList : MutableLiveData<MutableList<Note>> = MutableLiveData()
    private var dbHelper :DbHelper = DbHelper(application,null)


    init {
        getNotesFromDb()


    }

   private  fun getNotesFromDb() {

       viewModelScope.launch {
           noteLiveList.value = dbHelper.getNotes()
       }

    }


    fun getNotes(): LiveData<MutableList<Note>> {
        return noteLiveList
    }

    fun updateNote(note: Note) = dbHelper.updateNotes(note)

    fun deleteNote(id : Int) = dbHelper.deleteNotes(id)

    fun addNote(note: Note) = dbHelper.addNotes(note)



}