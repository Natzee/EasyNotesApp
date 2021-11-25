package com.example.easynotes.ui


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.example.easynotes.Constant.EXISTING_NOTE
import com.example.easynotes.Constant.IS_EMPTY
import com.example.easynotes.Constant.IS_EXISTS
import com.example.easynotes.Constant.IS_MODIFIED
import com.example.easynotes.Constant.IS_SECRET_MODE
import com.example.easynotes.Constant.ITEM_POSITION
import com.example.easynotes.Constant.NEW_NOTE
import com.example.easynotes.Constant.NOTE
import com.example.easynotes.R
import com.example.easynotes.data.Note
import com.example.easynotes.util.getDate
import com.example.easynotes.util.getTime
import com.example.easynotes.util.isToday
import com.example.easynotes.util.isYesterday
import com.yahiaangelo.markdownedittext.MarkdownEditText
import com.yahiaangelo.markdownedittext.MarkdownStylesBar
import java.util.*
import com.example.easynotes.Constant.ADD_NOTE_MESSAGE
import com.example.easynotes.Constant.DELETE_NOTE_MESSAGE
import com.example.easynotes.Constant.EDIT_NOTE_TOOL_BAR_NAME
import com.example.easynotes.Constant.EMPTY_NOTE_MESSAGE
import com.example.easynotes.Constant.UNCHANGED_NOTES_MESSAGE
import com.example.easynotes.Constant.UPDATE_NOTE_MESSAGE
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class DEditNoteActivity : AppCompatActivity() {


    private var resultCode = -1
    private var isSecretMode = false
    private var isNoteExists = false
    private var defaultPinnedState = false
    private var defaultPasswordProtection = false
    private var isDeleteIconClicked = false
    private var isTitleFocused = false
    private var isContentFocused = false

    private var len : Int = 0

    //private var isContentClicked = false
    private lateinit var note: Note

    //setting up view
    private lateinit var title: EditText
    private lateinit var content: MarkdownEditText
    private lateinit var noteCreated: TextView
    private lateinit var stylesBar: MarkdownStylesBar
    private lateinit var characterCount : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notes)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = EDIT_NOTE_TOOL_BAR_NAME

        title = findViewById(R.id.title)
        content = findViewById(R.id.content)
        noteCreated= findViewById(R.id.noteCreated)
        characterCount = findViewById(R.id.characterCount)


        stylesBar = findViewById(R.id.stylesbar)
        content.setStylesBar(stylesBar)

        //check whether new note or existing note
        //if true then get existing note or create
        //new note


        isNoteExists = intent.getBooleanExtra(IS_EXISTS, false)

        isSecretMode = intent.getBooleanExtra(IS_SECRET_MODE, false)

        if (isNoteExists) {
            note = intent.getSerializableExtra(NOTE) as Note
            resultCode = EXISTING_NOTE

            title.setText(note.title)

            content.renderMD(note.notes)


            updateCreatedDateTime()


        } else {
            note = Note()
            resultCode = NEW_NOTE
        }

        if (isSecretMode) {
            note.isPasswordProtected = true
        }

        defaultPinnedState = note.isPinned
        defaultPasswordProtection = note.isPasswordProtected

        openKeyBoard()

        title.setOnFocusChangeListener { _, hasFocus ->


            if (hasFocus) {
                isContentFocused = false
                isTitleFocused = true
                if (stylesBar.visibility == View.VISIBLE) stylesBar.visibility = View.GONE

            } else {

                hideKeyboard(title)
            }


        }
        content.setOnFocusChangeListener { _, hasFocus ->


            if (hasFocus) {
                isContentFocused = true
                isTitleFocused = false

                stylesBar.visibility = View.VISIBLE

                enableKeyboard(content)


            }

        }

        content.addTextChangedListener {


            len=it!!.length
            if(it.toString().contains(" "))
            {
                if(len != 0 ) len-=it.toString().filter { it.toString() == " " }.count()
            }


            Log.i("Edit_NS","$len   || /$it/")
            characterCount.text = len.toString()

        }


        KeyboardVisibilityEvent.setEventListener(
            this
        ) {

            if (isContentFocused) {


                when (it) {
                    true -> {
                        Log.i("Edit","req...")
                        content.requestFocus()
                    }
                    false -> {
                        Log.i("Edit","clear..")
                        content.clearFocus()
                    }
                }
            }


        }


    }



    @SuppressLint("SetTextI18n")
    private fun updateCreatedDateTime() {
        var flag = false

        val date = note.createdDate.getDate()
        val time = note.createdDate.getTime()

        noteCreated.text = "$date $time"
    }


    private fun openKeyBoard() {


        if (note.title.isEmpty()) {

            stylesBar.visibility = View.GONE
            isContentFocused = false
            isTitleFocused = true

            title.requestFocus()
            enableKeyboard(title)

        } else {


            stylesBar.visibility = View.VISIBLE
            isContentFocused = true
            isTitleFocused = false
            content.requestFocus()

            enableKeyboard(content)
        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.editnotemenu, menu)




        if (isSecretMode) {

            menu!!.findItem(R.id.secretNotes).icon =
                ResourcesCompat.getDrawable(resources, R.drawable.lock, null)
        } else {
            menu!!.findItem(R.id.secretNotes).icon =
                ResourcesCompat.getDrawable(resources, R.drawable.unlock, null)
        }


        if (note.isPinned) {
            menu.findItem(R.id.pinned).icon =
                ResourcesCompat.getDrawable(resources, R.drawable.unpin, null)
        } else {
            menu.findItem(R.id.pinned).icon =
                ResourcesCompat.getDrawable(resources, R.drawable.pin, null)
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> onBackPressed()

            R.id.pinned -> {


                note.isPinned = !note.isPinned
                if (note.isPinned) {
                    item.icon = ResourcesCompat.getDrawable(resources, R.drawable.unpin, null)
                    Toast.makeText(this, "Note Pinned", Toast.LENGTH_SHORT).show()
                } else {
                    item.icon = ResourcesCompat.getDrawable(resources, R.drawable.pin, null)

                    Toast.makeText(this, "Note UnPinned", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.secretNotes -> {
                var flag = true
                if (isSecretMode) {
                    flag = true
                } else {
                    if (!isMobileSecured()) {
                        flag = false
                    }

                }

                if (flag) {
                    note.isPasswordProtected = !note.isPasswordProtected
                    if (note.isPasswordProtected) {
                        Toast.makeText(this, "Note Moved to Secret", Toast.LENGTH_SHORT).show()
                        item.icon = ResourcesCompat.getDrawable(resources, R.drawable.lock, null)
                    } else {
                        Toast.makeText(this, "Note Moved To Normal", Toast.LENGTH_SHORT).show()
                        item.icon = ResourcesCompat.getDrawable(resources, R.drawable.unlock, null)
                    }
                } else {
                    Toast.makeText(this, "Make Sure Your Mobile Is Secured", Toast.LENGTH_SHORT)
                        .show()
                }

            }


            R.id.delete -> {

                val builder = AlertDialog.Builder(this)

                builder.setTitle("Delete")

                builder.setMessage("Confirm Delete Note")


                builder.setIcon(R.drawable.delete)
                //performing positive action
                builder.setPositiveButton("Yes") { _, _ ->

                    title.setText("")
                    content.setText("")
                    isDeleteIconClicked = true


                    onBackPressed()


                }

                //performing negative action
                builder.setNegativeButton("No") { _, _ ->

                }

                val alertDialog: AlertDialog = builder.create()

                alertDialog.setCancelable(false)
                alertDialog.show()


            }
        }
        return true
    }


    override fun onBackPressed() {


        /*
        * check for emptyNote
        * check unChanged Note
        * */
        val title = title.text.toString().trim()
        val c = content.getMD().trim()


        val intent1 = Intent(this, DMainActivity::class.java)

        if (title.isEmpty() && c.isEmpty()) {

            val positionOfItem = intent.getIntExtra(ITEM_POSITION, -1)
            intent1.putExtra(ITEM_POSITION, positionOfItem)
            if (isDeleteIconClicked) Toast.makeText(
                this,
                DELETE_NOTE_MESSAGE.replace("s", ""),
                Toast.LENGTH_SHORT
            ).show()
            else Toast.makeText(this, EMPTY_NOTE_MESSAGE, Toast.LENGTH_SHORT).show()

            intent1.putExtra(IS_EMPTY, true)
        } else if (title == note.title && c == note.notes && defaultPasswordProtection == note.isPasswordProtected && defaultPinnedState == note.isPinned) {

            Toast.makeText(this, UNCHANGED_NOTES_MESSAGE, Toast.LENGTH_SHORT).show()
            intent1.putExtra(IS_MODIFIED, false)
        } else {
            intent1.putExtra(IS_EMPTY, false)
            intent1.putExtra(IS_MODIFIED, true)


            if (!(title == note.title && c == note.notes)) {
                note.title = title
                note.notes = c

                if (note.lastEdited == 0L) {

                    note.createdDate = Calendar.getInstance().timeInMillis
                    Toast.makeText(this, ADD_NOTE_MESSAGE, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, UPDATE_NOTE_MESSAGE, Toast.LENGTH_SHORT).show()
                }
                note.lastEdited = Calendar.getInstance().timeInMillis

            }







            if (isNoteExists) {
                val positionOfItem = intent.getIntExtra(ITEM_POSITION, -1)
                intent1.putExtra(ITEM_POSITION, positionOfItem)

            }

            intent1.putExtra(NOTE, note)


        }

        setResult(resultCode, intent1)
        super.onBackPressed()

    }


    private fun isMobileSecured(): Boolean {
        val keyGuardManger = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        return keyGuardManger.isDeviceSecure
    }

    private fun enableKeyboard(view: EditText?) {


        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)


    }

    private fun hideKeyboard(view: EditText?) {


        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }


}