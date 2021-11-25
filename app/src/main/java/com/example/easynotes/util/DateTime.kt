package com.example.easynotes.util


import android.annotation.SuppressLint
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
val date =  SimpleDateFormat("dd/MM/yyyy")
@SuppressLint("SimpleDateFormat")
val time  = SimpleDateFormat("hh:mm a")









fun Long.getTime() : String = time.format(this)

fun Long.getDate() : String = date.format(this)

fun String.isToday(): Boolean = DateUtils.isToday(this.toLong())


fun String.isYesterday(): Boolean = DateUtils.isToday(this.toLong() + DateUtils.DAY_IN_MILLIS)