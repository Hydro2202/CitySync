package com.example.citysync.data.repository

import com.example.citysync.data.SupabaseClient
import com.example.citysync.data.model.Report
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportRepository {

    private val postgrest = SupabaseClient.client.postgrest

    suspend fun getReports(userId: String? = null): List<Report> = withContext(Dispatchers.IO) {
        postgrest["reports"]
            .select(columns = Columns.ALL) {
                if (userId != null) {
                    filter {
                        eq("user_id", userId)
                    }
                }
            }
            .decodeList<Report>()
    }

    fun countReportsThisMonth(reports: List<Report>): Int {
        val monthPrefix = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        return reports.count { report ->
            report.createdAt?.take(7) == monthPrefix
        }
    }

    suspend fun getReportById(id: String): Report? = withContext(Dispatchers.IO) {
        postgrest["reports"]
            .select(columns = Columns.ALL) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<Report>()
    }

    suspend fun createReport(report: Report) = withContext(Dispatchers.IO) {
        postgrest["reports"].insert(report)
    }

    suspend fun updateReport(id: String, report: Report) = withContext(Dispatchers.IO) {
        postgrest["reports"].update(report) {
            filter {
                eq("id", id)
            }
        }
    }

    suspend fun deleteReport(id: String) = withContext(Dispatchers.IO) {
        postgrest["reports"].delete {
            filter {
                eq("id", id)
            }
        }
    }

    suspend fun deleteUserReports(userId: String) = withContext(Dispatchers.IO) {
        postgrest["reports"].delete {
            filter {
                eq("user_id", userId)
            }
        }
    }
}
