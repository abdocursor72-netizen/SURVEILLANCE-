package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ReportViewModel
import com.example.ui.ScreenDestination
import com.example.ui.screens.ExportReportScreen
import com.example.ui.screens.CropCameraScreen
import com.example.ui.screens.ReportEditorScreen
import com.example.ui.screens.ReportsListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ReportViewModel = viewModel()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()

            val isDark = when (themeMode) {
                "داكن" -> true
                "فاتح" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        BackHandler(enabled = currentScreen != ScreenDestination.REPORTS_LIST) {
                            when (currentScreen) {
                                ScreenDestination.EXPORT_REPORT -> viewModel.navigateBackFromExport()
                                ScreenDestination.CROP_CAMERA -> viewModel.navigateBackFromCamera()
                                else -> viewModel.navigateTo(ScreenDestination.REPORTS_LIST)
                            }
                        }

                        when (currentScreen) {
                            ScreenDestination.REPORTS_LIST -> ReportsListScreen(viewModel = viewModel)
                            ScreenDestination.REPORT_EDITOR -> ReportEditorScreen(viewModel = viewModel)
                            ScreenDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            ScreenDestination.EXPORT_REPORT -> ExportReportScreen(viewModel = viewModel)
                            ScreenDestination.CROP_CAMERA -> CropCameraScreen(viewModel = viewModel)
                            else -> ReportsListScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
