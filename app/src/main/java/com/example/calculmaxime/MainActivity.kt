package com.example.calculmaxime

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import com.example.calculmaxime.ui.theme.CalculMaximeTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculMaximeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculMaximeApp()
                }
            }
        }
    }
}

@Composable
fun CalculMaximeApp() {
    var nombre by remember { mutableStateOf("") }
    var numberPerSec by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fond d'écran (optionnel)
        Image(
            painter = painterResource(id = R.drawable.mybackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Compteur de Maxime",
                color = Color(0xFFADFF2F),
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Saisir le nombre", color = Color.Red) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F8FF))
                    .focusRequester(focusRequester)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = numberPerSec,
                onValueChange = { numberPerSec = it },
                label = { Text("Saisir le nombre par seconde", color = Color.Red) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F8FF))
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = result,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F8FF))
                    .padding(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    try {
                        val n = nombre.toDouble()
                        val perSec = numberPerSec.toDouble()
                        if (n > 922337203685) {
                            Toast.makeText(
                                null,
                                "Veuillez choisir un nombre inférieur à 922 337 203 685",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val seconds = (n / perSec).toLong()
                            val days = TimeUnit.SECONDS.toDays(seconds)
                            val hours = TimeUnit.SECONDS.toHours(seconds) % 24
                            val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
                            val secs = seconds % 60
                            result = "$days jours $hours heures $minutes minutes $secs secondes"
                        }
                    } catch (e: Exception) {
                        result = "Entrée invalide"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcul", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    nombre = ""
                    numberPerSec = ""
                    result = ""
                    focusRequester.requestFocus()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Effacer", color = Color.Black)
            }
        }
    }
}