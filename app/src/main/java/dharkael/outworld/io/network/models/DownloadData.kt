package dharkael.outworld.io.network.models

import okhttp3.Headers
import okhttp3.ResponseBody

data class DownloadData(val headers: Headers, val responseBody: ResponseBody)