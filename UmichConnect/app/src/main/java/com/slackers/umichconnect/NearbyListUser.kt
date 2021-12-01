/*
Object storing info to display user in nearby people list
 */

package com.slackers.umichconnect

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class NearbyListUser(var uid: String? = null, var username: String? = null,
                     imageUrl: String? = null) {
    var imageUrl: String? by ImagePropDelegate(imageUrl)
}

class ImagePropDelegate private constructor ():
    ReadWriteProperty<Any?, String?> {
    private var _value: String? = null
        set(newValue) {
            newValue ?: run {
                field = null
                return
            }
            field = if (newValue == "null" || newValue.isEmpty()) null else newValue
        }

    constructor(initialValue: String?): this() { _value = initialValue }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = _value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        _value = value
    }
}