package com.example.golf_ppc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.golf_ppc.data.local.ReservacionData
import com.example.golf_ppc.data.local.ReservationStatus
import com.example.golf_ppc.viewmodel.ReservacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReservasScreen(
    navController: NavController,
    viewModel: ReservacionViewModel = viewModel()
) {
    val search by viewModel.searchQuery.collectAsState()
    val reservas by viewModel.reservations.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var reservaAEliminar by remember { mutableStateOf<ReservacionData?>(null) }

    // Mostrar mensajes del ViewModel (como "Reserva eliminada")
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (reservaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { reservaAEliminar = null },
            title = { Text("¿Eliminar reserva?") },
            text = { Text("Esta acción no se puede deshacer. ¿Deseas eliminar la reserva de ${reservaAEliminar?.clientName}?") },
            confirmButton = {
                TextButton(onClick = {
                    reservaAEliminar?.let { viewModel.deleteReservation(it.id) }
                    reservaAEliminar = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Listado de Reservas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = { Text("Buscar cliente...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeader("Cliente", weight = 1.2f)
                TableHeader("Fecha/Hora", weight = 1.2f)
                TableHeader("Estado", weight = 1f)
                Spacer(modifier = Modifier.width(80.dp)) // Espacio para los dos botones
            }

            if (reservas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay resultados", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(reservas) { reserva ->
                        ReservaRow(
                            reserva = reserva,
                            onEdit = { navController.navigate("editar_reserva/${reserva.id}") },
                            onDelete = { reservaAEliminar = reserva }
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaRow(
    reserva: ReservacionData,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.2f)) {
            Text(reserva.clientName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("Cancha ${reserva.fieldNumber}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        Column(modifier = Modifier.weight(1.2f)) {
            Text(reserva.date, style = MaterialTheme.typography.bodySmall)
            Text(reserva.time, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }

        Box(modifier = Modifier.weight(1f)) {
            StatusChip(reserva.status)
        }

        // --- BOTONES DE ACCIÓN ---
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
            }
        }
    }
}

@Composable
fun RowScope.TableHeader(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        color = Color.White,
        style = MaterialTheme.typography.bodySmall
    )
}
@Composable
fun ReservaRow(reserva: ReservacionData) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        TableCell(reserva.clientName)
        TableCell(reserva.date)
        TableCell(reserva.time)
        TableCell(reserva.fieldNumber.toString())

        // 🎯 ESTADO CON ESTILO
        Box(modifier = Modifier.weight(1f)) {
            StatusChip(reserva.status)
        }

        // ✏️ ACCIÓN (ICONO)
        IconButton(
            onClick = { }
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Editar")
        }
    }
}
@Composable
fun RowScope.TableCell(text: String) {
    Text(
        text = text,
        modifier = Modifier.weight(1f),
        style = MaterialTheme.typography.bodySmall
    )
}
@Composable
fun StatusChip(status: ReservationStatus) {

    val color = when (status) {
        ReservationStatus.ACTIVA -> Color(0xFF4CAF50)     // verde
        ReservationStatus.CANCELADA -> Color(0xFFFF9800)  // naranja
        ReservationStatus.COMPLETADA -> Color(0xFFF44336) // rojo
    }

    Box(
        modifier = Modifier
            .background(color, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}