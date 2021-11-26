package com.example.easynotes.data

import java.io.Serializable

data class Note(
    var id: Int = -1,
    var title: String = "",
    var notes: String = "",
    var lastEdited: Long = 0,
    var createdDate: Long = 0,
    var isPinned: Boolean = false,
    var isPasswordProtected: Boolean = false
) : Serializable
