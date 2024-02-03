package uz.xia.taxigo.ui.participants.chat.tool.photo.adapter

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.util.Date

// Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
// app didn't create.

// Container for information about each video.
data class PhotoInfo(
    val uri: Uri?, val name: String?, val modifyDate: Date?, val size: Int?
)

val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    MediaStore.Images.Media.getContentUri(
        MediaStore.VOLUME_EXTERNAL
    )
} else {
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
}

val projection = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.DISPLAY_NAME,
    MediaStore.Images.Media.DATE_MODIFIED,
    MediaStore.Images.Media.SIZE
)

// Show only videos that are at least 5 minutes in duration.
/*
val selection = "${MediaStore.Video.Media.DURATION} >= ?"
*/
/*val selectionArgs = arrayOf(
    TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
)*/

// Display videos in alphabetical order based on their display name.
val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

