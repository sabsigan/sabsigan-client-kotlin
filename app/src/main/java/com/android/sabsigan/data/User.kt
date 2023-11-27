package com.android.sabsigan.data

import java.io.Serializable

data class User(
    val id: String = "",
    var name: String = "",
    var state: String = "",
    var image: String = "",
    var current_wifi: String = "",
    val created_at: String = "",
    var updated_at: String = "",
    var last_active: String = "",
    var online: Boolean = false,
    ) : Serializable