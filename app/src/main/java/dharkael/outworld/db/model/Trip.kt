package dharkael.outworld.db.model

import android.os.Parcelable
import io.requery.*


@Table(name = "trip")
@Entity
interface Trip : Parcelable, Persistable{

    @get:Key
    @get:Column(name = "trip_id")
    val tripId: String

    @get:Column(name = "route_id")
    val routeId: String

    @get:Column(name = "service_id")
    val serviceId: String


    @get:Column(name = "trip_headsign")
    val tripHeadSign: String


    @get:Column(name = "direction_id")
    val directionId: Int


    @get:Column(name = "block_id")
    val blockId: Int
}
//route_id,service_id,trip_id,trip_headsign,direction_id,block_id
//1-274,APR17-APRDA17-Weekday-54,47258704-APR17-APRDA17-Weekday-54,"Ottawa-Rockcliffe",1,5524523
//1-274,APR17-APRDA17-Weekday-54,47258705-APR17-APRDA17-Weekday-54,"South Keys",0,5524523
