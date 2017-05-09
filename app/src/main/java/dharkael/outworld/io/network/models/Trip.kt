package dharkael.outworld.io.network.models

import com.squareup.moshi.Json

data class Trip(
        @Json(name="TripDestination") val destination: String,
        @Json(name="TripStartTime") val startTime: String,
        @Json(name="AdjustedScheduleTime") val adjustedScheduleTime: String,
        @Json(name="AdjustmentAge") val adjustmentAge: Double,
        @Json(name="LastTripOfSchedule") val lastTripOfSchedule: Boolean,
        @Json(name="BusType") val busType: String,
        @Json(name="GPSSpeed") val GPSSpeed: Double,
        @Json(name="Latitude") val latitude: Double,
        @Json(name="Longitude") val longitude: String
)

