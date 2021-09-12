package com.kabouzeid.gramophone.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import chr_56.MDthemer.core.ThemeColor
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.kabouzeid.gramophone.R
import com.kabouzeid.gramophone.model.Song
import com.kabouzeid.gramophone.util.MusicUtil
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.logging.Hex
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import org.jaudiotagger.tag.datatype.DataTypes
import org.jaudiotagger.tag.id3.AbstractID3v2Frame
import java.io.File
import java.io.IOException

// Todo Completed
/**
 * @author Karim Abou Zeid (kabouzeid), Aidan Follestad (afollestad), chr_56<modify>
 */
class SongDetailDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context: Activity? = activity
        val song: Song? = requireArguments().getParcelable("song")
        val dialog = MaterialDialog(context as Context)
            .title(R.string.label_details)
            .positiveButton(android.R.string.ok)
            .customView(viewRes = R.layout.dialog_file_details, horizontalPadding = true, scrollable = true)
        //set button color
        dialog.getActionButton(WhichButton.POSITIVE).updateTextColor(ThemeColor.accentColor(context))

        val dialogView: View = dialog.getCustomView()

        val fileName: TextView = dialogView.findViewById(R.id.file_name)
        val filePath: TextView = dialogView.findViewById(R.id.file_path)
        val fileSize: TextView = dialogView.findViewById(R.id.file_size)
        val fileFormat: TextView = dialogView.findViewById(R.id.file_format)
        val trackLength: TextView = dialogView.findViewById(R.id.track_length)
        val bitRate: TextView = dialogView.findViewById(R.id.bitrate)
        val samplingRate: TextView = dialogView.findViewById(R.id.sampling_rate)

        val title: TextView = dialogView.findViewById(R.id.text_song_title)
        val artist: TextView = dialogView.findViewById(R.id.text_song_artist_name)
        val album: TextView = dialogView.findViewById(R.id.text_song_album_name)
        val albumArtist: TextView = dialogView.findViewById(R.id.text_song_album_artist)
        val year: TextView = dialogView.findViewById(R.id.text_song_year)
        val genre: TextView = dialogView.findViewById(R.id.text_song_genre)
        val track: TextView = dialogView.findViewById(R.id.text_song_track_number)
        val other: TextView = dialogView.findViewById(R.id.text_song_other)

        fileName.text = makeTextWithTitle(context, R.string.label_file_name, "-")
        filePath.text = makeTextWithTitle(context, R.string.label_file_path, "-")
        fileSize.text = makeTextWithTitle(context, R.string.label_file_size, "-")
        fileFormat.text = makeTextWithTitle(context, R.string.label_file_format, "-")
        trackLength.text = makeTextWithTitle(context, R.string.label_track_length, "-")
        bitRate.text = makeTextWithTitle(context, R.string.label_bit_rate, "-")
        samplingRate.text = makeTextWithTitle(context, R.string.label_sampling_rate, "-")

        title.text = makeTextWithTitle(context, R.string.title,"-")
        artist.text = makeTextWithTitle(context, R.string.artist,"-")
        album.text = makeTextWithTitle(context, R.string.album,"-")
        albumArtist.text = makeTextWithTitle(context, R.string.album_artist,"-")
        year.text = makeTextWithTitle(context, R.string.year,"-")
        genre.text = makeTextWithTitle(context, R.string.genre,"-")
        track.text = makeTextWithTitle(context, R.string.track,"-")
        other.text = makeTextWithTitle(context,R.string.other_information,"-")

        if (song != null) {
            val songFile = File(song.data)
            if (songFile.exists()) {
                fileName.text = makeTextWithTitle(context, R.string.label_file_name, songFile.name)
                filePath.text = makeTextWithTitle(context, R.string.label_file_path, songFile.absolutePath)
                fileSize.text = makeTextWithTitle(context, R.string.label_file_size, getFileSizeString(songFile.length()))
                try {
                    val audioFile: AudioFile = AudioFileIO.read(songFile)
                    // debug only
                    audioFile.tag.fields.forEach { tagField ->
                        Log.v("DetailDialog", "# " + String(tagField.rawContent))
                        var hexString: String = "# "
                        tagField.rawContent.forEach { byte -> 
                            hexString += Hex.asHex(byte)
                            hexString += " "
                        }
                        Log.v("DetailDialog", hexString)
                    }

                    // files of the song
                    val audioHeader: AudioHeader = audioFile.audioHeader
                    fileFormat.text = makeTextWithTitle(context, R.string.label_file_format, audioHeader.format)
                    trackLength.text = makeTextWithTitle(context, R.string.label_track_length, MusicUtil.getReadableDurationString((audioHeader.trackLength * 1000).toLong()))
                    bitRate.text = makeTextWithTitle(context, R.string.label_bit_rate, audioHeader.bitRate + " kb/s")
                    samplingRate.text = makeTextWithTitle(context, R.string.label_sampling_rate, audioHeader.sampleRate + " Hz")
                    // tags of the song
                    title.text = makeTextWithTitle(context, R.string.title,song.title)
                    artist.text = makeTextWithTitle(context, R.string.artist,song.artistName)
                    album.text = makeTextWithTitle(context, R.string.album,song.albumName)
                    albumArtist.text = makeTextWithTitle(context, R.string.album_artist,audioFile.tag.getFirst(FieldKey.ALBUM_ARTIST))
                    if (song.year != 0) year.text = makeTextWithTitle(context, R.string.year,song.year.toString())
                    val songGenre = audioFile.tag.getFirst(FieldKey.GENRE)
                    genre.text = makeTextWithTitle(context, R.string.genre,songGenre)
                    if (song.trackNumber != 0) track.text = makeTextWithTitle(context, R.string.track,song.trackNumber.toString())


                    val custInfoField = audioFile.tag.getFields("TXXX")
                    var custInfo: String = "-";
                    if (custInfoField != null && custInfoField.size > 0){
                        custInfo = "<br />";
                        custInfoField.forEach { TagField ->
                            val frame = TagField as AbstractID3v2Frame
                            custInfo += frame.body.getObjectValue(DataTypes.OBJ_DESCRIPTION)
                            custInfo +=  ":<br />"
                            custInfo += frame.body.getObjectValue(DataTypes.OBJ_TEXT)
                            custInfo += "<br />"
                        }
                    }
                    other.text = makeTextWithTitle(context, R.string.other_information,custInfo)


                } catch (e: CannotReadException) {
                    Log.e(TAG, "error while reading the song file", e)
                    // fallback
                    trackLength.text = makeTextWithTitle(context, R.string.label_track_length, MusicUtil.getReadableDurationString(song.duration))
                } catch (e: IOException) {
                    Log.e(TAG, "error while reading the song file", e)
                    trackLength.text = makeTextWithTitle(context, R.string.label_track_length, MusicUtil.getReadableDurationString(song.duration))
                } catch (e: TagException) {
                    Log.e(TAG, "error while reading the song file", e)
                    trackLength.text = makeTextWithTitle(context, R.string.label_track_length, MusicUtil.getReadableDurationString(song.duration))
                } catch (e: ReadOnlyFileException) {
                    Log.e(TAG, "error while reading the song file", e)
                    trackLength.text = makeTextWithTitle(context, R.string.label_track_length, MusicUtil.getReadableDurationString(song.duration))
                } catch (e: InvalidAudioFrameException) {
                    Log.e(TAG, "error while reading the song file", e)
                    trackLength.text = makeTextWithTitle(context, R.string.label_track_length, MusicUtil.getReadableDurationString(song.duration))
                }
            } else {
                // fallback
                fileName.text = makeTextWithTitle(context, R.string.label_file_name, song.title)
                trackLength.text = makeTextWithTitle(context, R.string.label_track_length, MusicUtil.getReadableDurationString(song.duration))
            }
        }
        return dialog
    }

    companion object {
        val TAG: String = SongDetailDialog::class.java.simpleName
        @JvmStatic
        fun create(song: Song?): SongDetailDialog {
            val dialog = SongDetailDialog()
            val args = Bundle()
            args.putParcelable("song", song)
            dialog.arguments = args
            return dialog
        }

        private fun makeTextWithTitle(context: Context, titleResId: Int, text: String): Spanned {
            return Html.fromHtml("<b>" + context.resources.getString(titleResId) + ": " + "</b>" + text, Html.FROM_HTML_MODE_COMPACT)
        }
        private fun makeTextWithTitle(context: Context, title: String, text: String): Spanned {
            return Html.fromHtml("<b>$title: </b>$text", Html.FROM_HTML_MODE_COMPACT)
        }//Todo Remove

        private fun getFileSizeString(sizeInBytes: Long): String {
            val fileSizeInKB = sizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024
            return "$fileSizeInMB MB"
        }
    }
}
