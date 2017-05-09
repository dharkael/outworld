package dharkael.outworld.db.model

import android.os.Parcelable
import io.requery.*
import java.util.*


@Table(name = "calendar")
@Entity
interface Calendar : Parcelable, Persistable {

    @get:Key
    @get:Column(name = "service_id")
    val serviceId: String

    val monday:  Boolean

    val tuesday:  Boolean

    val wednesday:  Boolean

    val thursday:  Boolean

    val friday:  Boolean

    val saturday:  Boolean

    val sunday:  Boolean

    @get:Column(name = "start_date")
    val startDate: Date

    @get:Column(name = "end_date")
    val endDate: Date

}
//service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
//JAN17-JANDA17-Weekday-14,1,1,1,1,1,0,0,20170331,20170421