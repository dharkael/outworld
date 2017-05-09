package dharkael.outworld.io.network.models

import com.squareup.moshi.Json

data class RouteDirection(
        @Json(name="RouteNo") val routeNo: Int,
        @Json(name="RouteLabel") val routeLabel: String,
        @Json(name="Direction") val direction :String,
        @Json(name="Error") val error:String,
        @Json(name="RequestProcessingTime") val requestProcessingTime:String,
        @Json(name="Trips") val trips: List<Trip> = emptyList()
)
