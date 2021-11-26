package com.example.easynotes.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat


import androidx.recyclerview.widget.RecyclerView
import com.example.easynotes.ClickableInterface
import com.example.easynotes.R
import com.example.easynotes.data.Note
import com.example.easynotes.util.*
import com.yahiaangelo.markdownedittext.MarkdownEditText

class NotesAdapter(
    private val c: Context,
    private val listOfNotes: ArrayList<Note>,
    private val clickableInterface: ClickableInterface,
    private val LGType: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var isLongPress = false
        var view = ArrayList<View>()


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GridViewHolder(itemView: View, clickableInterface: ClickableInterface, c: Context) :
        RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val notes: TextView = itemView.findViewById(R.id.notes)
        val noteSample: MarkdownEditText = itemView.findViewById(R.id.notes_sample)
        val pinned: ImageView = itemView.findViewById(R.id.pinnedrv)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime)

        init {

            itemView.setOnClickListener {
                Log.i("Letsfind","first")
                if (isLongPress) {
                    if (!view.contains(it)) {
                        view.add(it)
                        itemView.background = c.resources.getDrawable(R.color.transparent, null)
                    } else {
                        view.remove(it)
                        itemView.background = c.resources.getDrawable(R.color.white, null)
                    }
                } else {
                    itemView.background = c.resources.getDrawable(R.color.white, null)
                }



                clickableInterface.onNotesClickListener(adapterPosition, isLongPress)


            }

            itemView.setOnLongClickListener {
                isLongPress = true
                clickableInterface.onNotesClickListener(adapterPosition, isLongPress)
                if (isLongPress) {
                    if (!view.contains(it)) {
                        view.add(it)
                        itemView.background = c.resources.getDrawable(R.color.transparent, null)
                    } else {
                        view.remove(it)
                        itemView.background = c.resources.getDrawable(R.color.white, null)
                    }
                } else {
                    itemView.background = c.resources.getDrawable(R.color.white, null)
                }

                true
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class ListViewHolder(itemView: View, clickableInterface: ClickableInterface, c: Context) :
        RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val dateEdited: TextView = itemView.findViewById(R.id.date)
        val timeEdited: TextView = itemView.findViewById(R.id.time)
        val pinned: ImageView = itemView.findViewById(R.id.pinnedrv)

        init {

            itemView.setOnClickListener {

                if (isLongPress) {
                    if (!view.contains(it)) {
                        view.add(it)
                        itemView.background = c.resources.getDrawable(R.color.transparent, null)
                    } else {
                        view.remove(it)
                        itemView.background = c.resources.getDrawable(R.color.white, null)
                    }
                } else {
                    itemView.background = c.resources.getDrawable(R.color.white, null)
                }
                clickableInterface.onNotesClickListener(adapterPosition, isLongPress)


            }

            itemView.setOnLongClickListener {

                isLongPress = true
                clickableInterface.onNotesClickListener(adapterPosition, isLongPress)
                if (isLongPress) {
                    if (!view.contains(it)) {
                        view.add(it)
                        itemView.background = c.resources.getDrawable(R.color.transparent, null)
                    } else {
                        view.remove(it)
                        itemView.background = c.resources.getDrawable(R.color.white, null)
                    }
                } else {
                    itemView.background = c.resources.getDrawable(R.color.white, null)
                }

                true
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (LGType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.griditem, parent, false)
            GridViewHolder(view, clickableInterface, c)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem, parent, false)
            ListViewHolder(view, clickableInterface, c)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (LGType == 0) {
            holder as GridViewHolder



            if (listOfNotes[position].notes.isEmpty()) {
                holder.notes.visibility = View.GONE
            } else {
                holder.notes.visibility = View.VISIBLE
                holder.noteSample.renderMD(listOfNotes[position].notes)
                holder.notes.text = holder.noteSample.editableText
            }




                if (listOfNotes[position].title.isEmpty()) {
                    holder.title.visibility = View.GONE
                } else {
                    holder.title.text = listOfNotes[position].title
                }



            if (listOfNotes[position].isPinned) {

                holder.pinned.visibility = View.VISIBLE
            } else {
                holder.pinned.visibility = View.GONE
            }

            when {
                listOfNotes[position].lastEdited.toString().isToday() -> {

                    holder.dateTime.text = "Today ${listOfNotes[position].lastEdited.getTime()}"
                }
                listOfNotes[position].lastEdited.toString().isYesterday() -> {

                    holder.dateTime.text = "Yesterday ${listOfNotes[position].lastEdited.getTime()}"
                }
                else -> {
                    holder.dateTime.text =
                        "${listOfNotes[position].lastEdited.getDate()} ${listOfNotes[position].lastEdited.getTime()}"
                }
            }


        } else {
            holder as ListViewHolder


            if (listOfNotes[position].title.isEmpty()) {
                holder.title.setTypeface(holder.title.typeface,Typeface.NORMAL)

                holder.title.text = listOfNotes[position].notes
            }
            else {
                holder.title.setTypeface(holder.title.typeface,Typeface.BOLD)

                holder.title.text = listOfNotes[position].title
            }




            when {
                listOfNotes[position].lastEdited.toString().isToday() -> {

                    holder.dateEdited.text = "Today"
                    holder.timeEdited.text = listOfNotes[position].lastEdited.getTime()

                }
                listOfNotes[position].lastEdited.toString().isYesterday() -> {

                    holder.dateEdited.text = "Yesterday"
                    holder.timeEdited.text = listOfNotes[position].lastEdited.getTime()
                }
                else -> {
                    holder.dateEdited.text = listOfNotes[position].lastEdited.getDate()
                    holder.timeEdited.text = listOfNotes[position].lastEdited.getTime()
                }
            }
            if (listOfNotes[position].isPinned) {
                holder.pinned.visibility = View.VISIBLE
            }
            else {
                holder.pinned.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfNotes.size
    }



}