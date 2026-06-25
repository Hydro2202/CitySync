package com.example.citysync.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthManager {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun signIn(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(email: String, password: String, fullName: String, phone: String, address: String) {
        val registrationMetadata = mapOf(
            "full_name" to fullName.trim(),
            "phone" to phone.trim(),
            "address" to address.trim()
        )

        auth.signUpWith(Email) {
            this.email = email.trim()
            this.password = password
            data = jsonObject(registrationMetadata)
        }
    }

    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        address: String
    ) {
        val user = getCurrentUser() ?: throw IllegalStateException("No authenticated user")

        val fullName = "${firstName.trim()} ${lastName.trim()}".trim()
        val trimmedPhone = phone.trim()
        val trimmedAddress = address.trim()
        val trimmedEmail = email.trim()

        auth.updateUser {
            if (trimmedEmail.isNotBlank() && trimmedEmail != user.email) {
                this.email = trimmedEmail
            }
            data = buildJsonObject {
                put("full_name", fullName)
                put("phone", trimmedPhone)
                put("address", trimmedAddress)
            }
        }

        postgrest["users"].update(
            buildJsonObject {
                put("full_name", fullName)
                put("phone", trimmedPhone)
                put("address", trimmedAddress)
                put("email", trimmedEmail)
            }
        ) {
            filter {
                eq("id", user.id)
            }
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount() {
        val user = getCurrentUser() ?: throw IllegalStateException("No authenticated user")

        postgrest["reports"].delete {
            filter {
                eq("user_id", user.id)
            }
        }

        postgrest.rpc("delete_user_account")
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUserOrNull()

    private fun jsonObject(metadata: Map<String, String>): JsonObject = buildJsonObject {
        metadata.forEach { (key, value) ->
            put(key, value)
        }
    }
}
