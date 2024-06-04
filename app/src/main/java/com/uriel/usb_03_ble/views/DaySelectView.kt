package com.uriel.usb_03_ble.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uriel.usb_03_ble.R
import com.uriel.usb_03_ble.ui.theme.Pretendard
import com.uriel.usb_03_ble.ui.theme.UrielDarkGray
import com.uriel.usb_03_ble.ui.theme.UrielTextDark
import com.uriel.usb_03_ble.ui.theme.UrielTextLight

@Composable
fun DaySelectView(modifier: Modifier) {
    val days = arrayOf("일", "월", "화", "수", "목", "금", "토")

    Column(modifier = modifier.fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(UrielDarkGray)
        .padding(all = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "요일 선택",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UrielTextLight
                ),
                modifier = Modifier.alpha(0.4F)
            )

            Image(painter = painterResource(id = R.drawable.white_arrow),
                contentDescription = "logo",
                modifier = Modifier)
        }

        Row(modifier = Modifier.padding(top = 10.dp)) {
            for (day in days) {
                Box(modifier = Modifier
                    .padding(end = 7.dp)
                    .clip(CircleShape)
                    .background(color = Color.White)
                    .weight(1.0F, fill = true)
                    .aspectRatio(1.0F),
                    contentAlignment = Alignment.Center) {
                    Text(day,
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = UrielTextDark
                        ))
                }
            }
        }
    }
}


@Composable
@Preview
fun DaySelectView_Preview() {
    DaySelectView(modifier = Modifier)
}