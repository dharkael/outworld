package dharkael.outworld.io.network

import io.reactivex.Observable
import okhttp3.Headers
import retrofit2.HttpException
import dharkael.outworld.io.network.models.DownloadData
import dharkael.outworld.io.network.models.GetNextTripsForStopResult
import dharkael.outworld.io.network.models.GetRouteSummaryForStopResult


class OCTranspoAPIImpl(val service: OCTranspoService) :OCTranspoAPI{

    override fun getRouteSummaryForStop(stop: String): Observable<GetRouteSummaryForStopResult> =
            service.GetRouteSummaryForStop(stop = stop)

    override fun getNextTripsForStop(stop: String, route: String): Observable<GetNextTripsForStopResult> =
            service.GetNextTripsForStop(stop = stop, route = route)

    override fun downloadFromUrl(url: String): Observable<DownloadData> {
        return service.getFromUrl(url = url)
                .map {
                    if (!it.isSuccessful) {
                        throw HttpException(it)
                    }
                    DownloadData(headers = it.headers(), responseBody = it.body())
                }
    }

    override fun headersFromUrl(url: String): Observable<Headers> {
        return Observable.fromCallable {
            val call = service.headFromUrl(url = url)
            val response = call.execute()
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            response.headers()
        }
    }
}