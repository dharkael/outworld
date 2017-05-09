package dharkael.outworld.io

import android.content.Context
import io.reactivex.Observable
import dharkael.outworld.io.network.OCTranspoAPI
import java.io.File
import java.io.InputStream
import java.util.*


interface IOManager {
    fun lastModifiedGTFS(): Observable<GTFSLastModified>
    //fun getGTFSDownload() :Observable<GTFSDownload>
    fun downloadAndSaveGTFS(context: Context):Observable<GTFSLastModified>
    val api: OCTranspoAPI
}
data class GTFSLastModified(val lastModified: Date?, val etag: String?)
data class GTFSDownload(val lastModifiedGTFS: GTFSLastModified, val inputStream: InputStream)
