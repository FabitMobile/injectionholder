package ru.fabit.injectionholder.exception

class ComponentNotFoundException(key: String) :
    Throwable("Component of the $key type was not found")