package uz.xia.taxigo.ui.participants.chat.tool.photo

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import uz.xia.taxigo.ui.participants.chat.tool.photo.adapter.PhotoInfo
import uz.xia.taxigo.ui.participants.chat.tool.photo.adapter.collection
import uz.xia.taxigo.ui.participants.chat.tool.photo.adapter.projection
import uz.xia.taxigo.ui.participants.chat.tool.photo.adapter.sortOrder
import java.util.*
import java.util.concurrent.TimeUnit

interface IPhotoSelectViewModel {
    val liveImages: LiveData<List<PhotoInfo>>
    fun getImages(context: Context,count: Int)
}

class PhotoSelectViewModel(app: Application)
    : AndroidViewModel(app),
    IPhotoSelectViewModel {
    private var start = 0
    private var areAllLoaded = false

    override val liveImages = MutableLiveData<List<PhotoInfo>>()

    override fun getImages(context: Context,count: Int) {
        val imageCursor: Cursor? = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )
        viewModelScope.launch(Dispatchers.IO) {
            if (areAllLoaded) return@launch
            val res = getImages(imageCursor,start, count)
            start = res?.first ?: 0
            areAllLoaded = res?.second ?: false
            val data = res?.third
            liveImages.postValue(data ?: listOf())
        }
    }

    suspend fun getImages(
        imageCursor: Cursor?,
        start: Int,
        count: Int
    ): Triple<Int, Boolean, List<PhotoInfo>>?{
        val photoList = mutableListOf<PhotoInfo>()
        var index = start
       return withContext(Dispatchers.IO){
            imageCursor.use { cursor ->
                // Cache column indices.
                val idColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val modifyDateColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val sizeColumn = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                while (cursor?.moveToPosition(index) == true) {
                    val id = idColumn?.let { cursor.getLong(it) }
                    val name = nameColumn?.let { cursor.getString(it)}
                    val dateModified = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(modifyDateColumn?:0)))
                    val size = sizeColumn?.let { cursor.getInt(it) }
                    val contentUri = id?.let { ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it) }
                    photoList += contentUri?.let { PhotoInfo(contentUri, name, dateModified, size) }!!
                    index++
                    if ((index - start) == count)
                        break
                }
            }
           val areAllLoaded = (index - start) != count
           Timber.d("getImages ${photoList.size}")
           return@withContext Triple(index, areAllLoaded, photoList)
        }
    }

    /* override suspend fun getImages(
         count: Int, start: Int
     ): Triple<MutableList<Triple<Uri?, String?, Date>>, Boolean, Int> {
         val imagesList = mutableListOf<Triple<Uri?, String?, Date>>()
         var index = start
         // Cache column indices.
         val idColumn = imageCursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
         val nameColumn = imageCursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
         val imageDateModifiedColumn =
             imageCursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
         val sizeColumn = imageCursor?.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
         return withContext(Dispatchers.IO) {
             while (imageCursor?.moveToPosition(index) == true) {
                 val id = idColumn?.let { imageCursor.getLong(it) }
                 val dateModified = Date(
                     TimeUnit.SECONDS.toMillis(
                         imageCursor.getLong(imageDateModifiedColumn ?: 0)
                     )
                 )

                 val displayName = nameColumn?.let { imageCursor.getString(it) }
                 val contentUri = id?.let {
                     ContentUris.withAppendedId(
                         MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it
                     )
                 }
                 index++
                 contentUri.let { imagesList.add(Three(contentUri, displayName, dateModified)) }
                 if (index == count) break
             }
             val areAllLoaded = index == imagesToLoad
             return@withContext Triple(imagesList, areAllLoaded, index)
         }
     }*/
}
