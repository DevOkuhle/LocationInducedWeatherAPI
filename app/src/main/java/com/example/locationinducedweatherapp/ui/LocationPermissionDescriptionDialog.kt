package com.example.locationinducedweatherapp.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.locationinducedweatherapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPermissionDescriptionDialog(
    isPermanentlyDeclined: Boolean,
    onDismissed: () -> Unit,
    onAcceptButton: () -> Unit,
    onGoToAppSetting: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissed() },
        confirmButton = {
            TextButton(onClick = {
                if (isPermanentlyDeclined) {
                    onGoToAppSetting
                } else {
                    onAcceptButton()
                }
            }) {
                Text(
                    text = stringResource(if (isPermanentlyDeclined) R.string.open_app_settings else R.string.grant_permission),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.location_permission_required),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = stringResource(if (isPermanentlyDeclined) R.string.location_permission_permanent_decline_message else R.string.location_permission_enquiry),
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}