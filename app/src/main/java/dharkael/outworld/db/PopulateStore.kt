package dharkael.outworld.db

import android.util.Log
import io.reactivex.Observable
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.apache.commons.csv.CSVFormat.DEFAULT
import org.apache.commons.csv.CSVRecord
import dharkael.outworld.db.model.CalendarDateEntity
import dharkael.outworld.db.model.CalendarEntity
import dharkael.outworld.db.model.StopEntity
import dharkael.outworld.db.model.TripEntity
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipFile

class PopulateStore(val data: KotlinReactiveEntityStore<Persistable>, val zipFile: ZipFile) {

    companion object {
        val STOP_FILENAME = "stops.txt"
        val TRIP_FILENAME = "trips.txt"
        val CALENDAR_DATE_FILENAME = "calendar_dates.txt"
        val CALENDAR_FILENAME = "calendar.txt"
        val dateFormat = SimpleDateFormat("yyyyMMDD",Locale.US)
        val zeroDate = Date(0)
        val stopEntity = StopEntity()
        val tripEntity = TripEntity()
    }

    internal fun String.toDate():Date{
        val date: Date? = dateFormat.parse(this)
        return date?: zeroDate
    }

    internal fun String.toBoolFromInt(): Boolean {
        return this.toInt() != 0
    }

    internal fun <R : Persistable> populateEntity(
            fileName: String, transform: (CSVRecord) -> R,
            predicate: ((R) -> Boolean)? = null
    )
            : Observable<Iterable<R>> {
        //TODO Rather than read the entire list into memory
        //TODO consider processing in chunks/batches

       // Log.i("PopulateStore", "$fileName: begins")
        val entry = zipFile.getEntry(fileName)
        InputStreamReader(zipFile.getInputStream(entry))
                .use { isr ->
                    val csvFormat = DEFAULT.withIgnoreEmptyLines(true)
                            .withAllowMissingColumnNames()
                            .withFirstRecordAsHeader()


                    val entities: List<R> = csvFormat
                            .parse(isr)
                            .map(transform)

                    val filtered = if (predicate != null) entities.filter(predicate) else entities
                    return data.insert(filtered).toObservable().doFinally {
                        Log.i("PopulateStore", "$fileName: ends")
                    }

                }
    }

    internal object StopHeaders {
        val stop_id=0
        val stop_code=1
        val stop_name=2
        //val stop_desc=3
        val stop_lat=4
        val stop_lon=5
        //val zone_id=6
        //val stop_url=7
        val location_type=8
    }

    internal fun mapStopRecordToEntity(record: CSVRecord): StopEntity {
        //Log.i("mapStopRecordToEntity", "${record.recordNumber}")
        var entity = StopEntity()
        record.apply {
            try {

                entity.setId(
                        get(StopHeaders.stop_id))

                entity.setCode(
                        get(StopHeaders.stop_code))

                entity.setName(
                        get(StopHeaders.stop_name))

                entity.setLatitude(
                        get(StopHeaders.stop_lat).toDouble())

                entity.setLongitude(
                        get(StopHeaders.stop_lon).toDouble())

                entity.setLocationType(get(StopHeaders.location_type).toInt())
            }catch (_:Exception){
                entity = stopEntity
            }
        }
        return entity

    }

    fun populateStops(): Observable<Iterable<StopEntity>> {
        val filter:(StopEntity) -> Boolean = {
           it !== stopEntity
        }
        return populateEntity(STOP_FILENAME, this::mapStopRecordToEntity, filter)
    }

    internal object TripHeaders {
        val route_id=0
        val service_id=1
        val trip_id = 2
        val trip_headsign=3
        val direction_id= 4
        val block_id=5
    }

    internal fun mapTripRecordToEntity(record: CSVRecord): TripEntity {
        var entity = TripEntity()
        record.apply {

            try {
                entity.setServiceId(
                        get(TripHeaders.service_id))

                entity.setRouteId(
                        get(TripHeaders.route_id))


                entity.setTripId(
                        get(TripHeaders.trip_id))

                entity.setTripHeadSign(
                        get(TripHeaders.trip_headsign))
                entity.setDirectionId(
                        get(TripHeaders.direction_id).toInt())

                entity.setBlockId(
                        get(TripHeaders.block_id).toInt())
            }catch (_:Exception){
                entity = tripEntity
            }
        }
        return entity

    }

    fun populateTrips(): Observable<Iterable<TripEntity>> {
        val filter:(TripEntity) -> Boolean = {
            it !== tripEntity
        }
        return populateEntity(TRIP_FILENAME, this::mapTripRecordToEntity, filter)
    }

    internal object  CalendarDateHeaders {
        val service_id = 0
        val date = 1
        val exception_type = 2
    }

    internal fun mapCalendarDateRecordToEntity(record: CSVRecord): CalendarDateEntity {
        val entity = CalendarDateEntity()
        record.apply {

           entity.setDate(
                   get(CalendarDateHeaders.date).toDate())

            entity.setExceptionType(
                    get(CalendarDateHeaders.exception_type).toInt())

            entity.setServiceId(
                    get(CalendarDateHeaders.service_id))

        }
        return entity
    }

    fun populateCalendarDate(): Observable<Iterable<CalendarDateEntity>> {
        val filter:(CalendarDateEntity) -> Boolean = {
            it.date.after(zeroDate)
        }
        return populateEntity(CALENDAR_DATE_FILENAME, this::mapCalendarDateRecordToEntity, filter)
    }

    internal object CalendarHeaders {
        val service_id = 0
        val monday = 1
        val tuesday= 2
        val wednesday = 3
        val thursday= 4
        val friday = 5
        val saturday = 6
        val sunday = 7
        val start_date= 8
        val end_date = 9
    }

    internal fun mapCalendarRecordToEntity(record: CSVRecord): CalendarEntity{
        val entity = CalendarEntity()
        record.apply {

            Log.i("PopulateStore",record.toMap().keys.toString() + " ${record.recordNumber}")

            entity.setServiceId(
                    get(CalendarHeaders.service_id))

            entity.setMonday(
                    get(CalendarHeaders.monday).toBoolFromInt())

            entity.setTuesday(
                    get(CalendarHeaders.tuesday).toBoolFromInt())

            entity.setWednesday(
                    get(CalendarHeaders.wednesday).toBoolFromInt())

            entity.setThursday(
                    get(CalendarHeaders.thursday).toBoolFromInt())

            entity.setFriday(
                    get(CalendarHeaders.friday).toBoolFromInt())

            entity.setSaturday(
                    get(CalendarHeaders.saturday).toBoolFromInt())

            entity.setSunday(
                    get(CalendarHeaders.sunday).toBoolFromInt())

            entity.setStartDate(
                    get(CalendarHeaders.start_date).toDate())

            entity.setEndDate(
                    get(CalendarHeaders.end_date).toDate())

        }
        return entity
    }

    fun populateCalendar(): Observable<Iterable<CalendarEntity>> {
        val filter:(CalendarEntity) -> Boolean = {
            it.startDate.after(zeroDate) &&
                    it.endDate.after(zeroDate)
        }
        return populateEntity(CALENDAR_FILENAME, this::mapCalendarRecordToEntity, filter)
    }

    fun populateAll(): Observable<Boolean> {
        //delete previous data
        //the .get().value() is required to realize the delete
        data.delete(StopEntity::class).get().value()
        data.delete(CalendarEntity::class).get().value()
        data.delete(CalendarDateEntity::class).get().value()
        data.delete(TripEntity::class).get().value()

        //TODO memory can we do this serially
        //TODO defer the call of subsequent populates until previous completes

        return populateStops().flatMap {
            populateTrips()
        }.flatMap {
            populateCalendar()
        }.flatMap {
            populateCalendarDate()
        }.flatMap {
            Observable.just(true)
        }

    }





}