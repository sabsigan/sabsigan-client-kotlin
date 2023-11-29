package com.android.sabsigan.data

import java.io.Serializable

data class SimpleUser(
    val id: String = "",
    var name: String = "",
    var checked: Boolean = false,
) : Serializable {
    constructor(user: User) : this(id = user.id, name = user.name)
}