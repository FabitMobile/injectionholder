package ru.fabit.injectionholder

import android.app.Application
import ru.fabit.injectionholder.callback.ComponentCallbackImpl
import ru.fabit.injectionholder.callback.ComponentCreationMode
import ru.fabit.injectionholder.registry.LifecycleCallbackRegistryImpl
import ru.fabit.injectionholder.registry.LifecycleCallbacksRegistry
import ru.fabit.injectionholder.storage.ComponentsStore

class InjectionHolderSoft private constructor(lifecycleCallbacksRegistry: LifecycleCallbacksRegistry) {

    companion object {
        @JvmStatic
        val instance by lazy {
            InjectionHolderSoft(
                LifecycleCallbackRegistryImpl(
                    ComponentCreationMode.SOFT
                )
            )
        }

        fun init(app: Application) {
            instance.componentCallback.addLifecycleCallbackListeners(app)
        }
    }

    private val componentsStore by lazy { ComponentsStore() }

    private val componentCallback: ComponentCallbackImpl by lazy {
        ComponentCallbackImpl(
            componentsStore,
            lifecycleCallbacksRegistry
        )
    }

    fun removeComponent(componentClass: Class<*>) = componentsStore.remove(componentClass)

    fun <T> clearComponent(owner: ComponentOwner<T>) = componentCallback.clearComponent(owner)

    fun <T> getComponent(owner: ComponentOwner<T>): T = componentCallback.getComponent(owner)

    fun <T> getComponentOwnerLifeCycle(componentOwner: ComponentOwner<T>): ComponentOwnerLifecycle =
        componentCallback.getCustomOwnerLifecycle(componentOwner)

    @JvmOverloads
    fun <T> findComponent(
        componentClass: Class<T>,
        componentBuilder: (() -> T)? = null
    ): T = componentsStore.findComponent(componentClass, componentBuilder)

}