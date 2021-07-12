package com.prof18.moneyflow.features.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prof18.moneyflow.ComposeNavigationFactory
import com.prof18.moneyflow.R
import com.prof18.moneyflow.Screen
import com.prof18.moneyflow.ui.style.AppMargins
import com.prof18.moneyflow.ui.style.MoneyFlowTheme
import org.koin.androidx.compose.getViewModel

object SettingsScreenFactory : ComposeNavigationFactory {
    override fun create(navGraphBuilder: NavGraphBuilder, navController: NavController) {
        navGraphBuilder.composable(Screen.SettingsScreen.route) {
            val viewModel = getViewModel<SettingsViewModel>()

            SettingsScreen(
                performBackup = { uri -> viewModel.performBackup(uri) },
                performRestore = { uri -> viewModel.performRestore(uri) }
            )
        }
    }
}

@Composable
fun SettingsScreen(
    performBackup: (Uri) -> Unit,
    performRestore: (Uri) -> Unit,
) {

    val context = LocalContext.current

    val createFileURI = remember { mutableStateOf<Uri?>(null) }
    val createFileAction =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument()) {
            createFileURI.value = it
        }
    createFileURI.value?.let { uri ->
        performBackup(uri)
        Toast.makeText(context, stringResource(R.string.db_export_completed), Toast.LENGTH_SHORT)
            .show()
    }

    val openFileURI = remember { mutableStateOf<Uri?>(null) }
    val openFileAction = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        openFileURI.value = it
    }
    openFileURI.value?.let { uri ->
        performRestore(uri)
        Toast.makeText(context, stringResource(R.string.db_import_completed), Toast.LENGTH_SHORT)
            .show()
    }

    SettingsScreenContent(
        onImportDatabaseClick = { openFileAction.launch(arrayOf("*/*")) },
        onExportDatabaseClick = { createFileAction.launch("MoneyFlowDB.db") },
        openDropboxSetup = {
            context.startActivity(
                Intent(
                    context,
                    DropboxLoginActivity::class.java
                )
            )
        }
    )
}

@Composable
private fun SettingsScreenContent(
    onImportDatabaseClick: () -> Unit,
    onExportDatabaseClick: () -> Unit,
    openDropboxSetup: () -> Unit,
) {
    Scaffold(
        topBar = {
            Text(
                text = stringResource(id = R.string.settings_screen),
                style = MaterialTheme.typography.h4,
                modifier = Modifier
                    .padding(horizontal = AppMargins.regular)
                    .padding(top = AppMargins.regular)
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(top = AppMargins.regular)
            ) {
                Text(
                    stringResource(R.string.import_database),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onImportDatabaseClick() }
                        .padding(AppMargins.regular)
                )
                Divider()
                Text(
                    stringResource(R.string.export_database),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExportDatabaseClick() }
                        .padding(AppMargins.regular)
                )
                Divider()
                Text(
                    stringResource(R.string.setup_dropbox),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openDropboxSetup() }
                        .padding(AppMargins.regular)
                )
                Divider()
            }
        }
    )
}

@Preview
@Composable
fun SettingsScreenLightPreview() {
    MoneyFlowTheme {
        Surface {
            SettingsScreenContent(
                onImportDatabaseClick = {},
                onExportDatabaseClick = {},
                openDropboxSetup = {}
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenDarkPreview() {
    MoneyFlowTheme(darkTheme = true) {
        Surface {
            SettingsScreenContent(
                onImportDatabaseClick = {},
                onExportDatabaseClick = {},
                openDropboxSetup = {}
            )
        }
    }
}