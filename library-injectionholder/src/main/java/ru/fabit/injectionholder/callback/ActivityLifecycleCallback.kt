package ru.fabit.injectionholder.callback

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import ru.fabit.injectionholder.ComponentOwner

class ActivityLifecycleCallback(
    private val componentCallback: ComponentCallback,
    private val componentCreationMode: ComponentCreationMode
) : Application.ActivityLifecycleCallbacks {

    private companion object {
        const val IS_FIRST_LAUNCH = "injectionholder.callback.IS_FIRST_LAUNCH"
    }

    private fun isFirstLaunch(outState: Bundle?): Boolean =
        outState?.getBoolean(IS_FIRST_LAUNCH, true) ?: true

    private fun setFirstLaunch(outState: Bundle?, isFirstLaunch: Boolean) {
        outState?.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch)
    }

    private fun addInjectionIfNeed(activity: Activity, outState: Bundle?) {
        if (activity is ComponentOwner<*>) {
            if (isFirstLaunch(outState)) {
                componentCallback.onRemoveInjection(activity)
            }
            componentCallback.onAddInjection(activity)
        }

        (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.registerFragmentLifecycleCallbacks(
                SupportFragmentLifecycleCallback(
                    componentCallback,
                    componentCreationMode
                ), false
            )
    }

    private fun removeInjectionIfNeed(activity: Activity) {
        if (activity is ComponentOwner<*> && activity.isFinishing) {
            componentCallback.onRemoveInjection(activity)
        }
    }

    override fun onActivityCreated(activity: Activity, outState: Bundle?) {
        addInjectionIfNeed(activity, outState)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
        removeInjectionIfNeed(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        setFirstLaunch(outState, isFirstLaunch = false)
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}