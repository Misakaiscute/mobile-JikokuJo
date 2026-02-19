package com.jikokujo.core.utils

import android.os.Build

object EmulatorDetector {
    val isEmulator: Boolean by lazy {
        Build.MANUFACTURER == "Google" &&
            Build.BRAND == "google" &&
            Build.DEVICE.startsWith("generic") &&
            Build.PRODUCT == "sdk_gphone" || Build.PRODUCT.startsWith("sdk_gphone") ||
            Build.HARDWARE in listOf("goldfish", "ranchu", "x86", "x86_64") ||
            Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("Emulator", ignoreCase = true) ||
            Build.MODEL.contains("sdk", ignoreCase = true) ||
            isQemuKernel()
    }

    @Suppress("PrivateApi")
    private fun isQemuKernel(): Boolean {
        return try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)
            val qemu = get.invoke(null, "ro.kernel.qemu") as? String
            qemu == "1"
        } catch (e: Throwable) {
            false
        }
    }
}