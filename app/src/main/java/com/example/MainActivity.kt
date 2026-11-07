package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.LauncherDatabase
import com.example.data.repository.LauncherRepository
import com.example.ui.screens.PixelHomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherSheet
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.LauncherViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels {
        val db = LauncherDatabase.getDatabase(applicationContext)
        val repository = LauncherRepository(applicationContext, db.launcherDao())
        LauncherViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val config by viewModel.launcherConfig.collectAsStateWithLifecycle()
            val isDrawerOpen by viewModel.isDrawerOpen.collectAsStateWithLifecycle()
            val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
            val activeFolder by viewModel.activeFolder.collectAsStateWithLifecycle()

            // Back gesture navigation handling
            BackHandler(enabled = isDrawerOpen || activeSheet != null || activeFolder != null) {
                when {
                    activeFolder != null -> viewModel.closeFolder()
                    activeSheet != null -> viewModel.closeSheet()
                    isDrawerOpen -> viewModel.closeDrawer()
                }
            }

            MyApplicationTheme(
                paletteName = config.colorPaletteName,
                darkModePreference = config.darkModePreference
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PixelHomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
