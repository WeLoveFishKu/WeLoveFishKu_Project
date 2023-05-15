package id.fishku.fishersellercore.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import id.fishku.fishersellercore.util.Constants.SIMPLE_DATE_FORMAT
import id.fishku.fishersellercore.util.Constants.SUFFIX_IMG
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

val timeStamp: String = SimpleDateFormat(
    SIMPLE_DATE_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

/**
 * Create custom temp file
 *
 * @param context
 * @return
 */
fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, SUFFIX_IMG, storageDir)
}

/**
 * Uri to file
 *
 * @param selectedImg
 * @param context
 * @return
 */
fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

/**
 * Capitalize words
 *
 * @return
 */
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
    it.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.ROOT
    ) else it.toString()
} }