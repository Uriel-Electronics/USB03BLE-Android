package com.uriel.usb_03_ble

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.uriel.usb_03_ble.ui.theme.USB03BLETheme
import com.uriel.usb_03_ble.utils.RequestPermissionsUtil
import com.uriel.usb_03_ble.views.HomeView
import java.util.Locale

class MainActivity : ComponentActivity() {
    val REQUEST_PERMISSION: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val location: MutableState<Location?> = remember { mutableStateOf(null) }
            val address: MutableState<Address?> = remember { mutableStateOf(null) }

            var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            } else {
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            }

            checkPermission(permissions)

            getLocation { loc ->
                if (loc != null) {
                    getAddress(loc) { add ->
                        if (add != null) {
                            location.value = loc
                            address.value = add
                        }
                    }
                }
            }

            USB03BLETheme {
                // A surface container using the 'background' color from the theme
                HomeView(modifier = Modifier, location = location.value, address = address.value)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(completion: (Location?) -> Unit) {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    Log.d("LOC", location.longitude.toString())

                    completion(location)
                }
            }
            .addOnFailureListener { fail ->
                print(fail)
                completion(null)
            }
    }

    //위도 경도로 주소 구하는 Reverse-GeoCoding
    private fun getAddress(location: Location, completion: (Address?) -> Unit) {
        try {
            with(Geocoder(this, Locale.KOREA).getFromLocation(location.latitude, location.longitude, 1)!!.first()){
                completion(this)
            }
        } catch (e: Exception){
            e.printStackTrace()
            completion(null)
        }
    }

    private fun checkPermission(permissionList: List<String>) {
        val requestList = ArrayList<String>()

        for (permission in permissionList) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(permission)
            }
        }

        if (requestList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, requestList.toTypedArray(), REQUEST_PERMISSION)
        }
    }
}