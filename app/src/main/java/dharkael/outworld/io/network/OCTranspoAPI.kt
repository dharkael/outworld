package dharkael.outworld.io.network

import io.reactivex.Observable
import okhttp3.Headers
import dharkael.outworld.io.network.models.DownloadData
import dharkael.outworld.io.network.models.GetNextTripsForStopResult
import dharkael.outworld.io.network.models.GetRouteSummaryForStopResult


interface OCTranspoAPI {
    fun getRouteSummaryForStop(stop: String): Observable<GetRouteSummaryForStopResult>
    fun getNextTripsForStop(stop: String, route: String): Observable<GetNextTripsForStopResult>
    fun downloadFromUrl(url: String): Observable<DownloadData>
    fun headersFromUrl(url: String): Observable<Headers>
}