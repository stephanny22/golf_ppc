package com.example.golf_ppc.data.local


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservacionDAO {

    //Obtiene todas las reservaciones
    @Query("SELECT * FROM reservations ORDER BY date ASC, time ASC")
    fun getAllReservations(): Flow<List<ReservacionData>>

    //Obtiene una reservacion dependiendo el id de esta
    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReservationById(id: Int): ReservacionData?

    // Buscar por nombre de cliente (sin importar mayúsculas)
    @Query("SELECT * FROM reservations WHERE clientName LIKE '%' || :query || '%' ORDER BY date ASC")
    fun searchByClientName(query: String): Flow<List<ReservacionData>>

    // Verificar conflicto de cancha: misma cancha, fecha y hora, estado ACTIVA
    @Query("""
        SELECT COUNT(*) FROM reservations 
        WHERE fieldNumber = :courtNumber 
        AND date = :date 
        AND time = :time 
        AND status = 'ACTIVA'
        AND id != :excludeId
    """)
    suspend fun countConflicts(
        courtNumber: Int,
        date: String,
        time: String,
        excludeId: Int = 0
    ): Int

    // Resumen: total reservas por estado ordenadas por estado
    @Query("SELECT COUNT(*) FROM reservations WHERE status = :status ORDER BY status = 'ACTIVA'")
    suspend fun countByStatus(status: ReservationStatus): Int

    // Canchas ocupadas hoy
    @Query("""
        SELECT COUNT(DISTINCT fieldNumber) FROM reservations 
        WHERE date = :today AND status = 'ACTIVA'
    """)
    suspend fun occupiedCourtsToday(today: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReservation(reservation: ReservacionData): Long

    @Update
    suspend fun updateReservation(reservation: ReservacionData)

    @Delete
    suspend fun deleteReservation(reservation: ReservacionData)

    //Elimina una reservacion
    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteById(id: Int)
}