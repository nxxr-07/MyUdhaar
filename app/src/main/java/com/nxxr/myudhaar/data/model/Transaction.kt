package com.nxxr.myudhaar.data.model

import java.util.UUID
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName


@IgnoreExtraProperties
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val personId: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),

    @get:PropertyName("isCredit")
    @set:PropertyName("isCredit")
    var isCredit: Boolean = false,

    val timestamp: Long = System.currentTimeMillis()
)
