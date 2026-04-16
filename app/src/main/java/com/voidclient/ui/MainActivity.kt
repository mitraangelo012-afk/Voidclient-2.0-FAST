package com.voidclient.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voidclient.R
import com.voidclient.services.ClientService
import com.voidclient.native.NativeInterface

class MainActivity : ComponentActivity() {

    private var isServiceRunning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("VoidClient 2.0") }
                        )
                    }
                ) { padding ->
                    MainScreen(
                        modifier = Modifier.padding(padding),
                        isServiceRunning = isServiceRunning,
                        onStartClick = { startClientService() },
                        onStopClick = { stopClientService() },
                        onRequestPermission = { requestOverlayPermission() }
                    )
                }
            }
        }
    }

    private fun startClientService() {
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
            return
        }

        val intent = Intent(this, ClientService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        isServiceRunning = true
        Toast.makeText(this, "VoidClient started", Toast.LENGTH_SHORT).show()
    }

    private fun stopClientService() {
        val intent = Intent(this, ClientService::class.java)
        stopService(intent)
        isServiceRunning = false
        Toast.makeText(this, "VoidClient stopped", Toast.LENGTH_SHORT).show()
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
        Toast.makeText(this, "Please grant overlay permission", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    isServiceRunning: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "VoidClient 2.0",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartClick,
            enabled = !isServiceRunning
        ) {
            Text("Start Client")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStopClick,
            enabled = isServiceRunning
        ) {
            Text("Stop Client")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRequestPermission
        ) {
            Text("Request Overlay Permission")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isServiceRunning) "Running" else "Stopped",
                    color = if (isServiceRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Native Interface:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (NativeInterface.isInitialized()) "Connected" else "Disconnected",
                    color = if (NativeInterface.isInitialized()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}