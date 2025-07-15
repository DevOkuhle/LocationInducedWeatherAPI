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
import com.example.locationinducedweatherapp.util.Constants.Companion.PERMISSION_TYPE_GPS_REQUEST
import com.example.locationinducedweatherapp.util.Constants.Companion.PERMISSION_TYPE_LOCATION_REQUEST

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPermissionDescriptionDialog(
    permissionTypeStatusCode: Int,
    onDismissed: () -> Unit,
    onAcceptButton: () -> Unit = {},
    onGoToAppSetting: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { onDismissed() },
        confirmButton = {
            TextButton(
                onClick = {
                    when(permissionTypeStatusCode) {
                        PERMISSION_TYPE_LOCATION_REQUEST, PERMISSION_TYPE_GPS_REQUEST -> onAcceptButton()
                        else -> onGoToAppSetting()
                    }
                }
            ) {
                val confirmButtonTitle = when(permissionTypeStatusCode) {
                    PERMISSION_TYPE_LOCATION_REQUEST, PERMISSION_TYPE_GPS_REQUEST -> stringResource(R.string.grant_permission)
                    else -> stringResource(R.string.open_app_settings)
                }
                Text(
                    text = confirmButtonTitle,
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
            val locationPermissionDescription = when(permissionTypeStatusCode) {
                PERMISSION_TYPE_LOCATION_REQUEST -> stringResource(R.string.location_permission_enquiry)
                PERMISSION_TYPE_GPS_REQUEST -> stringResource(R.string.enable_gps_location_enquiry)
                else -> stringResource(R.string.location_permission_permanent_decline_message)
            }
            Text(
                text = locationPermissionDescription,
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}