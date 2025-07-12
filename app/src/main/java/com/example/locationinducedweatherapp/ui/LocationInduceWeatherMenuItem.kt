package com.example.locationinducedweatherapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.locationinducedweatherapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInduceWeatherMenuItem(modifier: Modifier) {
    var showMenu by rememberSaveable { mutableStateOf(false) }
    TopAppBar(
        modifier = modifier,
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("")
        },
        navigationIcon = {
            IconButton(onClick = { showMenu }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Back"
                )
            }
        }
    )

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.add_favourite_location)) },
            onClick = {

            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.view_favourites)) },
            onClick = {
                showMenu = false
                // handle Logout action
            }
        )
    }
}

