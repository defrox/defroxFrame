package com.defrox.defroxframe.sliderimage.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.query
import java.util.concurrent.TimeUnit

class LocalImages(ctx: Context) {

    // Container for information about each image.
    data class Image(val uri: Uri,
                     val name: String,
                     val size: Int
    )
    val imageList = mutableListOf<Image>()

    val imageUriList = mutableListOf<Uri>()

    val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
    )
    // Show only iamges that are at least 5 minutes in duration.
    val selection = "${MediaStore.Images.Media.SIZE} <= ?"
    val selectionArgs = arrayOf( (1024*1024*3).toString() )

    // Display images in alphabetical order based on their display name.
    val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

    val query = ctx.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
    )

    /**
     * Get the list of all media images in device
     */
    fun getAllImages(shuffle: Boolean = false): MutableList<Image> {
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                // Get values of columns for a given image.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                imageList += Image(contentUri, name, size)
            }
        }
        if (shuffle) {
            imageList.shuffle()
        }
        return imageList
    }

    fun getAllImagesUri(shuffle: Boolean = false): MutableList<Uri> {
        val images = getAllImages().listIterator()
        while (images.hasNext()) {
            val cursor = images.next()
            imageUriList += cursor.uri
        }
        if (shuffle) {
            imageUriList.shuffle()
        }
        return imageUriList
    }

    fun getAllImagesList(): List<String> {
        val imagesResult = mutableListOf<String>()
        val images = getAllImages(true).listIterator()
        while (images.hasNext()) {
            val cursor = images.next()
            imagesResult.add(cursor.uri.toString())
        }
        return imagesResult.toList()
    }


}