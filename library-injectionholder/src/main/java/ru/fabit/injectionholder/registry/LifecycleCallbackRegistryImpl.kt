package ru.fabit.injectionholder.registry

import android.app.Application
import ru.fabit.injectionholder.callback.ActivityLifecycleCallback
import ru.fabit.injectionholder.callback.ComponentCallback
import ru.fabit.injectionholder.callback.ComponentCreationMode

class LifecycleCallbackRegistryImpl(private val componentCreationMode: ComponentCreationMode) :
    LifecycleCallbacksRegistry {
    override fun registerLifecycleCallbacks(
        app: Application,
        componentCallback: ComponentCallback
    ) {
        app.registerActivityLifecycleCallbacks(
            ActivityLifecycleCallback(
                componentCallback,
                componentCreationMode
            )
        )
    }
}