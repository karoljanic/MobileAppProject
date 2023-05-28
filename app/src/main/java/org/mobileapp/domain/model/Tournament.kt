package org.mobileapp.domain.model

import com.google.firebase.database.Exclude

data class Tournament(
    val id: String? = null,
    val name: String? = null,
    val ownerName: String? = null,
    val ownerUID: String? = null,
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "ownerName" to ownerName,
            "ownerUID" to ownerUID
        )
    }
}
