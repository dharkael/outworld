package dharkael.outworld.controller

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.bluelinelabs.conductor.RouterTransaction
import com.jakewharton.rxbinding2.widget.editorActionEvents
import com.jakewharton.rxbinding2.widget.textChangeEvents
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.requery.Persistable
import io.requery.android.QueryRecyclerAdapter
import io.requery.kotlin.like
import io.requery.query.Result
import io.requery.reactivex.KotlinReactiveEntityStore
import kotlinx.android.synthetic.main.controller_stop_list.view.*
import kotlinx.android.synthetic.main.home_stop_item.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import dharkael.outworld.App
import dharkael.outworld.R
import dharkael.outworld.db.model.StopEntity
import dharkael.outworld.io.IOManager
import dharkael.outworld.io.network.OCTranspoAPI
import dharkael.outworld.io.network.OCTranspoAPIImpl
import dharkael.outworld.io.network.OCTranspoService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface OnItemClickListener<in I> {
    fun onItemClick(item: I)
}

class StopListController : BaseController, OnItemClickListener<StopEntity> {

    companion object {
        val GOOGLE_TRANSIT_ZIP = "google_transit.zip"

        val transpoAPI: OCTranspoAPI

        init {
            val BASE_URL = "https://api.octranspo1.com/v1.2/"
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            val service = retrofit.create<OCTranspoService>(OCTranspoService::class.java)
            transpoAPI = OCTranspoAPIImpl(service)
        }


    }

    constructor(ioManager: IOManager) {
        this.ioManager = ioManager
    }

    constructor(args: Bundle?) : super(args)

    lateinit private var ioManager: IOManager
    val adapter by lazy {
        StopAdapter(data, this)
    }
    private var compositeDisposable = CompositeDisposable()
    val data: KotlinReactiveEntityStore<Persistable> by lazy {
        (applicationContext as App).dataStore
    }

    val executor: ExecutorService = Executors.newSingleThreadExecutor()


    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        val view = inflater.inflate(R.layout.controller_stop_list, container, false)
        val context = container.context


        view.stop_list_recycler_view.adapter = adapter
        view.stop_list_recycler_view.layoutManager = LinearLayoutManager(context)
        adapter.setExecutor(executor)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        compositeDisposable = CompositeDisposable()
        hideSoftKeyboard()
        bindViews(view)
        adapter.queryAsync()
    }

    override fun onDestroy() {
        executor.shutdown()
        adapter.close()
        super.onDestroy()
    }

    override fun onDetach(view: View) {
        compositeDisposable.clear()
        super.onDetach(view)
    }

    fun bindViews(view: View) {

        compositeDisposable.apply {

            add(view.stop_list_filter.editorActionEvents(Predicate { it.actionId() == EditorInfo.IME_ACTION_DONE })
                    .subscribe {
                        //val stop = it.view().text.toString()
                        hideSoftKeyboard()
                       // getRouteSummaryForStop(stop)

                    }
            )
            val filter: Observable<String> = view.stop_list_filter
                    .textChangeEvents()
                    .map { it.text().toString() }.debounce(200, TimeUnit.MILLISECONDS)
            add(
                adapter.subscribeFilter(filter)
            )
        }
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun getRouteSummaryForStop(stop: String) {
        val routerTransaction
                = RouterTransaction.with(NextTripsController(stop = stop, ioManager = ioManager))
        router.pushController(routerTransaction)
    }

    override fun onItemClick(item: StopEntity) {
        getRouteSummaryForStop(item.id)
    }


    class StopAdapter(val data: KotlinReactiveEntityStore<Persistable>, val listener: OnItemClickListener<StopEntity>) : QueryRecyclerAdapter<StopEntity, Holder>() {
        override fun onBindViewHolder(item: StopEntity, holder: Holder, position: Int) {
            holder.root.setOnClickListener { listener.onItemClick(item) }
            holder.root.home_stop_item_name.text = item.name
            holder.root.home_stop_item_code.text = item.code

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.home_stop_item, parent, false)

            val holder = Holder(view)
            return holder

        }

        override fun performQuery(): Result<StopEntity> {
            return data.select(StopEntity::class)
                    .where(StopEntity::name.like("%$filter%").or(StopEntity::code.like("$filter%")) )
                    .orderBy(StopEntity.NAME).get()
        }

        private var filter:String =""
        private var disposable:Disposable? = null
        fun subscribeFilter(filter: Observable<String>): Disposable{
            disposable?.dispose()
            val response  = filter.subscribe {
                this.filter = it
                queryAsync()
            }
            disposable = response
           return response
        }
    }

    class Holder(val root: View) : RecyclerView.ViewHolder(root)
}
