package com.example.easynotes.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.easynotes.*
import com.example.easynotes.util.Constant.ADD_NOTE_MESSAGE
import com.example.easynotes.util.Constant.ALERT_SECRET_MESSAGE_KEY
import com.example.easynotes.util.Constant.ALERT_MESSAGE_DELETE
import com.example.easynotes.util.Constant.ALERT_MESSAGE_SECRET
import com.example.easynotes.util.Constant.ALERT_TITLE_DELETE
import com.example.easynotes.util.Constant.ALERT_TITLE_SECRET
import com.example.easynotes.util.Constant.DELETE_NOTE_MESSAGE
import com.example.easynotes.util.Constant.ENABLE_SECURITY
import com.example.easynotes.util.Constant.FAILED_ADD_NOTE_MESSAGE
import com.example.easynotes.util.Constant.FAILED_DELETE_NOTE_MESSAGE
import com.example.easynotes.util.Constant.FAILED_UPDATE_NOTE_MESSAGE

import com.example.easynotes.util.Constant.GRID_VIEW
import com.example.easynotes.util.Constant.IS_EMPTY
import com.example.easynotes.util.Constant.IS_EXISTS
import com.example.easynotes.util.Constant.IS_MODIFIED
import com.example.easynotes.util.Constant.IS_SECRET_MODE
import com.example.easynotes.util.Constant.ITEM_POSITION

import com.example.easynotes.util.Constant.LIST_VIEW
import com.example.easynotes.util.Constant.MENU_GRID_VIEW
import com.example.easynotes.util.Constant.MENU_LIST_VIEW
import com.example.easynotes.util.Constant.MENU_NORMAL_MODE
import com.example.easynotes.util.Constant.MENU_SECRET_MODE
import com.example.easynotes.util.Constant.NEW_NOTE
import com.example.easynotes.util.Constant.NORMAL_MODE
import com.example.easynotes.util.Constant.NOTE
import com.example.easynotes.util.Constant.NOTE_PINED_MESSAGE
import com.example.easynotes.util.Constant.NOTE_SWAP_TO_NORMAL_MODE
import com.example.easynotes.util.Constant.NOTE_SWAP_TO_SECRET_MODE
import com.example.easynotes.util.Constant.NOTE_UNPINNED_MESSAGE
import com.example.easynotes.util.Constant.SECRET_MODE
import com.example.easynotes.util.Constant.SHARED_PREFERENCE_NAME
import com.example.easynotes.util.Constant.UNCHANGED_NOTES_MESSAGE
import com.example.easynotes.util.Constant.UPDATE_NOTE_MESSAGE

import com.example.easynotes.util.Constant.VIEW_TYPE
import com.example.easynotes.adapter.NotesAdapter
import com.example.easynotes.data.Note
import com.example.easynotes.interfaces.ClickableInterface
import com.example.easynotes.interfaces.ViewModelListener
import com.example.easynotes.util.Constant
import com.example.easynotes.viewmodel.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class DMainActivity : AppCompatActivity(), ClickableInterface,ViewModelListener {

    private var setPos = ArrayList<Int>()

    //setting up which mode
    private var currentMode = NORMAL_MODE

    //setting up which type
    private var viewType = GRID_VIEW


    //setting up menu for multiple selection for pining
    private var pinnedIconState = false

    //consent from dialog
    private var userConsent = false


    //menu
    private var menuItem  :MenuItem? = null

    //setting up views
    private lateinit var addNoteFAB: FloatingActionButton
    private lateinit var notesContainer: RecyclerView
    private lateinit var addNoteMessage: CardView
    private lateinit var firstNote: TextView


    //setting up no search result
    private lateinit var noResult: CardView

    //database and view model

    private lateinit var noteViewModel: NotesViewModel

    //action mode tool bar
    private var actionMode: ActionMode? = null

    private lateinit var noteList: ArrayList<Note>
    private lateinit var secretNoteList: ArrayList<Note>
    private lateinit var normalNoteList: ArrayList<Note>
    private lateinit var recyclerViewNoteList: ArrayList<Note>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //shared preference
        val userPreference = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)


        supportActionBar?.title = MENU_NORMAL_MODE

        val userConsent = userPreference.getBoolean(ALERT_SECRET_MESSAGE_KEY, false)
        //message if mobile not secured
        if (!userConsent) {

            setUpDialog(ALERT_TITLE_SECRET, ALERT_MESSAGE_SECRET, false)
        }


        //initializing views
        addNoteFAB = findViewById(R.id.addNotes)
        notesContainer = findViewById(R.id.notesContainer)
        addNoteMessage = findViewById(R.id.addNotesMeggage)
        noResult = findViewById(R.id.noresult)
        firstNote = findViewById(R.id.firstNote)


        //initializing database and view model
        noteViewModel = ViewModelProvider(this)[NotesViewModel::class.java]




        //initializing list
        noteList = ArrayList()
        secretNoteList = ArrayList()
        normalNoteList = ArrayList()
        recyclerViewNoteList = ArrayList()






        noteViewModel.getNotes().observe(this,{
            noteList.addAll(it)
            //getting user preference with default value
            viewType = userPreference.getInt(VIEW_TYPE, GRID_VIEW)


            //split secret and normal
            splitNotes()

            //callingAdapter
            displayNotes()
        })







        //on clicking this button it will go to edit notes
        addNoteFAB.setOnClickListener {
            createNote()
        }

        firstNote.setOnClickListener {
            createNote()
        }
        notesContainer.addItemDecoration(SpacingDecorator())

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.mainmenu, menu)


        // this.menu = menu!!
        if (viewType == 0) {
            menu!!.findItem(R.id.viewType).title = MENU_LIST_VIEW

        } else {
            menu!!.findItem(R.id.viewType).title = MENU_GRID_VIEW
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.search -> {


                filterNotes(item)


            }

            R.id.viewType -> {


                changeViewType(item)
            }


            R.id.hiddenNotes -> {
                var flag = true
                menuItem = item
                if (currentMode == SECRET_MODE) {
                    flag = false
                    openSecretNotes()
                }

                if (flag) {


                    if (isMobileSecured()) isAuthenticated()
                    else {

                        Toast.makeText(this@DMainActivity, ENABLE_SECURITY, Toast.LENGTH_LONG)
                            .show()
                    }
                }


            }



        }

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {

                addNoteFAB.visibility = View.VISIBLE
                return true
            }

        })
        return true
    }


    private fun splitNotes() {


        noteList.sortByDescending {
            it.lastEdited
        }



        for (i in noteList) {
            if (i.isPasswordProtected) {


                if (i.isPinned) secretNoteList.add(0, i)
                else secretNoteList.add(i)

            } else {
                if (i.isPinned) normalNoteList.add(0, i)
                else normalNoteList.add(i)


            }
        }


    }

    private fun createNote() {

        //passing intent to next activity i.e EditNoteActivity
        val intent = Intent(this, DEditNoteActivity::class.java)

        //check whether new or existing
        intent.putExtra(IS_EXISTS, false)

        //check whether note id created from secret mode
        if (currentMode == SECRET_MODE) intent.putExtra(IS_SECRET_MODE, true)
        else intent.putExtra(IS_SECRET_MODE, false)
        getNote.launch(intent)

    }

    private val getNote = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {


        if (it.resultCode == NEW_NOTE) {
            /* possibilities of fresh note
            * Normal Note
            * Secret Note
            * Pinned Note
            * Empty Note
            */
            //checking whether it is empty note
            val isEmptyNote = it.data?.getBooleanExtra(IS_EMPTY, false)!!

            if (!isEmptyNote) {
                // getting note from edit note activity
                val note = it.data?.getSerializableExtra(NOTE) as Note

                //check for mode
                if (note.isPasswordProtected) {

                    //check if pinned

                    secretNoteList.add(note)


                } else {

                    normalNoteList.add(note)


                }

                //add note to  db
                //coroutine implementation required here

                lifecycleScope.launch { noteViewModel.addNote(note,this@DMainActivity) }


            }
            else{
                Toast.makeText(this, Constant.EMPTY_NOTE_MESSAGE, Toast.LENGTH_SHORT).show()
            }


        } else {

            //Modifying existing note


            val positionOfItem = it.data?.getIntExtra(ITEM_POSITION, -1)!!

            /* possibilities of existing note
                * Normal Note
                * Secret Note
                * Pinned Note
                * Empty Note
                * no change note
                */
            val isEmptyNote = it.data?.getBooleanExtra(IS_EMPTY, false)!!
            val isModified = it.data?.getBooleanExtra(IS_MODIFIED, false)!!
            if (isEmptyNote) {

                //check for mode to delete from respective list if empty note
                if (currentMode == NORMAL_MODE) {
                    lifecycleScope.launch {  noteViewModel.deleteNote(normalNoteList[positionOfItem].id,this@DMainActivity)}

                    normalNoteList.removeAt(positionOfItem)
                } else {
                    lifecycleScope.launch { noteViewModel.deleteNote(secretNoteList[positionOfItem].id,this@DMainActivity) }

                    secretNoteList.removeAt(positionOfItem)
                }
            } else {

                //check if note is modified or not
                if (isModified) {
                    val note = it.data?.getSerializableExtra(NOTE) as Note

                    //check mode for changes
                    if (currentMode == NORMAL_MODE) {
                        //normal mode
                        //check for password protected
                        if (note.isPasswordProtected) {

                            //note is change to secret so removing in normal
                            normalNoteList.removeAt(positionOfItem)
                            //check if pinned

                            secretNoteList.add(note)


                        } else {
                            //check if pinned


                            //replacing note at that position


                            normalNoteList[positionOfItem] = note

                        }


                    } else {
                        //secret mode
                        if (!note.isPasswordProtected) {
                            // note is change to normal so removing in secret
                            secretNoteList.removeAt(positionOfItem)

                            normalNoteList.add(note)

                        } else {

                            secretNoteList[positionOfItem] = note

                        }


                    }


                    //update db
                    //coroutine implementation required

                    if (!isEmptyNote) {

                        lifecycleScope.launch { noteViewModel.updateNote(note,this@DMainActivity) }

                    }
                    else{
                        Toast.makeText(this, Constant.EMPTY_NOTE_MESSAGE, Toast.LENGTH_SHORT).show()
                    }


                }
                else{
                    Toast.makeText(this, UNCHANGED_NOTES_MESSAGE,Toast.LENGTH_SHORT).show()
                }

            }


        }
        displayNotes()

    }

    private fun displayNotes() {



        //check which mode so that we can display particular mode notes
        recyclerViewNoteList = if (currentMode == NORMAL_MODE) {
            normalNoteList
        } else {
            secretNoteList
        }


        recyclerViewNoteList.sortByDescending {
            it.lastEdited

        }
        recyclerViewNoteList.sortByDescending {
            it.isPinned
        }


        //check if recycler view is not empty
        //if empty display default message
        if (recyclerViewNoteList.size == 0) {
            notesContainer.visibility = View.GONE
            addNoteMessage.visibility = View.VISIBLE
        } else {
            notesContainer.visibility = View.VISIBLE
            addNoteMessage.visibility = View.GONE


            //check view type to set up layout manger
            if (viewType == GRID_VIEW) {

                notesContainer.layoutManager =
                    StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

            } else {

                notesContainer.layoutManager = LinearLayoutManager(this)
            }

            //custom adapter
            val notesAdapter = NotesAdapter(this, recyclerViewNoteList, this, viewType)
            notesContainer.adapter = notesAdapter
        }


    }

    private fun filterNotes(item: MenuItem) {
        //initializing search view
        val searchView = item.actionView as SearchView
        addNoteFAB.visibility = View.GONE
        noteList.clear()
        noteList.addAll(recyclerViewNoteList)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {


                //check whether any text is search
                if (newText!!.isNotEmpty()) {
                    //clear recycler view items so that previous searched won't be displayed
                    recyclerViewNoteList.clear()
                    noteList.forEach {
                        if (it.title.contains(newText,ignoreCase =true) || it.notes.contains(newText,ignoreCase = true)) {
                            Log.i("Note","yes ${it.title} ${it.notes}")
                            noResult.visibility = View.GONE
                            notesContainer.visibility = View.VISIBLE
                            if (!recyclerViewNoteList.contains(it)) recyclerViewNoteList.add(it)

                        } else {
                            Log.i("Note","No ${it.notes}")
                            if (recyclerViewNoteList.isEmpty()) {
                                noResult.visibility = View.VISIBLE
                                notesContainer.visibility = View.GONE
                            }

                        }
                    }

                } else {
                    //if there is no text available just display all list
                    Log.i("Note","Never")
                    notesContainer.visibility = View.VISIBLE
                    noResult.visibility = View.GONE
                    recyclerViewNoteList.clear()
                    recyclerViewNoteList.addAll(noteList)
                }

                if (recyclerViewNoteList.isNotEmpty()) {
                    displayNotes()
                }

                return true
            }

        })


    }

    private fun openSecretNotes() {
        //if mode is normal change to secret or vice versa

        if (currentMode == NORMAL_MODE) {
            supportActionBar?.title = MENU_SECRET_MODE
            menuItem!!.title = MENU_NORMAL_MODE
            currentMode = SECRET_MODE
        } else {
            supportActionBar?.title = MENU_NORMAL_MODE
            menuItem!!.title = MENU_SECRET_MODE
            currentMode = NORMAL_MODE
        }

        displayNotes()

    }

    private fun changeViewType(item: MenuItem) {
        //if view type is grid change to list or vice versa

        if (item.title == MENU_GRID_VIEW) {
            item.title = MENU_LIST_VIEW
            viewType = GRID_VIEW
        } else {
            item.title = MENU_GRID_VIEW
            viewType = LIST_VIEW
        }

        val userPreference = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
        //saving user preference while users closes app
        val editUserPreference = userPreference.edit()
        editUserPreference.putInt(VIEW_TYPE, viewType)
        editUserPreference.apply()
        displayNotes()
    }

    private val mActionCallBack = object : ActionMode.Callback {

        override fun onCreateActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            val inflater = mode!!.menuInflater
            inflater.inflate(R.menu.modifynote, menu)

            mode.title = "1 Item Selected"

            when(currentMode){
                NORMAL_MODE -> mode.menu.findItem(R.id.lock).title = "Lock"
                SECRET_MODE -> mode.menu.findItem(R.id.lock).title = "Un Lock"
            }
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }


        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onActionItemClicked(
            mode: ActionMode?,
            item: MenuItem?
        ): Boolean {
            return when (item!!.itemId) {
                R.id.pin -> {

                    pinnedItem()
                    mode!!.finish()
                    true
                }
                R.id.lock -> {
                    var flag = true
                    if (currentMode == SECRET_MODE) {
                        flag = false

                        swapBetweenNormalAndSecret()
                    }

                    if (flag) {


                        if (isMobileSecured()) {

                            swapBetweenNormalAndSecret()
                        } else {

                            Toast.makeText(this@DMainActivity, ENABLE_SECURITY, Toast.LENGTH_LONG)
                                .show()
                        }
                    }





                    mode!!.finish()
                    true
                }
                R.id.delete -> {
                    val singularPlural =
                        if (noteList.size == 1) {
                            "Item"
                        } else {
                            "Items"
                        }

                    setUpDialog(
                        ALERT_TITLE_DELETE,
                        "$ALERT_MESSAGE_DELETE ${noteList.size} $singularPlural"
                    )


                    true
                }

                R.id.allselect ->{

                    val p = setPos
                    if(item.title == "DeSelect"){
                        item.title = "Select"
                        item.icon=ResourcesCompat.getDrawable(resources, R.drawable.selectall, null)
                        mode!!.finish()





                    }

                    else{
                        item.title = "DeSelect"
                        item.icon=ResourcesCompat.getDrawable(resources, R.drawable.deselectall, null)

                        for(i in 0 until recyclerViewNoteList.size){
                            if(!p.contains(i)) {
                                onNotesClickListener(i,true)
                                NotesAdapter.view.add(notesContainer.getChildAt(i))
                                notesContainer.getChildAt(i).background = resources.getDrawable(R.color.transparent, null)
                            }

                        }

                    }


                    true
                }



                else -> {

                    false
                }
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {


            actionMode = null
            updateRecyclerView()
            //displayNotes()
            setPos.clear()
            NotesAdapter.isLongPress = false
            NotesAdapter.view.clear()

        }

    }


    private fun isAuthenticated() {


        val keyGuardManger = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        if (keyGuardManger.isDeviceSecure) {
            val intent = keyGuardManger.createConfirmDeviceCredentialIntent(
                "Authentication required",
                "password"
            )
            getActivityResult.launch(intent)

        }


    }

    private val getActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {


            openSecretNotes()

        } else {

            Toast.makeText(this, "Invalid Authentication", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onNotesClickListener(position: Int, isLongPress: Boolean) {
        Log.i("Letsfind","second")

        //check whether recycler view in multiple selection state
        if (isLongPress) {
            setPos.add(position)

            if (actionMode == null) {

                actionMode = startSupportActionMode(mActionCallBack)
                noteList.clear()
            }

            if (!noteList.contains(recyclerViewNoteList[position])) noteList.add(recyclerViewNoteList[position]
            )
            else noteList.remove(recyclerViewNoteList[position])

            if(noteList.size == recyclerViewNoteList.size) {
                actionMode!!.menu.findItem(R.id.allselect).icon =
                    ResourcesCompat.getDrawable(resources, R.drawable.deselectall, null)
                actionMode!!.menu.findItem(R.id.allselect).title = "DeSelect"
            }

           if (noteList.isEmpty()) {
               actionMode!!.finish()
            } else {
                actionMode!!.title = "${noteList.size} Item Selected"

            }



            var flag = false
            for (i in noteList) {
                if (!i.isPinned) {
                    flag = true
                    actionMode!!.menu.findItem(R.id.pin).icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.pin, null)
                    actionMode!!.menu.findItem(R.id.pin).title = "Pin"
                    pinnedIconState = true
                } else {
                    if (!flag) {
                        actionMode!!.menu.findItem(R.id.pin).icon =
                            ResourcesCompat.getDrawable(resources, R.drawable.unpin, null)
                        actionMode!!.menu.findItem(R.id.pin).title = "Un Pin"
                        pinnedIconState = false
                    }
                }
            }


        } else {
            //to modify existing Notes
            val intent = Intent(this, DEditNoteActivity::class.java)

            //set it already exists
            intent.putExtra(IS_EXISTS, true)

            //set position because while activity getting modified notes we need position to modify
            intent.putExtra(ITEM_POSITION, position)


            //set notes to be modified
            intent.putExtra(NOTE, recyclerViewNoteList[position])

            getNote.launch(intent)


        }
    }


    private fun deleteItem() {


        for (i in noteList) {

            //checking mode using password protected instead of current mode
            if (i.isPasswordProtected) {
                secretNoteList.remove(i)
            } else {

                normalNoteList.remove(i)
            }

        }

        if (noteList.size == 1) Toast.makeText(
            this,
            DELETE_NOTE_MESSAGE.replace("s", ""),
            Toast.LENGTH_SHORT
        ).show()
        else Toast.makeText(this, DELETE_NOTE_MESSAGE, Toast.LENGTH_SHORT).show()
        //delete note in db
        //coroutine implementation required
        actionMode!!.finish()
        lifecycleScope.launch { for (i in noteList) {


            noteViewModel.deleteNote(i.id,this@DMainActivity)
        }
            noteList.clear()
        }


        updateRecyclerView()


    }

    private fun pinnedItem() {


        //as of now just pinning item not
        // unpinning if already pinned only
        // that operation happens in edit
        // notes but will change


        //already pinned
        //not pinned


        for (i in noteList) {

            i.isPinned = pinnedIconState


        }

        if (pinnedIconState) {
            if (noteList.size == 1) Toast.makeText(
                this,
                NOTE_PINED_MESSAGE.replace("s", ""),
                Toast.LENGTH_SHORT
            ).show()
            else Toast.makeText(this, NOTE_PINED_MESSAGE, Toast.LENGTH_SHORT).show()

        } else {
            if (noteList.size == 1) Toast.makeText(
                this,
                NOTE_UNPINNED_MESSAGE.replace("s", ""),
                Toast.LENGTH_SHORT
            ).show()
            else Toast.makeText(this, NOTE_UNPINNED_MESSAGE, Toast.LENGTH_SHORT).show()
        }

        //update note in db
        //coroutine implementation required
        lifecycleScope.launch {
            for (i in noteList) {
            noteViewModel.updateNote(i,this@DMainActivity)
        }
            noteList.clear()
        }


        updateRecyclerView()
    }

    private fun swapBetweenNormalAndSecret() {

        for (i in noteList) {
            //if it was normal mode then we must add to secret vice versa
            if (currentMode == NORMAL_MODE) {

                normalNoteList.remove(i)

                i.isPasswordProtected = true

                if (i.isPinned) {

                    secretNoteList.add(0, i)

                } else {
                    secretNoteList.add(i)

                }


            } else {

                secretNoteList.remove(i)

                i.isPasswordProtected = false

                if (i.isPinned) {
                    normalNoteList.add(0, i)
                } else {
                    normalNoteList.add(i)
                }


            }


        }


        when (currentMode) {
            NORMAL_MODE -> {
                if (noteList.size == 1) Toast.makeText(
                    this,
                    NOTE_SWAP_TO_SECRET_MODE.replace("s", ""),
                    Toast.LENGTH_SHORT
                ).show()
                else Toast.makeText(this, NOTE_SWAP_TO_SECRET_MODE, Toast.LENGTH_SHORT).show()
            }

            SECRET_MODE -> {
                if (noteList.size == 1) Toast.makeText(
                    this,
                    NOTE_SWAP_TO_NORMAL_MODE.replace("s", ""),
                    Toast.LENGTH_SHORT
                ).show()
                else Toast.makeText(this, NOTE_SWAP_TO_NORMAL_MODE, Toast.LENGTH_SHORT).show()
            }

        }
        //update note in db
        //coroutine implementation required
        lifecycleScope.launch {for (i in noteList) {

            noteViewModel.updateNote(i,this@DMainActivity)
        }
            noteList.clear()
        }


        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        recyclerViewNoteList = if (currentMode == NORMAL_MODE) {
            normalNoteList
        } else {
            secretNoteList
        }

        if (recyclerViewNoteList.isEmpty()) {
            notesContainer.visibility = View.GONE
            addNoteMessage.visibility = View.VISIBLE
        }

        displayNotes()

    }


    private fun isMobileSecured(): Boolean {
        val keyGuardManger = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        return keyGuardManger.isDeviceSecure
    }

    private fun setUpDialog(title: String, message: String, onlyAlert: Boolean = true) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(title)

        builder.setMessage(message)


        if (onlyAlert) {
            builder.setIcon(R.drawable.delete)
            //performing positive action
            builder.setPositiveButton("Yes") { _, _ ->
                userConsent = true

                deleteItem()


            }

            //performing negative action
            builder.setNegativeButton("No") { _, _ ->
                userConsent = false
            }
        } else {
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            val view = View.inflate(this, R.layout.alertcheckbox, null)

            builder.setView(view)
            var checked = false
            val checkBox = view.findViewById<CheckBox>(R.id.checkbox)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                checked = isChecked
            }





            builder.setPositiveButton(
                "Ok"
            ) { _, _ ->

                if (checked) {
                    val userPreference = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                    val editUserPreference = userPreference.edit()
                    editUserPreference.putBoolean(ALERT_SECRET_MESSAGE_KEY, true)
                    editUserPreference.apply()
                }


            }


        }


        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun success(result: Boolean, operation: NotesDbOperation) {

        when(operation){
            NotesDbOperation.ADD->{
                    if(result) Toast.makeText(this, ADD_NOTE_MESSAGE,Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, FAILED_ADD_NOTE_MESSAGE,Toast.LENGTH_SHORT).show()
            }
            NotesDbOperation.UPDATE ->{
                if(result) Toast.makeText(this, UPDATE_NOTE_MESSAGE,Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, FAILED_UPDATE_NOTE_MESSAGE,Toast.LENGTH_SHORT).show()
            }
            NotesDbOperation.DELETE ->{
                if(result) Toast.makeText(this, DELETE_NOTE_MESSAGE,Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, FAILED_DELETE_NOTE_MESSAGE,Toast.LENGTH_SHORT).show()
            }


        }

    }


}