package com.example.golf_ppc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.golf_ppc.screens.AllReservasScreen
import com.example.golf_ppc.screens.WelcomeScreen
import com.example.golf_ppc.screens.ReservaScreen
import com.example.golf_ppc.ui.theme.Golf_ppcTheme
import com.example.golf_ppc.viewmodel.ReservacionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Golf_ppcTheme {
                val navController = rememberNavController()
                // Obtenemos el ViewModel aquí para compartirlo si es necesario
                val viewModel: ReservacionViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "welcome"
                ) {
                    composable("welcome") { WelcomeScreen(navController) }
                    composable("home") { HomeScreen(navController, viewModel) }
                    composable("reserva") { ReservaScreen(navController) }
                    composable("all_reservas") { AllReservasScreen(navController) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ReservacionViewModel = viewModel()) {
    // Observamos los datos del Dashboard y las Reservas
    val dashboardInfo by viewModel.dashboard.collectAsState()
    val listaReservas by viewModel.reservations.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Golf Club Dashboard", fontWeight = FontWeight.Bold) }
            )
        },
    ) { innerPadding ->
        // Pasamos los datos reales a la función de contenido
        ScrollContent(navController, innerPadding, dashboardInfo, listaReservas)
    }
}

@Composable
fun ScrollContent(
    navController: NavController,
    padding: PaddingValues,
    info: com.example.golf_ppc.viewmodel.Informacion,
    reservas: List<com.example.golf_ppc.data.local.ReservacionData>
) {
    // Mapeamos tu información del ViewModel a la lista del Grid
    val itemsDashboard = listOf(
        "Hoy\n${info.occupiedFieldsToday}" to "Canchas",
        "Activas\n${info.activeField}" to "Reservas",
        "Finalizadas\n${info.completedField}" to "Historial",
        "Canceladas\n${info.cancelledField}" to "Alertas"
    )

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // GRID 2x2 DINÁMICO
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                itemsDashboard.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        rowItems.forEach { (titulo, subtitulo) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.2f)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = titulo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(text = subtitulo, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(text = "Próximas reservas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // LISTA REAL DESDE LA BASE DE DATOS (Tomamos las últimas 3)
        items(reservas.take(3)) { reserva ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = reserva.clientName, fontWeight = FontWeight.Bold)
                    Text(text = "Cancha ${reserva.fieldNumber} - ${reserva.time}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (reservas.isEmpty()) {
            item { Text("No hay reservas próximas", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = androidx.compose.ui.graphics.Color.Gray) }
        }

        item {
            Button(onClick = { navController.navigate("all_reservas") }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver todas las reservas")
            }
        }

        item {
            OutlinedButton(onClick = { navController.navigate("reserva") }, modifier = Modifier.fillMaxWidth()) {
                Text("Nueva Reserva")
            }
        }
    }
}