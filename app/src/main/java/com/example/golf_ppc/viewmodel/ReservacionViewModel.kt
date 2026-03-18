package com.example.golf_ppc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.golf_ppc.data.local.AppDatabase
import com.example.golf_ppc.data.local.ReservacionData
import com.example.golf_ppc.data.repositorio.ReservacionRepositorio
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class Informacion(
    val activeField: Int = 0,
    val cancelledField: Int = 0,
    val completedField: Int = 0,
    val occupiedFieldsToday: Int = 0,
    val totalFields: Int = 18
        )

@OptIn(ExperimentalCoroutinesApi::class)
class ReservacionViewModel (application: Application): AndroidViewModel(application) {

    private val repositorio = ReservacionRepositorio(
        AppDatabase.getInstance(application)
    )

    // Estado de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Lista filtrada reactiva
    val reservations: StateFlow<List<ReservacionData>> = _searchQuery
        .flatMapLatest { query -> repositorio.searchReservations(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mensajes de error/éxito
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Dashboard
    private val _dashboard = MutableStateFlow(Informacion())
    val dashboard: StateFlow<Informacion> = _dashboard.asStateFlow()

    init {
        refreshDashboard()
        // Refrescar dashboard cuando cambian las reservas
        viewModelScope.launch {
            reservations.collect { refreshDashboard() }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addReservation(reservation: ReservacionData) {
        viewModelScope.launch {
            val error = repositorio.addReservation(reservation)
            _userMessage.value = error ?: "Reserva registrada exitosamente."
        }
    }

    fun updateReservation(reservation: ReservacionData) {
        viewModelScope.launch {
            val error = repositorio.updateReservation(reservation)
            _userMessage.value = error ?: "Reserva actualizada correctamente."
        }
    }

    fun deleteReservation(id: Int) {
        viewModelScope.launch {
            repositorio.deleteReservation(id)
            _userMessage.value = "Reserva eliminada."
        }
    }

    suspend fun getReservationById(id: Int): ReservacionData? =
        repositorio.getById(id)

    fun clearMessage() {
        _userMessage.value = null
    }

    private fun refreshDashboard() {
        viewModelScope.launch {
            _dashboard.value = Informacion(
                activeField = repositorio.getActiveCount(),
                cancelledField = repositorio.getCancelledCount(),
                completedField = repositorio.getCompletedCount(),
                occupiedFieldsToday = repositorio.getOccupiedCourtsToday(
                    getTodayDate()
                )
            )
        }
    }

    private fun getTodayDate(): String {
        val cal = Calendar.getInstance()
        return String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,  // Calendar.MONTH empieza en 0
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }
}