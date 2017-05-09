package dharkael.outworld.io.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import dharkael.outworld.BuildConfig
import dharkael.outworld.io.network.models.GetNextTripsForStopResult
import dharkael.outworld.io.network.models.GetRouteSummaryForStopResult


interface OCTranspoService {

    @GET
    @Streaming
    fun getFromUrl(@Url url:String): Observable<Response<ResponseBody>>

    @HEAD
    fun headFromUrl(@Url url:String): retrofit2.Call<Void>
    /*

1) GetRouteSummaryForStop
Retrieves the routes for a given stop number

URL: https://api.octranspo1.com/v1.2/GetRouteSummaryForStop

Parameters:

appID: Required.?Generated in the developer portal after registration.
apiKey: Required. Generated in the developer portal after registration.
stop: Required. 4-digit stop number found on bus stops.
A full list of stops can be downloaded here: http://www.octranspo1.com/files/google_transit.zip
     */
    @FormUrlEncoded
    @POST("GetRouteSummaryForStop")
    fun GetRouteSummaryForStop(
            @Field("stop") stop: String,
            @Field("appID") appID: String = BuildConfig.OCT_APP_ID,
            @Field("apiKey") apiKey: String = BuildConfig.OCT_API_KEY
    ): Observable<GetRouteSummaryForStopResult>

    /*

    GetNextTripsForStop
Retrieves next three trips on the routeDirection for a given stop number

URL: https://api.octranspo1.com/v1.2/GetNextTripsForStop

Parameters:

appID: Required.??Generated in the developer portal after registration.
apiKey: Required. Generated in the developer portal after registration.
routeNo: Required. Bus routeDirection number.
stop: Requited. 4-digit stop number found on bus stops. A full list of stops can be downloaded here:
    http://data.ottawa.ca/dataset/oc-transpo-schedules
     */

    @FormUrlEncoded
    @POST("GetNextTripsForStop")
    fun GetNextTripsForStop(
            @Field("stop") stop: String,
            @Field("route") route: String,
            @Field("appID") appID: String = BuildConfig.OCT_APP_ID,
            @Field("apiKey") apiKey: String = BuildConfig.OCT_API_KEY
    ) : Observable<GetNextTripsForStopResult>
}