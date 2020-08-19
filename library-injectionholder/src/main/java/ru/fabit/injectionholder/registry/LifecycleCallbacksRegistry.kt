package ru.fabit.injectionholder.registry

import android.app.Application
import ru.fabit.injectionholder.callback.ComponentCallback

internal interface LifecycleCallbacksRegistry {
    fun registerLifecycleCallbacks(app: Application, componentCallback: ComponentCallback)
}