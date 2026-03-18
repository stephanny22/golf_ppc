package com.example.golf_ppc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReservationStatus { ACTIVA, CANCELADA, COMPLETADA }

@Entity(tableName = "reservations")
data class ReservacionData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientName: String,
    val clientPhone: String,
    val fieldNumber: Int, // Número de cancha
    val date: String, // Formato: "yyyy-MM-dd"
    val time: String, // Formato: "HH:mm"
    val durationHours: Int = 1,
    val status: ReservationStatus = ReservationStatus.ACTIVA,
    val notes: String = ""
)
