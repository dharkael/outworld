package dharkael.outworld

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import dharkael.outworld.controller.StopListController
import dharkael.outworld.db.PopulateStore
import dharkael.outworld.io.IOManager
import dharkael.outworld.io.IOManagerImpl
import dharkael.outworld.io.network.OCTranspoAPIImpl
import dharkael.outworld.io.network.OCTranspoService
import java.io.File
import java.util.zip.ZipFile


class MainActivity : AppCompatActivity() {

    val KEY_GTFS_LAST_MODIFIED = "GTFS_LAST_MODIFIED"
    val baseURL: String = "https://api.octranspo1.com/v1.2/"
    val ioManager: IOManager

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        val service = retrofit.create<OCTranspoService>(OCTranspoService::class.java)
        val ocTranspoAPI = OCTranspoAPIImpl(service)
        ioManager = IOManagerImpl(ocTranspoAPI)
    }

    private lateinit var router: Router


    var progressText: String
        get() = (activity_main_progress_text?.text?.toString()) ?: ""
        set(value) {
            activity_main_progress_text?.text = value
        }
    var progressShown: Boolean
        get() {
            val visibility = (activity_main_progress_container?.visibility) ?: View.GONE
            return visibility == View.VISIBLE
        }
        set(value) {
            val visibility = if (value) View.VISIBLE else View.GONE
            activity_main_progress_container?.visibility = visibility
        }

    val sharedPrefs by lazy {
        application?.getSharedPreferences(BuildConfig.APP_PREFS_NAME, Context.MODE_PRIVATE)
    }

    var lastModified: Long
        get() = sharedPrefs?.getLong(KEY_GTFS_LAST_MODIFIED, 0) ?: 0
        set(value) {
            sharedPrefs?.edit()?.putLong(KEY_GTFS_LAST_MODIFIED, value)?.apply()
        }
    val disposables = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        this.apply {
            progressShown = false
            progressText = "Loading MainActivity stuff"
            progressShown = true
            maybePopulateStore()

        }
        router = Conductor.attachRouter(this, controller_container, savedInstanceState)
    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    fun transitionToRoot() {
        if (!router.hasRootController()) {
            val controller = StopListController(ioManager = ioManager)
            router.setRoot(RouterTransaction.with(controller))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    fun maybeDownloadAndSaveGTFS(): Observable<Long> {

        val modified = lastModified

        return ioManager.lastModifiedGTFS().flatMap {
            val newModified = it.lastModified?.time ?: 0
            if (newModified > modified) {
                return@flatMap ioManager.downloadAndSaveGTFS(this).map { it.lastModified?.time ?: 0 }
            } else {
                return@flatMap Observable.just(modified)
            }
        }.doAfterNext {
            lastModified = it
        }

    }

    fun maybePopulateStore() {
        val originalLastModified = lastModified
        maybeDownloadAndSaveGTFS()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if (it > originalLastModified) {
                        val filesDir: File = application.filesDir
                        val file = File(filesDir, StopListController.GOOGLE_TRANSIT_ZIP)
                        val zipFile = ZipFile(file)
                        val populateStore = PopulateStore(data = (applicationContext as App).dataStore, zipFile = zipFile)
                        populateStore.populateAll()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    progressShown = false
                                    zipFile.close()
                                    transitionToRoot()

                                }, {
                                    zipFile.close()
                                })
                    } else {
                        progressShown = false
                        transitionToRoot()
                    }

                }, {
                    Log.e("MainActivity", it.message)

                })
    }
}
