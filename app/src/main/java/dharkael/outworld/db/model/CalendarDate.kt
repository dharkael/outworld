package dharkael.outworld.db.model

import android.os.Parcelable
import io.requery.*
import java.util.*


@Table(name = "calendar_date")
@Entity
interface CalendarDate : Parcelable, Persistable {
    @get:Key
    @get:Column(name = "service_id")
    val serviceId: String

    @get:Key
    val date: Date

    @get:Column(name = "exception_type")
    val exceptionType: Int

}

//service_id,date,exception_type
//APR17-APRDA17-Weekday-54,20170522,2
//APR17-SunBik17-Sunday-01,20170522,1