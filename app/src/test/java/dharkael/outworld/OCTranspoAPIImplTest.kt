package dharkael.outworld

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test
import dharkael.outworld.io.network.OCTranspoAPIImpl
import dharkael.outworld.io.network.OCTranspoService
import dharkael.outworld.io.network.models.GetNextTripsForStopResult
import dharkael.outworld.io.network.models.GetRouteSummaryForStopResult
import dharkael.outworld.io.network.models.RouteDirection
import java.util.concurrent.TimeUnit

class OCTranspoAPI_ImplTest {


    @Test
    fun getRouteSummaryForStop_calls_service_GetRouteSummaryForStop_with_given_stop(){
        val stop = "9999"
        val expected = GetRouteSummaryForStopResult(stop = stop, error = "", routes = listOf(), stopDescription = stop)
        val service:OCTranspoService = mock()
        whenever(service.GetRouteSummaryForStop(stop = stop)).thenReturn(Observable.just(expected))

        val octranspo = OCTranspoAPIImpl(service)
        val actualObservable: Observable<GetRouteSummaryForStopResult> = octranspo.getRouteSummaryForStop(stop = stop)

        verify(service).GetRouteSummaryForStop(stop = stop)

        actualObservable.test()
                .awaitDone(5, TimeUnit.SECONDS)
                .assertResult(expected)

    }

    @Test
    fun getNextTripsForStop(){
        val service:OCTranspoService = mock()
        val octranspo =  OCTranspoAPIImpl(service)

        val stop = "9999"
        val route = "Some Route"
        val routeDirection = RouteDirection(routeNo = 1, routeLabel = "Some Label", direction = "NORTH",
                requestProcessingTime = "SomeTime", error = "Error")
        val expected = GetNextTripsForStopResult(stopNo = stop, stopLabel = stop, error = "", routeDirection = routeDirection)
        whenever(service.GetNextTripsForStop(stop = stop, route = route))
                .thenReturn(Observable.just(expected))

        val tripsForStop = octranspo.getNextTripsForStop(stop = stop, route = "Some Route")

        tripsForStop.test()
                .awaitDone(5, TimeUnit.SECONDS)
                .assertResult(expected)

    }

}