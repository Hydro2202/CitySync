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

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount() {
        val user = getCurrentUser()
        if (user != null) {
            try {
                // 1. Delete profile from public.users first
                postgrest["users"].delete {
                    filter {
                        eq("id", user.id)
                    }
                }

                // 2. Call RPC to delete from auth.users
                postgrest.rpc("delete_user_account")
            } catch (e: Exception) {
                // Log or handle error if RPC fails
            } finally {
                auth.signOut()
            }
        }
    }

    fun getCurrentUser() = auth.currentUserOrNull()

    private fun jsonObject(metadata: Map<String, String>): JsonObject = buildJsonObject {
        metadata.forEach { (key, value) ->
            put(key, value)
        }
    }
}
