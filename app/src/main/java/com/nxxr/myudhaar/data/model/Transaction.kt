package com.nxxr.myudhaar.data.model

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val personId: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val isCredit: Boolean = false // true = returned, false = borrowed
)
