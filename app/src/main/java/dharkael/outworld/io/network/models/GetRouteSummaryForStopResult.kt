package dharkael.outworld.io.network.models

import com.squareup.moshi.Json

data class GetRouteSummaryForStopResult(
        @Json(name="StopNo") val stop: String,
        @Json(name = "StopDescription") val stopDescription: String,
        @Json(name = "Error") val error: String,
        @Json(name = "Routes") val routes: List<Route>
)