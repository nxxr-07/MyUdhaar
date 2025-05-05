package com.nxxr.myudhaar.data.model

import java.util.UUID

data class Person(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val phone: String? = null,
    val details: String = ""
)
