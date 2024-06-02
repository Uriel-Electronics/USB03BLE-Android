package com.uriel.usb_03_ble.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.usb_03_ble.ui.theme.Pretendard
import com.uriel.usb_03_ble.ui.theme.UrielDarkGray
import com.uriel.usb_03_ble.ui.theme.UrielTextDark
import com.uriel.usb_03_ble.ui.theme.UrielTextLight
import kotlinx.coroutines.delay

@Composable
fun TimerView(modifier: Modifier) {
    val time: MutableState<Int> = remember { mutableStateOf(3600) }
    val timerOn = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (time.value != 0 && timerOn.value) {
            time.value--
            delay(1000L)
        }
    }

    Box(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(UrielDarkGray)
            .padding(all = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                val hour: String = if (time.value / 3600 == 1) "01" else "00"
                val minute: String = if ((time.value % 3600 / 60) > 10) "${(time.value % 3600) / 60}" else "0${(time.value % 3600) / 60}"
                val second: String = if (((time.value % 3600) % 60) % 60 > 10) "${((time.value % 3600) % 60) % 60}" else "0${((time.value % 3600) % 60) % 60}"

                Text(text = "타이머",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = UrielTextLight
                    ),
                    modifier = Modifier.alpha(0.4F)
                )
                Text(text = "${hour}:${minute}:${second}",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = UrielTextLight
                    )
                )
            }

            Box(modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .clip(CircleShape)
                .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("시작",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = UrielTextDark
                    )
                )

            }
        }
    }
}
