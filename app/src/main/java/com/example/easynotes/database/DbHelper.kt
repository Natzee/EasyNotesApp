package com.example.easynotes.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.example.easynotes.data.Note
import com.example.easynotes.database.DatabaseContract.NotesTable

class DbHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(
    context,
    DB_NAME, factory,
    DB_VERSION
) {

    companion object {
        const val DB_NAME: String = "NotesTable.db"

        const val DB_VERSION: Int = 2


    }

    override fun onCreate(db: SQLiteDatabase?) {

        val notesTable =
            "CREATE TABLE ${NotesTable.TABLE_NAME}(${NotesTable.ID} INTEGER PRIMARY KEY AUTOINCREMENT ,${NotesTable.TITLE} TEXT NOT NULL,${NotesTable.CONTENT} TEXT NOT NULL,${NotesTable.PASSWORD_PROTECTED} INTEGER NOT NULL,${NotesTable.PINNED} INTEGER NOT NULL,${NotesTable.DATE_TIME} TEXT NOT NULL ,${NotesTable.CREATED_DATE} TEXT NOT NULL)"
        db!!.execSQL(notesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${NotesTable.TABLE_NAME}")
    }

    fun addNotes(note: Note) : Boolean{

        val value = contentValuesOf()
        val db = this.writableDatabase


        value.put(NotesTable.TITLE, note.title)
        value.put(NotesTable.CONTENT, note.notes)
        value.put(NotesTable.PASSWORD_PROTECTED, note.isPasswordProtected.toInt())
        value.put(NotesTable.PINNED, note.isPinned.toInt())
        value.put(NotesTable.DATE_TIME, "${note.lastEdited}")
        value.put(NotesTable.CREATED_DATE,"${note.createdDate}")

        val getId = db.insert(NotesTable.TABLE_NAME, null, value)

       note.id = getId.toInt()

        return getId>0

    }

    fun updateNotes(note: Note) : Boolean {
        val db = this.writableDatabase
        val value = contentValuesOf()
        value.put(NotesTable.TITLE, note.title)
        value.put(NotesTable.CONTENT, note.notes)
        value.put(NotesTable.PASSWORD_PROTECTED, note.isPasswordProtected.toInt())
        value.put(NotesTable.PINNED, note.isPinned.toInt())
        value.put(NotesTable.DATE_TIME, "${note.lastEdited}")
       return db.update(NotesTable.TABLE_NAME, value, "${NotesTable.ID}=?", arrayOf(note.id.toString())) > 0
    }

    fun deleteNotes(id: Int) :Boolean {
        val db = this.writableDatabase
       return db.delete(NotesTable.TABLE_NAME, "${NotesTable.ID}=?", arrayOf(id.toString())) > 0
    }

    fun getNotes(): ArrayList<Note> {

        val db = writableDatabase
        val tempNotesList = ArrayList<Note>()


        val query = "SELECT * FROM ${NotesTable.TABLE_NAME} ORDER BY ${NotesTable.PINNED} = 1 DESC"
        val cursor = db.rawQuery(query, null)


        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val content = cursor.getString(2)
                val passwordProtected = cursor.getInt(3).toBoolean()
                val pinned = cursor.getInt(4).toBoolean()
                val dateTime = cursor.getString(5)
                val createDate = cursor.getString(6)

                val note = Note(id, title, content,dateTime.toLong(), createDate.toLong() ,pinned, passwordProtected)
                tempNotesList.add(note)

            } while (cursor.moveToNext())
        }
        cursor.close()
        return tempNotesList


    }

    private fun Boolean.toInt(): Int {
        return if (this) {
            1
        } else {
            0
        }

    }

    private fun Int.toBoolean(): Boolean = this == 1


}