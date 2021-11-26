package com.example.easynotes.database

import android.provider.BaseColumns

object NotesTable : BaseColumns {


    const val TABLE_NAME = "NotesList"
    const val ID = "id"
    const val TITLE = "title"
    const val CONTENT = "content"
    const val PASSWORD_PROTECTED = "passwordProtected"
    const val PINNED = "pinned"
    const val DATE_TIME = "dateTime"
    const val CREATED_DATE = "createDate"


}