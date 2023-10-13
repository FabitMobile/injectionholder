package ru.fabit.injectionholder.callback

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.fabit.injectionholder.ComponentOwner
import java.util.concurrent.atomic.AtomicBoolean

class SupportFragmentLifecycleCallback(
    private val componentCallback: ComponentCallback,
    private val componentCreationMode: ComponentCreationMode
) : FragmentManager.FragmentLifecycleCallbacks() {

    private val isInSaveState = AtomicBoolean(false)

    private fun addInjectionIfNeed(fragment: Fragment?) {
        if (fragment is ComponentOwner<*>) {
            if (!isInSaveState.get() && componentCreationMode == ComponentCreationMode.AGGRESSIVE) {
                componentCallback.onRemoveInjection(fragment)
            }
            componentCallback.onAddInjection(fragment)
        }
        fragment?.childFragmentManager
            ?.registerFragmentLifecycleCallbacks(
                SupportFragmentLifecycleCallback(
                    componentCallback,
                    componentCreationMode
                ),
                false
            )
    }

    private fun removeInjectionIfNeed(fragment: Fragment?) {

        var anyParentIsRemoving = false
        var parent = fragment?.parentFragment
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving
            parent = parent.parentFragment
        }

        if (fragment !is ComponentOwner<*>) {
            return
        }

        if (fragment.activity?.isFinishing == true) {
            if (!isInSaveState.get()) {
                componentCallback.onRemoveInjection(fragment)
            }
            return
        }

        if (isInSaveState.get()) {
            isInSaveState.set(false)
            return
        }


        if (fragment.isRemoving || anyParentIsRemoving) {
            componentCallback.onRemoveInjection(fragment)
        }
    }


    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        addInjectionIfNeed(f)
        super.onFragmentAttached(fm, f, context)
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        isInSaveState.set(false)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        isInSaveState.set(false)
    }

    override fun onFragmentActivityCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState)

        if (savedInstanceState != null) {
            isInSaveState.set(true)
        }
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        removeInjectionIfNeed(f)
    }

}