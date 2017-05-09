package dharkael.outworld.dagger.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import dharkael.outworld.io.IOManager
import dharkael.outworld.io.IOManagerImpl
import dharkael.outworld.io.network.OCTranspoAPI
import dharkael.outworld.io.network.OCTranspoAPIImpl
import dharkael.outworld.io.network.OCTranspoService
import javax.inject.Singleton

@Module
class IOModule(val baseURL: String = "https://api.octranspo1.com/v1.2/") {



    @Provides
    @Singleton
    fun providesOCTranspoService(): OCTranspoService {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create<OCTranspoService>(OCTranspoService::class.java)
    }

    @Provides
    @Singleton
    fun providesOCTranspoAPI(service: OCTranspoService): OCTranspoAPI = OCTranspoAPIImpl(service)

    @Provides
    @Singleton
    fun providesIOManager(api: OCTranspoAPI): IOManager = IOManagerImpl(api)
}