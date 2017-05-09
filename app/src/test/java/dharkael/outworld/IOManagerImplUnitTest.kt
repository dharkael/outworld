package dharkael.outworld

import org.mockito.Mockito
import dharkael.outworld.io.IOManager
import dharkael.outworld.io.IOManagerImpl
import dharkael.outworld.io.IOManagerImpl.Companion.HEADER_E_TAG
import dharkael.outworld.io.IOManagerImpl.Companion.HEADER_LAST_MODIFIED
import dharkael.outworld.io.network.OCTranspoAPI

class IOManagerImplUnitTest {

    inline fun <reified T: Any> mock() = Mockito.mock(T::class.java)


}
