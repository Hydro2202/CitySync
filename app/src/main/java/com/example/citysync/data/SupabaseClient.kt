package com.example.citysync.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://fingxcgtgqtrblnfmbgy.supabase.co",
        supabaseKey = "sb_publishable_i9AfJgSJkPG92IZW_eUUIQ_jRGb8M_B"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
