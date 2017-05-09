package dharkael.outworld.io

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import dharkael.outworld.BuildConfig
import dharkael.outworld.io.network.OCTranspoAPI
import java.io.InputStream
import java.io.OutputStream

class IOManagerImpl(val transpoAPI: OCTranspoAPI): IOManager {
    override val api: OCTranspoAPI = transpoAPI

    companion object{
         val HEADER_LAST_MODIFIED = "Last-Modified"
         val HEADER_E_TAG = "ETag"
    }

    override fun lastModifiedGTFS(): Observable<GTFSLastModified> {
        return transpoAPI.
                headersFromUrl(url = BuildConfig.OCT_GTFS_URL)
                .map {
                    GTFSLastModified(
                            lastModified = it.getDate(HEADER_LAST_MODIFIED),
                            etag = it.get(HEADER_E_TAG)
                    )
                }
    }

    override fun downloadAndSaveGTFS(context: Context):Observable<GTFSLastModified> {
        return getGTFSDownload()
                .map {
                    Log.i("downloadAndSave", "before open")
                    val outputStream = context
                            .openFileOutput(BuildConfig.GTFS_FILENAME, Context.MODE_PRIVATE)
                    copyStream(inputStream = it.inputStream, outputStream = outputStream)
                    Log.i("downloadAndSave", "made it past copy stream")
                    it.lastModifiedGTFS
                }.cache() // we only want to download and save once
    }

    fun getGTFSDownload() :Observable<GTFSDownload> {
        return transpoAPI
                .downloadFromUrl(url = BuildConfig.OCT_GTFS_URL)
                .map {
                    val headers = it.headers
                    val last = GTFSLastModified(
                            lastModified = headers.getDate(HEADER_LAST_MODIFIED),
                            etag = headers.get(HEADER_E_TAG)
                    )
                    GTFSDownload(last, it.responseBody.byteStream())
                }
    }

    fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        try {

            val buffer = ByteArray(1024)
            var read = inputStream.read(buffer)
            while (read != -1) {
                outputStream.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
        } finally {
            try {inputStream.close()} catch (_: Throwable){}
            try {outputStream.close()} catch (_: Throwable){}
        }
    }

}