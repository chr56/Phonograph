package com.kabouzeid.gramophone.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.kabouzeid.gramophone.R
import com.kabouzeid.gramophone.model.Song
import com.kabouzeid.gramophone.util.PlaylistsUtil
// import java.util.*

/**
 * @author Karim Abou Zeid (kabouzeid), Aidan Follestad (afollestad)
 */
class CreatePlaylistDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val songs: List<Song>? = requireArguments().getParcelableArrayList(SONGS)
        return MaterialDialog(requireActivity())
            .title(R.string.new_playlist_title)
            .positiveButton(R.string.create_action)
            .negativeButton(android.R.string.cancel)
            .input(
                inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME or
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                hintRes = R.string.playlist_name_empty,
                // maxLength = 0,
                allowEmpty = false
            ) { _, charSequence ->
                if (activity == null) return@input
                val name: String = charSequence.toString().trim()
                if (name.isNotEmpty()) {
                    if (!PlaylistsUtil.doesPlaylistExist(requireActivity(), name)) {
                        val playlistId = PlaylistsUtil.createPlaylist(requireActivity(), name)
                        if (activity != null) {
                            if (songs != null && songs.isNotEmpty()) {
                                PlaylistsUtil.addToPlaylist(requireActivity(), songs, playlistId, true)
                            }
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            requireActivity().resources.getString(
                                R.string.playlist_exists, name
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    companion object {
        private const val SONGS = "songs"
        @JvmOverloads
        fun create(song: Song? = null as Song?): CreatePlaylistDialog {
            val list: MutableList<Song> = ArrayList()
            if (song != null) list.add(song)
            return create(list)
        }

        @JvmStatic
        fun create(songs: List<Song>?): CreatePlaylistDialog {
            val dialog = CreatePlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList(SONGS, ArrayList(songs))
            dialog.arguments = args
            return dialog
        }
    }
}