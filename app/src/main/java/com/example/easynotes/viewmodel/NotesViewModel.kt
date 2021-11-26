package com.example.easynotes.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.easynotes.NotesDbOperation

import com.example.easynotes.data.Note
import com.example.easynotes.database.DbHelper
import com.example.easynotes.interfaces.ViewModelListener
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

    fun updateNote(note: Note,viewModelListener: ViewModelListener) {

        val update = dbHelper.updateNotes(note)

        viewModelListener.success(update,NotesDbOperation.UPDATE)
    }

    fun deleteNote(id : Int,viewModelListener: ViewModelListener) {
      val delete =   dbHelper.deleteNotes(id)
        viewModelListener.success(delete,NotesDbOperation.DELETE)

    }

    fun addNote(note: Note,viewModelListener: ViewModelListener)  {

        val add = dbHelper.addNotes(note)

        viewModelListener.success(add,NotesDbOperation.ADD)

    }



}