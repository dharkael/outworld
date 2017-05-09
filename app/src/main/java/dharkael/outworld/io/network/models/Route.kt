package dharkael.outworld.io.network.models

import com.squareup.moshi.Json

data class Route(
        @Json(name="RouteNo") val routeNo: Int,
        @Json(name="DirectionID") val directionId: Int,
        @Json(name="Direction") val direction :String,
        @Json(name="RouteHeading") val routeHeading:String,
        @Json(name="Trips") val trips: List<Trip> = emptyList()
)


