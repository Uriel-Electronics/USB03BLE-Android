package com.uriel.usb_03_ble.views

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.usb_03_ble.ui.theme.Pretendard
import com.uriel.usb_03_ble.ui.theme.UrielBGDark
import com.uriel.usb_03_ble.ui.theme.UrielTextDark
import com.uriel.usb_03_ble.ui.theme.UrielTextLight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("MissingPermission")
@Composable
fun DeviceView(device: MutableState<BluetoothDevice?>, viewState: MutableState<ViewState>) {
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    fun connectToDevice(device: BluetoothDevice) {

        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.close()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    for (service in gatt.services) {
//                        Log.d("BLETEST", "Service: ${service.uuid}")
                        for (characteristic in service.characteristics) {

//                            if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) {
//                                Log.d("BLETEST", "WRITABLE")
//
//                                sendCurrentStatusRequest(gatt, characteristic)
//                            }
                            if (characteristic.uuid.toString().contains("ffe1")) {

                                CoroutineScope(Main).launch {
                                    sendPacket(gatt, characteristic, sendData())
                                    delay(1200)

                                    requestData(gatt, characteristic)
                                    viewState.value = ViewState.DEVICE_CONNECTED
                                }
                                // Check if characteristic is writable
//                                Log.d("BLETEST", "right uuid ${characteristic.uuid}")
//
//                                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
//                                    gatt.readCharacteristic(characteristic)
//                                }

                            }

                        }
                    }
                    Log.e("BLETEST", "No writable characteristic found")
                }
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("BLETEST", "Data written successfully")

                } else {
                    Log.e("BLETEST", "Characteristic write failed, status: $status")
                }
            }
        }

        device.connectGatt(context, false, gattCallback)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(UrielBGDark)) {
        Column(modifier = Modifier.fillMaxHeight()
            .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier
                .padding(bottom = 30.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(UrielTextLight)
                .fillMaxWidth()
                .clickable {
                    //TODO: go back
                }
            ) {
                Text(text = "시간 재설정하기",
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

            Box(modifier = Modifier
                .padding(top = 30.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(UrielTextLight)
                .fillMaxWidth()
                .clickable {
                    device.value?.let {
                        Log.d("BLETEST", "start")
                        connectToDevice(it)
                    }
                }
            ) {
                Text(text = "모드 선택하기",
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

@OptIn(ExperimentalUnsignedTypes::class)
fun sendData(): UByteArray {
    val date = Date()
    val yform = SimpleDateFormat("yyyy", Locale.getDefault())
    val mform = SimpleDateFormat("MM", Locale.getDefault())
    val dform = SimpleDateFormat("dd", Locale.getDefault())
    val dayform = SimpleDateFormat("EEE", Locale.getDefault())
    val hhform = SimpleDateFormat("HH", Locale.getDefault())
    val minform = SimpleDateFormat("mm", Locale.getDefault())
    val ssform = SimpleDateFormat("ss", Locale.getDefault())

    val yy = yform.format(date)
    val mm = mform.format(date)
    val dd = dform.format(date)
    val day = dayform.format(date)
    println(day)
    val hh = hhform.format(date)
    val minute = minform.format(date)
    val ss = ssform.format(date)

    val YY = (yy.toInt() - 2000).toUByte()
    println(YY)
    val MM = mm.toInt().toUByte()
    println(MM)
    val DD = dd.toInt().toUByte()
    println(DD)
    val DAY: UByte = when (day) {
        "Mon" -> 1.toUByte()
        "Tue" -> 2.toUByte()
        "Wed" -> 3.toUByte()
        "Thu" -> 4.toUByte()
        "Fri" -> 5.toUByte()
        "Sat" -> 6.toUByte()
        "Sun" -> 7.toUByte()
        else -> 0.toUByte() // 잘못된 요일 값 처리
    }
    val HH = hh.toInt().toUByte()
    val MIN = minute.toInt().toUByte()
    val SS = ss.toInt().toUByte()

    // Assuming you have similar functions to get LAT and LNG in your Kotlin code
    val LAT = getFormattedLAT()
    val LNG = getFormattedLNG()

    val bigLAT: Short = LAT.toShort()
    val bigLNG: Short = LNG.toShort()

    val upperLAT = (bigLAT.toInt() shr 8).toUByte()
    val lowerLAT = (bigLAT.toInt() and 0x00FF).toUByte()

    val upperLNG = (bigLNG.toInt() shr 8).toUByte()
    val lowerLNG = (bigLNG.toInt() and 0x00FF).toUByte()

    val absLAT: Short = 0
    val absLNG: Short = 0

    val CHECKSUM = (175 + YY.toInt() + MM.toInt() + DD.toInt() + DAY.toInt() + HH.toInt() + MIN.toInt() + SS.toInt() + upperLAT.toInt() + lowerLAT.toInt() + upperLNG.toInt() + lowerLNG.toInt() + 5 + 70).toUByte()

    val packet: UByteArray = ubyteArrayOf(
        175.toUByte(), YY, MM, DD, DAY, HH, MIN, SS, upperLAT, lowerLAT, upperLNG, lowerLNG, 5.toUByte(), 70.toUByte(), CHECKSUM, 13.toUByte(), 10.toUByte()
    )

    Log.d("BLETEST", packet.joinToString(", ") { it.toString() })

    return packet
}

// Dummy functions to simulate the behavior of locationManager in Swift
fun getFormattedLAT(): Int {
    // Implement the function to get the formatted LAT value
    return 12345
}

fun getFormattedLNG(): Int {
    // Implement the function to get the formatted LNG value
    return 67890
}

@OptIn(ExperimentalUnsignedTypes::class)
@SuppressLint("MissingPermission")
private fun sendPacket(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, packet: UByteArray) {
    Log.d("BLETEST", "PACKET SENDING STARTED")

    characteristic.value = packet.toByteArray()
    val result = gatt.writeCharacteristic(characteristic)

    Log.d("BLETEST", "PACKET Sending ${result}")
}

@SuppressLint("MissingPermission")
private fun sendCurrentStatusRequest(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
    val stx: Byte = 0x00
    val mode: Byte = 0x00
    val day: Byte = 0x00
    val onTime: ByteArray = byteArrayOf(0x00, 0x00)
    val offTime: ByteArray = byteArrayOf(0x00, 0x00)
    val onTime2: ByteArray = byteArrayOf(0x00, 0x00)
    val offTime2: ByteArray = byteArrayOf(0x00, 0x00)

    val checksum: Byte = (stx + mode + day +
            onTime[0] + onTime[1] +
            offTime[0] + offTime[1] +
            onTime2[0] + onTime2[1] +
            offTime2[0] + offTime2[1]).toByte()

    val packet: ByteArray = byteArrayOf(
        stx, mode, day,
        onTime[0], onTime[1],
        offTime[0], offTime[1],
        onTime2[0], onTime2[1],
        offTime2[0], offTime2[1],
        checksum, 0x0D, 0x0A
    )

    characteristic.value = packet
    val result = gatt.writeCharacteristic(characteristic)

    Log.d("BLETEST", "send current status ${result}")
}

private fun requestData(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {

    val CHECKSUM: Byte = 207.toByte()

    val packet: ByteArray = byteArrayOf(207.toByte(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, CHECKSUM, 13, 10)

    Log.d("BLETEST", packet.contentToString())
    sendBytesToDevice(gatt, characteristic, packet)
}

@SuppressLint("MissingPermission")
private fun sendBytesToDevice(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, bytes: ByteArray) {
    characteristic.value = bytes
    val result = gatt.writeCharacteristic(characteristic)

    Log.d("BLETEST", "send bytes to device ${result}")
}