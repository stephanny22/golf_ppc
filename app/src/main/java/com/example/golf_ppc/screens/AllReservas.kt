package com.example.golf_ppc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

import com.example.golf_ppc.data.local.AppDatabase
import com.example.golf_ppc.data.local.ReservacionData
import com.example.golf_ppc.data.local.ReservationStatus
import com.example.golf_ppc.data.repositorio.ReservacionRepositorio

@Composable
fun AllReservasScreen(navController: NavController) {

    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val repo = ReservacionRepositorio(db)

    var search by remember { mutableStateOf("") }

    val reservas by repo.searchReservations(search)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        Text(
            text = "Listado de Reservas",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔍 BUSCADOR
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar reserva...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🟩 HEADER TIPO TABLA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            TableHeader("Cliente")
            TableHeader("Fecha")
            TableHeader("Hora")
            TableHeader("Cancha")
            TableHeader("Estado")
            TableHeader("Acciones")
        }

        // 📋 LISTA
        LazyColumn {
            items(reservas) { reserva ->
                ReservaRow(reserva)
            }
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
@Composable
fun RowScope.TableHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.weight(1f),
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