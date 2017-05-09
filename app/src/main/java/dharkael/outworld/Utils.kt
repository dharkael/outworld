package dharkael.outworld

import android.os.Bundle


//fun Bundle.putString(key: String? , value: String?): Bundle {
//    this.putString(key, value)
//    return this
//}

fun Bundle.builder(): BundleBuilder {
    return BundleBuilder(this)
}
class BundleBuilder(val bundle: Bundle) {

    fun putString(key: String? , value: String?): BundleBuilder {
        bundle.putString(key, value)
        return this
    }


}