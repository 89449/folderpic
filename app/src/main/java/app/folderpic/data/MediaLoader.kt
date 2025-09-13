package app.folderpic.data

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class Folder(
    val id: Long,
    val name: String,
    val count: Int,
    val thumbnailUri: Uri
)

data class MediaItem(
    val id: Long,
    val size: Long,
    val dateAdded: Long,
    val duration: Long,
    val name: String,
    val mimeType: String,
    val uri: Uri,
    val width: Int,
    val height: Int
)

class MediaLoader(private val context: Context) {
    suspend fun getMediaFolders(): List<Folder> = withContext(Dispatchers.IO) {
        val folders = mutableMapOf<Long, Folder>()
        val collection = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)
            val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdCol)
                val bucketName = cursor.getString(bucketNameCol) ?: ""
                val id = cursor.getLong(idCol)

                val currentFolder = folders[bucketId]
                if (currentFolder == null) {
                    val uri = Uri.withAppendedPath(collection, id.toString())
                    folders[bucketId] = Folder(
                        id = bucketId,
                        name = bucketName,
                        count = 1,
                        thumbnailUri = uri
                    )
                } else {
                    folders[bucketId] = currentFolder.copy(count = currentFolder.count + 1)
                }
            }
        }
        return@withContext folders.values.toList()
    }

    suspend fun getMediaForFolder(folderId: Long): List<MediaItem> = withContext(Dispatchers.IO) {
        val mediaItems = mutableListOf<MediaItem>()
        val collection = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DURATION,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.RELATIVE_PATH
        )

        val selection = "${MediaStore.Files.FileColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(folderId.toString())
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeTypeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val size = cursor.getLong(sizeCol)
                val dateAdded = cursor.getLong(dateAddedCol)
                val duration = cursor.getLong(durationCol)
                val name = cursor.getString(nameCol)
                val mimeType = cursor.getString(mimeTypeCol)
                val width = cursor.getInt(widthCol)
                val height = cursor.getInt(heightCol)

                if (mimeType.startsWith("image/") || mimeType.startsWith("video/")) {
                    val uri = Uri.withAppendedPath(collection, id.toString())
                    mediaItems.add(
                        MediaItem(
                            id = id,
                            size = size,
                            dateAdded = dateAdded,
                            duration = duration,
                            name = name,
                            mimeType = mimeType,
                            uri = uri,
                            width = width,
                            height = height
                        )
                    )
                }
            }
        }
        return@withContext mediaItems
    }
}