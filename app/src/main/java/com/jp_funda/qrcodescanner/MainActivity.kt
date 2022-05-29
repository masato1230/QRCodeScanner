package com.jp_funda.qrcodescanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.jp_funda.qrcodescanner.ui.theme.QRCodeScannerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkCameraPermission {
            setContent {
                QRCodeScannerTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        MainContent()
                    }
                }
            }
        }
    }

    private fun checkCameraPermission(onGranted: () -> Unit) {
        val cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> // パーミッションダイアログ表示後に呼び出されるコールバック
                if (isGranted) {
                    // パーミッションが与えられたら、onGrantedコールバックを呼び出す
                    onGranted()
                } else {
                    // バーミッションが与えられなかった時は、設定アプリを開き、このアプリを終了する
                    val settingsAppUri = "package:$packageName"
                    val intent =
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse(settingsAppUri),
                        )
                    startActivity(intent)
                    finish()
                }
            }

        // カメラパーミッションが与えられていれば、onGrantedコールバックを呼び出し、そうでなければパーミッションをリクエストする
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
fun MainContent() {
    val detectedQrCode = remember { mutableStateOf("") }

    CameraPreview(modifier = Modifier.fillMaxSize()) {
        detectedQrCode.value = it
    }

    Text(
        text = detectedQrCode.value,
        color = Color.White,
        style = MaterialTheme.typography.h6,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(vertical = 10.dp)
    )
}