package dharkael.outworld.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.controller_next_trips.view.*
import dharkael.outworld.R
import dharkael.outworld.builder
import dharkael.outworld.io.IOManager

class NextTripsController : BaseController {

    constructor(stop: String, ioManager: IOManager): this(Bundle()
            .builder()
            .putString(KEY_STOP, stop)
            .bundle) {
        this.ioManager = ioManager
    }

    constructor(args: Bundle): super(args){
        stop = args.getString(KEY_STOP)?:""
    }

    val stop: String
    lateinit private var ioManager: IOManager
    private var compositeDisposable = CompositeDisposable()
    companion object {
        val KEY_STOP = "NextTripsController.stop"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_next_trips, container, false)
        view.next_trips_text.text = "Hello NextTripsController $stop"
        Log.i("NextTrips", "onCreateView context ${container.context}")
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        compositeDisposable = CompositeDisposable()
    }

    override fun onDetach(view: View) {
        compositeDisposable.clear()
        super.onDetach(view)
    }
    fun retrieveNextTrips(){

    }
}