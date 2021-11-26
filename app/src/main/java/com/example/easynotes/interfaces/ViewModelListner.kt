package com.example.easynotes.interfaces

import com.example.easynotes.NotesDbOperation

interface ViewModelListener {
    fun success(result: Boolean,operation : NotesDbOperation)
}