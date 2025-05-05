package com.nxxr.myudhaar.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nxxr.myudhaar.data.model.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: throw Exception("User not logged in")
    private val userDoc get() = firestore.collection("users").document(uid)



    // --------------------- Persons ---------------------
    suspend fun addPerson(person: Person) {
        userDoc.collection("persons").document(person.id).set(person).await()
    }

    suspend fun getAllPersons(): List<Person> {
        val snapshot = userDoc.collection("persons").get().await()
        return snapshot.toObjects(Person::class.java)
    }

    suspend fun deletePerson(personId: String) {
        userDoc.collection("persons").document(personId).delete().await()
    }

    // --------------------- Transactions ---------------------
    suspend fun addTransaction(transaction: Transaction) {
        userDoc.collection("persons")
            .document(transaction.personId)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction)
            .await()
    }

    suspend fun getTransactionsForPerson(personId: String): List<Transaction> {
        val snapshot = userDoc.collection("persons")
            .document(personId)
            .collection("transactions")
            .get()
            .await()
        return snapshot.toObjects(Transaction::class.java)
    }
}
