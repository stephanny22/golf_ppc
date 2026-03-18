package com.example.golf_ppc.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.golf_ppc.data.local.ReservacionData
import com.example.golf_ppc.data.local.ReservationStatus
import com.example.golf_ppc.viewmodel.ReservacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarReservaScreen(
    navController: NavController,
    reservaId: Int,
    viewModel: ReservacionViewModel = viewModel()
) {
    // --- ESTADOS DE LOS FORMULARIOS ---
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var cancha by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(ReservationStatus.ACTIVA) }

    val userMessage by viewModel.userMessage.collectAsState()

    // 1. CARGA INICIAL: Buscamos los datos actuales de la reserva
    LaunchedEffect(reservaId) {
        val datos = viewModel.getReservationById(reservaId)
        datos?.let {
            nombre = it.clientName
            telefono = it.clientPhone
            cancha = it.fieldNumber.toString()
            fecha = it.date
            hora = it.time
            notas = it.notes
            estado = it.status
        }
    }

    // 2. ESCUCHAR ÉXITO: Si el mensaje confirma la actualización, volvemos atrás
    LaunchedEffect(userMessage) {
        if (userMessage?.contains("actualizada") == true) {
            navController.popBackStack()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modificar Reserva") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Datos del Cliente", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de contacto") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()
            Text("Detalles de la Cancha", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cancha,
                    onValueChange = { cancha = it },
                    label = { Text("Cancha #") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha") },
                    modifier = Modifier.weight(1.5f)
                )
            }

            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- SELECTOR DE ESTADO ---
            Text("Estado Actual", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReservationStatus.entries.forEach { statusOption ->
                    FilterChip(
                        selected = estado == statusOption,
                        onClick = { estado = statusOption },
                        label = { Text(statusOption.name) },
                        leadingIcon = if (estado == statusOption) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas de la reserva") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Mensaje de error (si hay conflictos de cancha)
            if (userMessage != null && !userMessage!!.contains("actualizada")) {
                Text(userMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTÓN DE ACCIÓN ---
            Button(
                onClick = {
                    val reservaEditada = ReservacionData(
                        id = reservaId,
                        clientName = nombre,
                        clientPhone = telefono,
                        fieldNumber = cancha.toIntOrNull() ?: 0,
                        date = fecha,
                        time = hora,
                        status = estado,
                        notes = notas
                    )
                    viewModel.updateReservation(reservaEditada)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Guardar Cambios")
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}