package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.SelfieCameraModal
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainPortalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LogisticsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LogisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentUser by viewModel.currentUser.collectAsState()
                    val isLoginSelfieRequired by viewModel.isLoginSelfieRequired.collectAsState()
                    val authError by viewModel.authError.collectAsState()

                    when {
                        // 1. Not Authenticated: Display Login
                        currentUser == null -> {
                            LoginScreen(
                                authError = authError,
                                onLogin = { fidOrEmail, password ->
                                    viewModel.login(fidOrEmail, password)
                                }
                            )
                        }

                        // 2. Authenticated but Account has No Saved Selfie:
                        // Mandatory camera capture workflow before allowing entry to dashboard!
                        isLoginSelfieRequired -> {
                            SelfieCameraModal(
                                title = "Mandatory Login Verification Selfie",
                                subtitle = "Your franchise account requires a verified profile selfie. Align your face and capture below.",
                                isMandatoryLogin = true,
                                onSelfieConfirmed = { bitmap ->
                                    viewModel.saveSelfieImage(bitmap, fromLoginFlow = true)
                                },
                                onDismiss = {
                                    viewModel.cancelLoginSelfie()
                                }
                            )
                        }

                        // 3. Authenticated with Saved Selfie: Full Dashboard Access
                        else -> {
                            MainPortalScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
