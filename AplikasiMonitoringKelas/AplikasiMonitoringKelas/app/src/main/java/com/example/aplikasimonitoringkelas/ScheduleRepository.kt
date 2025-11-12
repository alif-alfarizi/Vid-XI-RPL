package com.example.aplikasimonitoringkelas

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// --- Step 1: Create a Singleton Repository ---
// This object will hold our schedule list and be accessible from anywhere in the app.
// It acts like a temporary, in-memory database.
object ScheduleRepository {
    // Using mutableStateListOf so that Composables automatically update when the list changes.
    private val _scheduleList = mutableStateListOf(
        ScheduleItem(1, "Senin", "07:00-08:30", "Matematika", "X RPL", "Bu Tika"),
        ScheduleItem(2, "Selasa", "09:00-10:30", "Bahasa Inggris", "XI RPL", "Pak Budi"),
        ScheduleItem(3, "Rabu", "11:00-12:30", "Fisika", "XII RPL", "Pak Agus")
    )

    // Expose the list as a read-only List
    val scheduleList: List<ScheduleItem>
        get() = _scheduleList

    fun addSchedule(item: ScheduleItem) {
        _scheduleList.add(item.copy(id = (_scheduleList.maxOfOrNull { it.id } ?: 0) + 1))
    }

    fun updateSchedule(item: ScheduleItem) {
        val index = _scheduleList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            _scheduleList[index] = item
        }
    }

    fun deleteSchedule(item: ScheduleItem) {
        _scheduleList.removeIf { it.id == item.id }
    }
}

// --- Step 2: Create a ViewModel (Optional but recommended) ---
// This helps in managing UI-related data and surviving configuration changes.
class ScheduleViewModel : ViewModel() {
    // The ViewModel gets its data from the repository.
    val schedules: List<ScheduleItem> = ScheduleRepository.scheduleList

    // Functions to modify data are called on the repository.
    fun addSchedule(item: ScheduleItem) {
        ScheduleRepository.addSchedule(item)
    }

    fun updateSchedule(item: ScheduleItem) {
        ScheduleRepository.updateSchedule(item)
    }

    fun deleteSchedule(item: ScheduleItem) {
        ScheduleRepository.deleteSchedule(item)
    }
}
