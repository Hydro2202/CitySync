package com.example.citysync.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
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
        // Step 1: Create the Auth Account
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName)
                put("phone", phone)
                put("address", address)
            }
        }
        
        // Step 2: Manually sync to public.users (Safe Mode)
        val user = getCurrentUser()
        if (user != null) {
            try {
                postgrest["users"].insert(buildJsonObject {
                    put("id", user.id)
                    put("email", email)
                    put("full_name", fullName)
                    put("phone", phone)
                    put("address", address)
                })
            } catch (e: Exception) {
                // If manual sync fails, we don't crash sign-up
                e.printStackTrace()
            }
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
}
