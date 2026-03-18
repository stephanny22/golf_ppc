package com.example.golf_ppc.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import com.example.golf_ppc.data.local.AppDatabase
import com.example.golf_ppc.data.local.ReservacionData
import com.example.golf_ppc.data.local.ReservationStatus
import com.example.golf_ppc.data.repositorio.ReservacionRepositorio

@Composable
fun ReservaScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val dao = db.reservacionDAO()
    val repo = ReservacionRepositorio(db)
    val scope = rememberCoroutineScope()


    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var cancha by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("1") }
    var notas by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Registrar Reserva", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cancha,
            onValueChange = { cancha = it },
            label = { Text("Número de cancha") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = hora,
            onValueChange = { hora = it },
            label = { Text("Hora (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = duracion,
            onValueChange = { duracion = it },
            label = { Text("Duración (horas)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                if (
                    nombre.isNotBlank() &&
                    telefono.isNotBlank() &&
                    cancha.isNotBlank() &&
                    fecha.isNotBlank() &&
                    hora.isNotBlank()
                ) {

                    scope.launch {

                        val error = repo.addReservation(
                            ReservacionData(
                                clientName = nombre,
                                clientPhone = telefono,
                                fieldNumber = cancha.toIntOrNull() ?: 1,
                                date = fecha,
                                time = hora,
                                durationHours = duracion.toIntOrNull() ?: 1,
                                status = ReservationStatus.ACTIVA,
                                notes = notas
                            )
                        )

                        if (error == null) {
                            // ÉXITO
                            navController.popBackStack()
                        } else {
                            // ERROR (conflicto de cancha)
                            println(error)
                        }
                    }
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Reserva")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}