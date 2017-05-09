package dharkael.outworld.io.network.models

import com.squareup.moshi.Json


data class GetNextTripsForStopResult(
        @Json(name="StopNo") val stopNo: String,
        @Json(name="StopLabel") val stopLabel: String,
        @Json(name="Error") val error: String,
        @Json(name="Route") val routeDirection: RouteDirection
        )




/*
 "StopNo":"7659",
      "StopLabel":"BANK FIFTH",
      "Error":"",

 */