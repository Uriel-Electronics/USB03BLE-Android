package com.uriel.usb_03_ble.views

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.usb_03_ble.views.TimeFormatExt.timeFormat
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TimerViewModel(): ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private val hour = TimeUnit.HOURS.toMillis(1)
    private val minute = TimeUnit.MINUTES.toMillis(0)
    private val second = TimeUnit.SECONDS.toMillis(0)

    val initialTotalTimeInMillis = hour + minute + second
    var timeLeft = mutableStateOf(initialTotalTimeInMillis)
    val countDownInterval = 1000L

    var timerText = mutableStateOf(timeLeft.value.timeFormat())

    val isPlaying = mutableStateOf(false)

    fun startCountDownTimer() = viewModelScope.launch {
        countDownTimer = object : CountDownTimer(timeLeft.value, countDownInterval) {
            override fun onTick(p0: Long) {
                timerText.value = p0.timeFormat()
                timeLeft.value = p0

                Log.d("BLETEST", timerText.value)
            }

            override fun onFinish() {
                timerText.value = initialTotalTimeInMillis.timeFormat()
                isPlaying.value = false
            }
        }
            .start()
    }

    fun stopCountDownTimer() = viewModelScope.launch {
        countDownTimer?.cancel()
    }

    fun resetCountDownTimer() = viewModelScope.launch {
        countDownTimer?.cancel()

        timerText.value = initialTotalTimeInMillis.timeFormat()
        timeLeft.value = initialTotalTimeInMillis
    }
}

