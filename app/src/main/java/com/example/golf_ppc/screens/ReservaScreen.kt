package com.example.golf_ppc.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.golf_ppc.data.local.AppDatabase
import com.example.golf_ppc.data.local.ReservacionData
import com.example.golf_ppc.data.local.ReservationStatus
import com.example.golf_ppc.data.repositorio.ReservacionRepositorio
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val repo = ReservacionRepositorio(db)
    val scope = rememberCoroutineScope()

    // Estados de los campos
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var cancha by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("1") }
    var notas by remember { mutableStateOf("") }

    // --- Lógica del Calendario ---
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        // Formateamos la fecha a yyyy-MM-dd
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        fecha = formatter.format(Date(selectedDate))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()) // Añadido para que quepa en pantallas pequeñas
    ) {
        Text("Registrar Reserva", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Campos de texto normales...
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

        // --- CAMPO DE FECHA CON CALENDARIO ---
        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha de Reserva") },
            placeholder = { Text("Selecciona una fecha") },
            readOnly = true, // Evita que el teclado se abra
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }, // Abre el calendario al tocar el campo
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                }
            }
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

        // Botón Guardar
        Button(
            onClick = {
                if (nombre.isNotBlank() && fecha.isNotBlank() && cancha.isNotBlank()) {
                    scope.launch {
                        repo.addReservation(
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
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Reserva")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}