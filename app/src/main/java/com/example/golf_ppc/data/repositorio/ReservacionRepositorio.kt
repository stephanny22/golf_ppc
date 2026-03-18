package com.example.golf_ppc.data.repositorio


import com.example.golf_ppc.data.local.AppDatabase
import com.example.golf_ppc.data.local.ReservacionData
import com.example.golf_ppc.data.local.ReservationStatus
import kotlinx.coroutines.flow.Flow

class ReservacionRepositorio (private val db: AppDatabase) {

    private val dao = db.reservacionDAO()

    //Obtiene todas las reservaciones
    fun getAllReservations(): Flow<List<ReservacionData>> =
        dao.getAllReservations()

    //Busca reservaciones
    fun searchReservations(query: String): Flow<List<ReservacionData>> =
        if (query.isBlank()) getAllReservations()
        else dao.searchByClientName(query)

    suspend fun getById(id: Int): ReservacionData? = dao.getReservationById(id)

    suspend fun addReservation(reservation: ReservacionData): String? {
        val conflicts = dao.countConflicts(
            reservation.fieldNumber,
            reservation.date,
            reservation.time
        )
        if (conflicts > 0) {
            return "La cancha ${reservation.fieldNumber} ya está reservada " +
                    "el ${reservation.date} a las ${reservation.time}."
        }
        dao.insertReservation(reservation)
        return null
    }
    suspend fun updateReservation(reservation: ReservacionData): String? {
        val conflicts = dao.countConflicts(
            reservation.fieldNumber,
            reservation.date,
            reservation.time,
            excludeId = reservation.id
        )
        if (conflicts > 0) {
            return "Conflicto: la cancha ${reservation.fieldNumber} ya está " +
                    "reservada el ${reservation.date} a las ${reservation.time}."
        }
        dao.updateReservation(reservation)
        return null
    }

    suspend fun deleteReservation(id: Int) = dao.deleteById(id)

    // Para el Dashboard
    suspend fun getActiveCount() = dao.countByStatus(ReservationStatus.ACTIVA)
    suspend fun getCancelledCount() = dao.countByStatus(ReservationStatus.CANCELADA)
    suspend fun getCompletedCount() = dao.countByStatus(ReservationStatus.COMPLETADA)
    suspend fun getOccupiedCourtsToday(today: String) = dao.occupiedCourtsToday(today)
}