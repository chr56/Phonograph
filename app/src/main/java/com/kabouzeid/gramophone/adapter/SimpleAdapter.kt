package com.kabouzeid.gramophone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kabouzeid.gramophone.R
import com.kabouzeid.gramophone.helper.MusicPlayerRemote
import com.kabouzeid.gramophone.util.MusicUtil
import java.util.regex.Pattern

class SimpleAdapter(stamps: IntArray, lines: Array<CharSequence>) : RecyclerView.Adapter<SimpleAdapter.VH>() {
    var lyrics = lines
    var timeStamps = stamps

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val line: TextView = itemView.findViewById<TextView>(R.id.dialog_lyrics_line)
        val time: TextView = itemView.findViewById<TextView>(R.id.dialog_lyrics_times)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_lyrics, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        // parse line feed
        val b = StringBuffer()
        lyrics[position].split(Pattern.compile("\\\\[nNrR]")).forEach {
            b.append(it).appendLine()
        }

        holder.time.text = MusicUtil.parseTimeStamp(timeStamps[position])
        if (timeStamps[position] < 0) holder.time.visibility = View.GONE
        holder.line.text = b.toString()
        holder.line.setOnLongClickListener {
            MusicPlayerRemote.seekTo(timeStamps[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return lyrics.size
    }
}
