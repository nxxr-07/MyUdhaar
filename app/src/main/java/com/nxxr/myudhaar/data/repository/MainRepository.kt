package com.nxxr.myudhaar.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nxxr.myudhaar.data.model.Person
import com.nxxr.myudhaar.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.nxxr.myudhaar.data.model.Supplier
import kotlinx.coroutines.tasks.await

class MainRepository(
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    private fun getUserId(): String = auth.currentUser?.uid ?: throw Exception("User not logged in")

    private fun userRef() = db.collection("users").document(getUserId())
    private fun personsRef() = userRef().collection("persons")
    private fun suppliersRef() = userRef().collection("suppliers")

    // ------------------ PERSON OPERATIONS ------------------

    suspend fun addPerson(person: Person): Result<Unit> = runCatching {
        val docRef = personsRef().document()
        val personWithId = person.copy(id = docRef.id)
        docRef.set(personWithId).await()
    }

    suspend fun updatePerson(person: Person): Result<Unit> = runCatching {
        if (person.id.isBlank()) throw IllegalArgumentException("Person ID cannot be blank")
        personsRef().document(person.id).set(person).await()
    }

    suspend fun fetchAllPersons(): Result<List<Person>> = runCatching {
        personsRef().get().await().toObjects(Person::class.java)
    }

    // ------------------ TRANSACTION OPERATIONS ------------------

    suspend fun addTransaction(personId: String, transaction: Transaction): Result<Unit> = runCatching {
        val transactionRef = personsRef()
            .document(personId)
            .collection("transactions")
            .document()

        val transactionWithId = transaction.copy(id = transactionRef.id)
        transactionRef.set(transactionWithId).await()
    }

    suspend fun addSupplierTransaction(personId: String, transaction: Transaction): Result<Unit> = runCatching {
        val transactionRef = suppliersRef()
            .document(personId)
            .collection("transactions")
            .document()

        val transactionWithId = transaction.copy(id = transactionRef.id)
        transactionRef.set(transactionWithId).await()
    }

    suspend fun updateTransaction(personId: String, transaction: Transaction): Result<Unit> = runCatching {
        if (transaction.id.isBlank()) throw IllegalArgumentException("Transaction ID cannot be blank")
        personsRef()
            .document(personId)
            .collection("transactions")
            .document(transaction.id)
            .set(transaction)
            .await()
    }

    suspend fun fetchTransactions(personId: String): Result<List<Transaction>> = runCatching {
        personsRef()
            .document(personId)
            .collection("transactions")
            .get()
            .await()
            .toObjects(Transaction::class.java)
    }

    // ------------------ SUPPLIER TRANSACTION OPERATIONS ------------------
    suspend fun fetchSupplierTransactions(supplierId: String): Result<List<Transaction>> = runCatching {
        suppliersRef()
            .document(supplierId)
            .collection("transactions")
            .get()
            .await()
            .toObjects(Transaction::class.java)
    }


    // ------------------ SUPPLIER OPERATIONS ------------------
    suspend fun addSupplier(supplier: Supplier): Result<Unit> = runCatching {
        val docRef = suppliersRef().document()
        val personWithId = supplier.copy(id = docRef.id)
        docRef.set(personWithId).await()
    }

    suspend fun updateSupplier(supplier: Supplier): Result<Unit> = runCatching {
        if (supplier.id.isBlank()) throw IllegalArgumentException("Person ID cannot be blank")
        suppliersRef().document(supplier.id).set(supplier).await()
    }

    suspend fun fetchAllSuppliers(): Result<List<Supplier>> = runCatching {
        suppliersRef().get().await().toObjects(Supplier::class.java)
    }
}

