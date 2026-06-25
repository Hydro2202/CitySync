package com.example.citysync.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://fingxcgtgqtrblnfmbgy.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZpbmd4Y2d0Z3F0cmJsbmZtYmd5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODIzMDYwNjYsImV4cCI6MjA5Nzg4MjA2Nn0.2hk2B-yhJElLpVOdUIiQOJHX6yQk7fvHFnj0iFm64ws"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
