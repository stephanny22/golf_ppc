package com.example.golf_ppc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import android.os.Bundle
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.golf_ppc.screens.AllReservasScreen
import com.example.golf_ppc.screens.WelcomeScreen
import com.example.golf_ppc.screens.ReservaScreen


import com.example.golf_ppc.ui.theme.Golf_ppcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Golf_ppcTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "welcome"
                ) {

                    composable("welcome") {
                        WelcomeScreen(navController)
                    }

                    composable("home") {
                        HomeScreen(navController)
                    }

                    composable("reserva") {
                        ReservaScreen(navController)
                    }
                    composable("all_reservas") {
                        AllReservasScreen(navController)
                    }
                }
            }
        }
    }

}
@Preview(showBackground = true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Golf Club")
                }
            )
        },
    ) { innerPadding ->
        ScrollContent(navController,innerPadding)
    }
}
@Composable
fun ScrollContent(navController: NavController,padding: PaddingValues) {

    val itemsList = listOf(
        "Reservas Hoy \n 12",
        "Canchas Ocupadas \n 5",
        "Reservas Activas \n 8",
        "Reservas finalizadas \n 4"
    )

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // GRID 2x2 - Construido dinámicamente con Column y Row
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dividimos la lista en grupos de 2 (filas)
                itemsList.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { text ->
                            Box(
                                modifier = Modifier
                                    .weight(1f) // Toma la mitad del espacio disponible
                                    .aspectRatio(1f) // Se asegura de que sea un cuadrado perfecto
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = text,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        // Espaciador por si en un futuro tienes una cantidad impar de items (ej: 3 o 5 items)
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // TÍTULO
        item {
            Text(
                text = "Próximas reservas",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // LISTA DE 3 ITEMS
        items(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(16.dp)
            ) {
                Text("nombre - hora - lugar")
            }
        }

        // BOTÓN VER MÁS
        item {
            Button(
                onClick = {navController.navigate("all_reservas")},
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Ver más")
            }
        }

        // BOTÓN FINAL
        item {
            Button(
                onClick = { navController.navigate("reserva")},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reservar")
            }
        }
    }
}

