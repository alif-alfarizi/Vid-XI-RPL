package com.example.aplikasimonitoringkelas

/**
 * Data class to hold schedule information.
 * This is the single source of truth for the schedule model.
 *
 * @param id Unique identifier for the schedule item.
 * @param hari Day of the week (e.g., "Senin").
 * @param jam Time of the class (e.g., "07:00 - 08:30").
 * @param mataPelajaran The subject being taught.
 * @param kelas The class group (e.g., "X RPL").
 * @param guru The name of the teacher.
 */
data class ScheduleItem(
    val id: Int,
    val hari: String,
    val jam: String,
    val mataPelajaran: String,
    val kelas: String,
    val guru: String
)
