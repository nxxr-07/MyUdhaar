package com.nxxr.myudhaar.data.model

import java.util.UUID

data class Supplier(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val phone: String? = null,
    val details: String = "",
    val totalAmount: Double = 0.0
)

