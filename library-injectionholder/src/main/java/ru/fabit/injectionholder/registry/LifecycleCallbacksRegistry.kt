package ru.fabit.injectionholder.registry

import android.app.Application
import ru.fabit.injectionholder.callback.ComponentCallback

interface LifecycleCallbacksRegistry {
    fun registerLifecycleCallbacks(app: Application, componentCallback: ComponentCallback)
}