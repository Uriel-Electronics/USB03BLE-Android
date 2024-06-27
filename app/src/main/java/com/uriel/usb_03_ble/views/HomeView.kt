package com.uriel.usb_03_ble.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.uriel.usb_03_ble.ui.theme.Pretendard
import com.uriel.usb_03_ble.ui.theme.UrielBGDark
import com.uriel.usb_03_ble.ui.theme.UrielDarkGray
import com.uriel.usb_03_ble.ui.theme.UrielTextDark
import com.uriel.usb_03_ble.ui.theme.UrielTextLight
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ViewState {
    HOME, FIND_DEVICE, DEVICE_VIEW, DEVICE_CONNECTED
}

@SuppressLint("MissingPermission")
@Composable
fun HomeView(modifier: Modifier, location: Location?, address: Address?) {

    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    val viewState: MutableState<ViewState> = remember { mutableStateOf(ViewState.HOME) }
    val foundDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val finalDevice: MutableState<BluetoothDevice?> = remember { mutableStateOf(null) }
    val characteristic: MutableState<BluetoothGattCharacteristic?> = remember { mutableStateOf(null) }
    val gatt: MutableState<BluetoothGatt?> = remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000L)
        }
    }

    if (viewState.value == ViewState.HOME) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(UrielBGDark)) {
            Column(modifier = Modifier
                .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Text(text = "Uriel",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = UrielTextLight
                    )
                )

                Box(modifier = Modifier
                    .padding(20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF0085FF),
                                Color(0xFF56C1FF)
                            )
                        )
                    )
                    .padding(20.dp),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(modifier = Modifier.alpha(0.7F)) {
                            Text(if (address == null) "--" else address.countryName + " ",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = UrielTextLight,
                                ))

                            Text(if (address == null) "--" else address.adminArea,
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = UrielTextLight,
                                ))
                        }

                        val dateFormat = "yyyy년 MM월 dd일 EEEE"
                        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.KOREAN)
                        val simpleDate: String = simpleDateFormat.format(currentTime)

                        Text(simpleDate,
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = UrielTextLight,
                            ))

                        val timeFormat = "a hh:mm:ss"
                        val simpleTimeFormat = SimpleDateFormat(timeFormat, Locale.KOREAN)
                        val timeString: String = simpleTimeFormat.format(currentTime)

                        Text(timeString,
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = UrielTextLight,
                            ),
                            modifier = Modifier.padding(vertical = 16.dp))

                        Row(
                            modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier
                                    .weight(0.5F)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(1.0F, 1.0F, 1.0F, 0.15F))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text("위도",
                                    style = TextStyle(
                                        fontFamily = Pretendard,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = UrielTextLight,
                                    ), modifier = Modifier.alpha(0.7F))

                                location?.latitude?.let {
                                    Text("${ (it * 10).toInt().toDouble()/10 }",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = UrielTextLight,
                                        ))
                                }
                            }

                            Row(
                                modifier
                                    .padding(start = 8.dp)
                                    .weight(0.5F)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(1.0F, 1.0F, 1.0F, 0.15F))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text("경도",
                                    style = TextStyle(
                                        fontFamily = Pretendard,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = UrielTextLight,
                                    ), modifier = Modifier.alpha(0.7F))

                                location?.longitude?.let {
                                    Text("${ (it * 10).toInt().toDouble()/10 }",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = UrielTextLight,
                                        ))
                                }
                            }
                        }
                    }
                }

                Box(modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(UrielDarkGray)
                    .padding(20.dp)) {
                    Column {
                        Text(text = "기기 설치 후 아래의 버튼을 눌러\n기기를 연결해주세요.",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = UrielTextLight,
                                lineHeight = 23.sp
                            )
                        )
                        Box(modifier = Modifier
                            .padding(top = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(UrielTextLight)
                            .fillMaxWidth()
                            .clickable {
                                viewState.value = ViewState.FIND_DEVICE
                            }
                        ) {
                            Text(text = "기기 추가하기",
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
    } else if (viewState.value == ViewState.FIND_DEVICE) {
        FindDevice(viewState = viewState, foundDevices = foundDevices, finalDevice = finalDevice)
    } else if (viewState.value == ViewState.DEVICE_VIEW) {
        DeviceView(device = finalDevice, viewState, gatt, characteristic)
    } else if (viewState.value == ViewState.DEVICE_CONNECTED) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(UrielBGDark)) {
            Column(modifier = Modifier
                .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Text(text = if (foundDevices.isEmpty()) "Uriel" else finalDevice.value!!.name,
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = UrielTextLight
                    )
                )

                address?.let {
                    Box(modifier = Modifier
                        .padding(20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0085FF),
                                    Color(0xFF56C1FF)
                                )
                            )
                        )
                        .padding(20.dp),
                        contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(modifier = Modifier.alpha(0.7F)) {
                                Text(address.countryName + " ",
                                    style = TextStyle(
                                        fontFamily = Pretendard,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = UrielTextLight,
                                    ))

                                Text(address.adminArea,
                                    style = TextStyle(
                                        fontFamily = Pretendard,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = UrielTextLight,
                                    ))
                            }

                            val dateFormat = "yyyy년 MM월 dd일 EEEE"
                            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.KOREAN)
                            val simpleDate: String = simpleDateFormat.format(currentTime)

                            Text(simpleDate,
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = UrielTextLight,
                                ))

                            val timeFormat = "a hh:mm:ss"
                            val simpleTimeFormat = SimpleDateFormat(timeFormat, Locale.KOREAN)
                            val timeString: String = simpleTimeFormat.format(currentTime)

                            Text(timeString,
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = UrielTextLight,
                                ),
                                modifier = Modifier.padding(vertical = 16.dp))

                            Row(
                                modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier
                                        .weight(0.5F)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(1.0F, 1.0F, 1.0F, 0.15F))
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text("위도",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = UrielTextLight,
                                        ), modifier = Modifier.alpha(0.7F))

                                    location?.latitude?.let {
                                        Text("${ (it * 10).toInt().toDouble()/10 }",
                                            style = TextStyle(
                                                fontFamily = Pretendard,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = UrielTextLight,
                                            ))
                                    }
                                }

                                Row(
                                    modifier
                                        .padding(start = 8.dp)
                                        .weight(0.5F)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(1.0F, 1.0F, 1.0F, 0.15F))
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text("경도",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = UrielTextLight,
                                        ), modifier = Modifier.alpha(0.7F))

                                    location?.longitude?.let {
                                        Text("${ (it * 10).toInt().toDouble()/10 }",
                                            style = TextStyle(
                                                fontFamily = Pretendard,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = UrielTextLight,
                                            ))
                                    }
                                }
                            }
                        }
                    }
                }

                ModeView(modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp))

                DaySelectView(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp)
                )

                TimerView(modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp),
                    gatt,
                    characteristic
                    )
            }
        }
    }

}

fun getCurrentTime(): Date {
    return Date()
}