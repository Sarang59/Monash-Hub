package com.example.loginpage.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.FilterAlt
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.loginpage.R
import com.example.loginpage.data.model.campuses
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.example.loginpage.viewmodel.CampusMapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(navController: NavController, drawerState: DrawerState) {
    // Get current context and viewModel
    val context = LocalContext.current
    val viewModel: CampusMapViewModel = viewModel()
    // Observing state from ViewModel
    val routePoints by viewModel.routePoints.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()

    // Manages map camera position
    val cameraPositionState = rememberCameraPositionState()

    // Coroutine scope for background tasks
    val coroutineScope = rememberCoroutineScope()

    // Remember location permission state
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    var selectedCampus by remember { mutableStateOf("Clayton") }

    // Find coordinates of selected campus
    val defaultLatLng = campuses.first { it.name == selectedCampus }.let { LatLng(it.lat, it.lng) }
    var currentLocation by remember { mutableStateOf(defaultLatLng) }

    // Search bar state
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }


    val colorScheme = MaterialTheme.colorScheme
    // OpenRoute API key (used for routing)
    val apiKey = "5b3ce3597851110001cf6248b32d582edf144bc08ace14c782390e3e"


    // Request location permission on screen load
    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    // Fetch and animate to device location if permission granted
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            coroutineScope.launch {
                val location = getDeviceLocation(context)
                location?.let {
                    currentLocation = it
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                }
            }
        } else {
            // Center camera on default campus if permission not granted
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15f))
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.primaryBg) {
        Column {
            Header(navController = navController,drawerState = drawerState)

            Spacer(modifier = Modifier.height(26.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Campus Map",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Dropdown for selecting campus
                    Box {
                        IconButton(onClick = { expanded = true }, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Sharp.FilterAlt,
                                contentDescription = "Filter Icon",
                                tint = colorScheme.primaryText,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.primaryBg)) {
                            campuses.take(4).forEach { campus ->
                                DropdownMenuItem(
                                    text = { Text(campus.name) },
                                    onClick = {
                                        expanded = false
                                        selectedCampus = campus.name
                                        val latLng = LatLng(campus.lat, campus.lng)
                                        coroutineScope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = colorScheme.primaryText
                )

                // Search bar and suggestions
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val boxWidth = this.maxWidth

                    Column(modifier = Modifier.width(boxWidth)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                // Fetch suggestions if input length >= 3
                                if (it.text.length >= 3) {
                                    viewModel.fetchSuggestions(it.text, currentLocation, apiKey)
                                } else {
                                    viewModel.clearSuggestions()
                                }
                            },
                            placeholder = {
                                Text("Search", color = colorScheme.inactiveGrey, fontSize = 18.sp)
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val query = searchQuery.text.trim()
                                    if (query.isNotBlank()) {
                                        viewModel.getRouteToAddress(query, currentLocation, apiKey)
                                        viewModel.clearSuggestions()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = if (searchQuery.text.isNotBlank()) colorScheme.primaryText else colorScheme.inactiveGrey,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colorScheme.Transparent,
                                unfocusedContainerColor = colorScheme.Transparent,
                                focusedIndicatorColor = colorScheme.primaryText,
                                unfocusedIndicatorColor = colorScheme.inactiveGrey,
                            ),
                            shape = RectangleShape,
                            modifier = Modifier
                                .width(boxWidth)
                        )

                        // Search suggestion dropdown
                        DropdownMenu(
                            expanded = suggestions.isNotEmpty(),
                            onDismissRequest = { viewModel.clearSuggestions() },
                            modifier = Modifier
                                .width(boxWidth)
                                .heightIn(max = 200.dp)
                                .background(MaterialTheme.colorScheme.primaryBg)
                        ) {
                            suggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        searchQuery = TextFieldValue(suggestion)
                                        viewModel.getRouteToAddress(suggestion, currentLocation, apiKey)
                                        viewModel.clearSuggestions()
                                    }
                                )
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))

                // Map view with routing polyline and marker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f).padding(bottom = 24.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted)
                    ) {
                        if (routePoints.isNotEmpty()) {
                            Polyline(points = routePoints)   // Draw route
                            Marker(     // Mark destination
                                state = MarkerState(position = routePoints.last()),
                                title = "Destination"
                            )
                        }
                    }
                }
            }
        }
    }
}

// Suspended function to get current device location
@SuppressLint("MissingPermission")
private suspend fun getDeviceLocation(context: Context): LatLng? {
    return try {
        // Use fused location provider
        val fused = LocationServices.getFusedLocationProviderClient(context)
        val location = fused.lastLocation.await()       // Asynchronously get last known location
        location?.let { LatLng(it.latitude, it.longitude) }     // Convert to LatLng if not null
    } catch (e: Exception) {
        // Log any exception and return null
        Log.e("MapScreen", "Error getting device location", e)
        null
    }
}
