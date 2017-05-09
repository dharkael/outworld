package dharkael.outworld.dagger.components

import dagger.Component
import dharkael.outworld.MainActivity
import dharkael.outworld.dagger.modules.IOModule


@Component(modules = arrayOf(IOModule::class))
interface IOComponent {
    fun inject(activity: MainActivity)
}