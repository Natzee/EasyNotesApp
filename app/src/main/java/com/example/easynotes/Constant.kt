package com.example.easynotes

object Constant {


    //Constant for user preference
    const val SECRET_MODE = 1
    const val NORMAL_MODE = 0
    const val LIST_VIEW = 1
    const val GRID_VIEW = 0

    //constant for shared preference key
    const val MODE = "mode"
    const val VIEW_TYPE = "viewType"
    const val SHARED_PREFERENCE_NAME = "UserPreference"


    //constant for activity result
    const val NEW_NOTE = 1
    const val EXISTING_NOTE = 0

    //constant for key in intent to differentiate new note or existing note
    const val IS_EXISTS = "isExists"

    //constant for key in intent empty note
    const val IS_EMPTY = "isEmpty"

    //constant for key in intent to get not
    const val NOTE = "note"

    //constant for key in intent to get position of note in recycler view
    const val ITEM_POSITION = "itemPosition"

    //constant for key in intent to check note is modified or not
    const val IS_MODIFIED = "isModified"


    //constant for key in intent to check note is from secret mode
    const val IS_SECRET_MODE = "isSecretMode"

    //constant for menu
    const val MENU_LIST_VIEW = "List view"
    const val MENU_GRID_VIEW = "Grid view"
    const val MENU_NORMAL_MODE = "Normal Notes"
    const val MENU_SECRET_MODE = "Secret Notes"

    //constant for dialog for delete
    const val ALERT_TITLE_DELETE = "Delete"
    const val ALERT_MESSAGE_DELETE = "Do you want to really Delete"

    //constant for dialog for show to enable secret feature

    const val ALERT_TITLE_SECRET  = "Attention"
    const val ALERT_MESSAGE_SECRET = "To Use Secret Mode Feature Make Sure Your Device is Protected"


    //constant for alert message
    const val ALERT_SECRET_MESSAGE_KEY = "message"


    //toast message
    const val ENABLE_SECURITY = "Make Sure Your Mobile is Secured"

    const val EMPTY_NOTE_MESSAGE = "Discard Empty Note"

    const val UNCHANGED_NOTES_MESSAGE = "Note Unchanged"

    const val  ADD_NOTE_MESSAGE ="Note Added Successfully"

    const val UPDATE_NOTE_MESSAGE = "Note Updated Successfully"

    const val DELETE_NOTE_MESSAGE = "Notes Deleted Successfully"

    const val NOTE_PINED_MESSAGE = "Notes Pinned"

    const val NOTE_UNPINNED_MESSAGE = "Notes Un Pinned"


    const val NOTE_SWAP_TO_SECRET_MODE = "Notes Added to Secret Note"

    const val NOTE_SWAP_TO_NORMAL_MODE = "Notes Added to Normal Mode"

    //constant for Edit note activity toolbar name

    const val EDIT_NOTE_TOOL_BAR_NAME = "Edit Note"

}