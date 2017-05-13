package dharkael.outworld.db.model

import io.requery.*


@Table(name = "stop_time")
@Entity
interface StopTime {
    @get:Key @get:Generated
    val id: Int

    @get:Column(name = "trip_id")
    val tripId: String

    @get:Column(name = "arrival_time")
    val arrivalTime:String

    @get:Column(name = "departure_time")
    val departureTime:String

    @get:Column(name = "stop_id")
    val stopId:String

    @get:Column(name = "stop_sequence")
    val stopSequence:Int

    @get:Column(name = "pickup_type")
    val pickupType:Int

    @get:Column(name = "drop_off_type")
    val dropOffType:Int

}

//trip_id	                            arrival_time	departure_time	stop_id	stop_sequence	pickup_type	drop_off_type
//47258704-APR17-APRDA17-Weekday-54	    5:48:00	        5:48:00	        RF900	1	            0	        0