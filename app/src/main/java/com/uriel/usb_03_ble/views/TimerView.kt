package com.uriel.usb_03_ble.views

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.usb_03_ble.ui.theme.Pretendard
import com.uriel.usb_03_ble.ui.theme.UrielDarkGray
import com.uriel.usb_03_ble.ui.theme.UrielTextLight

@Composable
fun TimerView(modifier: Modifier,
              selectedGatt: MutableState<BluetoothGatt?>,
              selectedCharacteristic: MutableState<BluetoothGattCharacteristic?>
) {
    val viewModel = remember { mutableStateOf(TimerViewModel()) }
//    val time: MutableState<Int> = remember { mutableIntStateOf(3600) }
    val timerOn = remember { mutableStateOf(false) }

    val isOn: MutableState<Boolean> = remember { mutableStateOf(true) }

    Row(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1.0F)
            .fillMaxWidth()
            .height(150.dp)) {
            Row(modifier = Modifier
                .padding(end = 5.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(UrielDarkGray)
                .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.fillMaxHeight()
                    .padding(vertical = 20.dp)) {
//                    val hour: String = if (time.value / 3600 == 1) "01" else "00"
//                    val minute: String = if ((time.value % 3600 / 60) > 10) "${(time.value % 3600) / 60}" else "0${(time.value % 3600) / 60}"
//                    val second: String = if (((time.value % 3600) % 60) % 60 > 10) "${((time.value % 3600) % 60) % 60}" else "0${((time.value % 3600) % 60) % 60}"

                    viewModel.value.apply {
                        Text(text = "수동 작동",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = UrielTextLight
                            ),
                        )

                        Text(text = timerText.value,
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = UrielTextLight
                            )
                        )

                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomEnd) {
                            Switch(checked = timerOn.value, onCheckedChange = {
                                timerOn.value = it
                                isPlaying.value = timerOn.value
                                if (selectedGatt.value == null) {
                                    Log.d("BLETEST", "BAD GATT")
                                } else if (selectedCharacteristic.value == null) {
                                    Log.d("BLETEST", "BAD Char")
                                } else {
                                    if (isPlaying.value) {
                                        Log.d("BLETEST", "타이머 시작")
                                        sendEmergencyData(selectedGatt.value!!,
                                            selectedCharacteristic.value!!)

                                        startCountDownTimer()
                                    } else {
                                        Log.d("BLETEST", "타이머 종료")
                                        sendEmergencyOffData(selectedGatt.value!!,
                                            selectedCharacteristic.value!!)

                                        resetCountDownTimer()
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1.0F)
            .fillMaxWidth()
            .height(150.dp)) {
            Row(modifier = Modifier
                .padding(start = 5.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(UrielDarkGray)
                .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.fillMaxHeight()
                    .padding(vertical = 20.dp)) {

                    Text(text = "전원 ON/OFF",
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = UrielTextLight
                        ),
                    )

                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd) {
                        Switch(checked = isOn.value, onCheckedChange = {
                            if (selectedGatt.value == null) {
                                Log.d("BLETEST", "BAD GATT")
                            } else if (selectedCharacteristic.value == null) {
                                Log.d("BLETEST", "BAD Char")
                            } else {
                                isOn.value = it

                                if (isOn.value) {
                                    sendOnData(selectedGatt.value!!, selectedCharacteristic.value!!)
                                } else {
                                    sendOffData(selectedGatt.value!!, selectedCharacteristic.value!!)
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun sendEmergencyData(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
    val CHECKSUM: UByte = (207u + 7u).toUByte()
    println(CHECKSUM)

    val packet: UByteArray = ubyteArrayOf(207u, 7u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, CHECKSUM, 13u, 10u)
    sendPacket(gatt, characteristic, packet)
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun sendEmergencyOffData(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
    val CHECKSUM: UByte = (207u + 9u).toUByte()
    println(CHECKSUM)

    val packet: UByteArray = ubyteArrayOf(207u, 9u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, CHECKSUM, 13u, 10u)
    sendPacket(gatt, characteristic, packet)
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun sendOnData(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
    val CHECKSUM: UByte = (207u + 9u).toUByte()
    println(CHECKSUM)

    val packet: UByteArray = ubyteArrayOf(207u, 9u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, CHECKSUM, 13u, 10u)
    sendPacket(gatt, characteristic, packet)
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun sendOffData(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
    val CHECKSUM: UByte = (207u + 10u).toUByte()
    println(CHECKSUM)

    val packet: UByteArray = ubyteArrayOf(207u, 10u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, CHECKSUM, 13u, 10u)
    sendPacket(gatt, characteristic, packet)
}
