package hr.ferit.frankoklepac.rma_project.viewmodel

import android.app.Application
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import hr.ferit.frankoklepac.rma_project.utils.LocationHelper
import kotlinx.coroutines.launch
import android.location.Geocoder
import java.util.Locale


data class Server(val name: String, val latitude: Double, val longitude: Double)

@Suppress("DEPRECATION")
class MainMenuViewModel(application: Application) : AndroidViewModel(application) {
    val userLocation = mutableStateOf<Location?>(null)
    val closestServer = mutableStateOf<String?>(null)
    val locationError = mutableStateOf<String?>(null)

    private val servers = listOf(
        Server("Frankfurt", 50.1109, 8.6821),
        Server("Amsterdam", 52.3676, 4.9041),
        Server("Chicago", 41.8781, -87.6298),
        Server("Seoul", 37.5665, 126.9780),
    )

    private val locationHelper = LocationHelper(application)
    private val geocoder = Geocoder(application, Locale.getDefault())


    fun fetchLocation() {
        viewModelScope.launch {
            try {
                val location = locationHelper.getLastLocation()
                userLocation.value = location
                if (location != null) {
                    closestServer.value = findClosestServer(location)
                    locationError.value = null
                } else {
                    locationError.value = "Unable to fetch location. Ensure location services are enabled."
                }
            } catch (e: SecurityException) {
                locationError.value = "Location permission denied."
            }
        }
    }

    private fun findClosestServer(location: Location): String {
        var closestServer = servers[0]
        var minDistance = Float.MAX_VALUE

        for (server in servers) {
            val serverLocation = Location("").apply {
                latitude = server.latitude
                longitude = server.longitude
            }
            val distance = location.distanceTo(serverLocation)
            if (distance < minDistance) {
                minDistance = distance
                closestServer = server
            }
        }
        return closestServer.name
    }

    internal fun getCityName(location: Location): String? {
        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses?.firstOrNull()?.locality ?: "Unknown city"
        } catch (e: Exception) {
            "Unknown city"
        }
    }

}