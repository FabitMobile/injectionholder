package ru.fabit.injectionholder.callback

import ru.fabit.injectionholder.ComponentOwner

interface ComponentCallback {

    fun <T> onAddInjection(componentOwner: ComponentOwner<T>)

    fun <T> onRemoveInjection(componentOwner: ComponentOwner<T>)
}