package dharkael.outworld

import android.app.Application
import android.os.StrictMode
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import dharkael.outworld.db.model.Models

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
    }

    val dataStore: KotlinReactiveEntityStore<Persistable> by lazy {
        val model = Models.DEFAULT
        val source = DatabaseSource(this, model, 1)

        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE)

           // source.setLoggingEnabled(true)
        }
        val configuration = source.configuration
        KotlinReactiveEntityStore(KotlinEntityDataStore<Persistable>(configuration))
    }
}
