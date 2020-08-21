package ru.fabit.injectionholder.callback

import android.app.Application
import ru.fabit.injectionholder.ComponentOwner
import ru.fabit.injectionholder.ComponentOwnerLifecycle
import ru.fabit.injectionholder.registry.LifecycleCallbacksRegistry
import ru.fabit.injectionholder.storage.ComponentsStore

class ComponentCallbackImpl(
    private val componentsStore: ComponentsStore,
    private val lifecycleCallbacksRegistry: LifecycleCallbacksRegistry
) : ComponentCallback {

    override fun <T> onAddInjection(componentOwner: ComponentOwner<T>) {
        val component = getComponent(componentOwner)
        componentOwner.inject(component)
    }

    override fun <T> onRemoveInjection(componentOwner: ComponentOwner<T>) {
        componentsStore.remove(componentOwner.getComponentKey())
    }

    fun addLifecycleCallbackListeners(app: Application) {
        lifecycleCallbacksRegistry.registerLifecycleCallbacks(app, this)
    }

    fun <T> getComponent(owner: ComponentOwner<T>) = initOrGetComponent(owner)

    fun <T> getCustomOwnerLifecycle(owner: ComponentOwner<T>): ComponentOwnerLifecycle {
        return object : ComponentOwnerLifecycle {

            private var isInjected = false

            override fun onCreate() {
                if (!isInjected) {
                    onAddInjection(owner)
                    isInjected = true
                }
            }

            override fun onFinishDestroy() {
                if (isInjected) {
                    onRemoveInjection(owner)
                    isInjected = false
                }
            }
        }
    }


    fun <T> clearComponent(componentOwner: ComponentOwner<T>) {
        componentsStore.remove(componentOwner.getComponentKey())
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> initOrGetComponent(owner: ComponentOwner<T>): T {
        val ownerKey = owner.getComponentKey()

        return if (componentsStore.isExist(ownerKey)) {
            componentsStore[ownerKey] as T
        } else {
            owner.provideComponent().also { newComponent ->
                componentsStore.add(ownerKey, newComponent as Any)
            }
        }
    }

}