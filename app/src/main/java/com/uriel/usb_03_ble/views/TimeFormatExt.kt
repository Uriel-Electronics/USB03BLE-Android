package com.uriel.usb_03_ble.views

import java.util.concurrent.TimeUnit

object TimeFormatExt {
    private const val FORMAT = "%02d:%02d:%02d"

    fun Long.timeFormat(): String = String.format(
        FORMAT,
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )
}