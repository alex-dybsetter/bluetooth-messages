@file:OptIn(ExperimentalPermissionsApi::class)

package com.ajblass.bluetoothmessages.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ajblass.bluetoothmessages.R
import com.ajblass.bluetoothmessages.feature.bluetooth.BluetoothService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
internal fun HomeRoute() {

	val permissionsState =
		rememberMultiplePermissionsState(permissions = BluetoothService.requiredPermissions)

	HomeScreen(permissionsState = permissionsState)
}

@Composable
internal fun HomeScreen(
	permissionsState: MultiplePermissionsState,
) {
	Column(
		modifier = Modifier.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		if (!permissionsState.allPermissionsGranted) {
			RequestPermission(permissionsState = permissionsState)
		}
	}
}

@Composable
private fun RequestPermission(
	permissionsState: MultiplePermissionsState
) {
	Column(
		modifier = Modifier.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		val prompt =
			if (permissionsState.shouldShowRationale) stringResource(id = R.string.request_permission)
			else stringResource(id = R.string.request_permission_rationale)
		Text(prompt)
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = { permissionsState.launchMultiplePermissionRequest() }
		) {
			Text(text = "Allow Permissions")
		}
	}
}