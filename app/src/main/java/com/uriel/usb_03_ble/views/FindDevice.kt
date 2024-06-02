package com.uriel.usb_03_ble.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.uriel.usb_03_ble.R
import com.uriel.usb_03_ble.ui.theme.Pretendard
import com.uriel.usb_03_ble.ui.theme.UrielBGDark
import com.uriel.usb_03_ble.ui.theme.UrielDarkGray
import com.uriel.usb_03_ble.ui.theme.UrielTextDark
import com.uriel.usb_03_ble.ui.theme.UrielTextLight

@Composable
fun FindDevice(viewState: MutableState<ViewState>, foundDevice: MutableState<BluetoothDevice?>) {

    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

    val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        .build()

    val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            if (result.device.name != null) {
                val codes = result.device.toString()

                val uuids = codes.split(":")
                if (uuids[0] == "94" && uuids[1] == "C9") {

                    foundDevice.value = result.device
                }
            }
        }

//        override fun onScanFailed(errorCode: Int) {
//            println("onScanFailed  $errorCode")
//        }
    }

    val gattCallback = object : BluetoothGattCallback() {
        // GATT의 연결 상태 변경을 감지하는 콜백
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            // 연결이 성공적으로 이루어진 경우
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // GATT 서버에서 사용 가능한 서비스들을 비동기적으로 탐색
                Log.d("BleManager", "연결 성공")
                gatt?.discoverServices()

                // ...
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 연결 끊김
                Log.d("BleManager", "연결 해제")

                // ...
            }
        }

        // 장치에 대한 새로운 서비스가 발견되었을 때 호출되는 콜백
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            // 원격 장치가 성공적으로 탐색된 경우
            if(status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLETEST", "nice gatt success")
                viewState.value = ViewState.DEVICE_VIEW
            }
        }
    }

    bluetoothLeScanner.startScan(null, scanSettings, scanCallback)

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = UrielBGDark),
        contentAlignment = Alignment.TopCenter) {
        Column {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Text(text = "기기찾기",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = UrielTextLight
                    ),
                    modifier = Modifier.padding(vertical = 13.dp),
                )
            }

            if (foundDevice.value == null) {
                Box(modifier = Modifier
                    .padding(20.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = UrielDarkGray)
                    .aspectRatio(1.0F),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.uriel_logo),
                            contentDescription = "logo",
                            modifier = Modifier.padding(40.dp))
                        Text(text = "주변의 기기를 찾는 중입니다....",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UrielTextLight
                            ),
                            textAlign = TextAlign.Center)

                        Text(text = "기기가 보이지 않는다면 기기가 설치되고\n" +
                                "전원이 켜져있는지 확인해주세요.",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = UrielTextLight,
                                lineHeight = 23.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }  else {
                Box(modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color = UrielDarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "기기를 찾았습니다.",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UrielTextLight
                            ),
                            modifier = Modifier.padding(top = 40.dp),
                        )

                        Box {
                            Box(modifier = Modifier.background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFF373A3F),
                                        Color.Red
                                    )
                                )
                            ))

                            Image(painter = painterResource(id = R.drawable.found_device),
                                contentDescription = "logo",
                                modifier = Modifier.padding(20.dp))

                            Box(modifier = Modifier.background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFF373A3F),
                                        Color.Red
                                    )
                                )
                            ))
                        }

                        Text(text = "${foundDevice.value!!.name ?: "test"}",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = UrielTextLight
                            ),
                            modifier = Modifier.padding(vertical = 20.dp),
                        )

                        Box(modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(UrielTextLight)
                            .clickable {
                                foundDevice.value?.let { device ->
                                    bluetoothAdapter
                                        .getRemoteDevice(device.address)
                                        .connectGatt(context, false, gattCallback)
                                }
                            }
                        ) {
                            Text(text = "연결하기",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = UrielTextDark
                                ),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(vertical = 12.5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

