package com.example.cintia.pcryal

import android.os.Environment

class EnvironmentHelper {
    val storageDirectory: String?
        get() = Environment.getExternalStorageDirectory().absolutePath
}