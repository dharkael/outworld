package dharkael.outworld.db.model

import android.os.Parcelable
import io.requery.*

@Entity
interface Stop : Parcelable, Persistable {

    @get:Key
    val id: String

    val code:  String

    val name: String

    val latitude: Double

    val longitude: Double

    val locationType: Int

}


//stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type
//AA010,8767,"SUSSEX / RIDEAU FALLS",,45.439869,-75.695839,,,0