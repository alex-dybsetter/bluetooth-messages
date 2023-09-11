@file:OptIn(ExperimentalPermissionsApi::class)

package com.ajblass.bluetoothmessages.feature.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ajblass.bluetoothmessages.R
import com.ajblass.bluetoothmessages.feature.bluetooth.BluetoothState
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManager
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManagerImpl
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
internal fun HomeRoute(
	myBluetoothManager: MyBluetoothManagerImpl
) {

	val viewmodel = remember {
		HomeViewModel(myBluetoothManager)
	}

	val uiState by viewmodel.uiState.collectAsState()
	val permissionsState =
		rememberMultiplePermissionsState(permissions = MyBluetoothManager.requiredPermissions)

	HomeScreen(
		bluetoothState = BluetoothState.ENABLED,
		uiState = uiState,
		permissionsState = permissionsState,
		scanDevices = viewmodel::scanDevices,
		connectToDevice = viewmodel::connect,
		disconnectFromDevice = viewmodel::disconnect,
		clearScanResults = viewmodel::clearResults
	)
}

@Composable
internal fun HomeScreen(
	bluetoothState: BluetoothState,
	uiState: HomeScreenState,
	permissionsState: MultiplePermissionsState,
	scanDevices: () -> Unit,
	connectToDevice: (BluetoothDevice) -> Unit,
	disconnectFromDevice: () -> Unit,
	clearScanResults: () -> Unit,
) {
	Column(
		modifier = Modifier
			.padding(16.dp)
			.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		if (!permissionsState.allPermissionsGranted) {
			RequestPermission(permissionsState = permissionsState)
		} else {
			when {
				bluetoothState == BluetoothState.DISABLED -> {
					Text(text = "Please turn Bluetooth on.")
				}

				uiState is HomeScreenState.Scanning -> {
					Text(text = "Scanning...")
					Spacer(modifier = Modifier.fillMaxHeight(.5f))
					CircularProgressIndicator()
				}

				uiState is HomeScreenState.ScanSuccess -> {
					ScanControls(
						scanDevices = scanDevices,
						clearScanResults = clearScanResults
					)
					DeviceList(
						deviceList = uiState.result,
						connectToDevice = connectToDevice,
					)
				}

				uiState is HomeScreenState.Pairing -> {
					Text(text = "Pairing devices...")
					Spacer(modifier = Modifier.fillMaxHeight(.5f))
					CircularProgressIndicator()
				}

				uiState is HomeScreenState.PairingSuccess -> {
					Text(text = "Paired with ")
				}

				else -> {
					ScanControls(
						scanDevices = scanDevices,
						clearScanResults = clearScanResults
					)
					Text(text = "Something went wrong.  Try again.")
				}
			}
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

@Composable
private fun ScanControls(
	scanDevices: () -> Unit,
	clearScanResults: () -> Unit
) {
	Column(
		modifier = Modifier.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = { scanDevices() }
		) {
			Text(text = "Scan Devices")
		}
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = { clearScanResults() }
		) {
			Text(text = "Clear Devices")
		}
	}
}

@SuppressLint("MissingPermission")
@Composable
private fun DeviceList(
	deviceList: List<BluetoothDevice>,
	connectToDevice: (BluetoothDevice) -> Unit,
) {
	Column(
		modifier = Modifier.padding(16.dp),
		horizontalAlignment = Alignment.Start
	) {
		Text(
			text = "Devices",
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.fillMaxWidth()
		)
		if (deviceList.isEmpty()) {
			Text(text = "Nothing to show")
		}
		LazyColumn {
			items(items = deviceList, key = { it.address }) { device ->
				Column {
					Text(text = "Name: ${device.name}")
					Text(text = "Type: ${device.type}")
					Text(text = "Address: ${device.address}")
					Text(text = "Bluetooth Class: ${device.bluetoothClass}")
					Text(text = "Bond State: ${device.bondState}")
					Button(
						modifier = Modifier.fillMaxWidth(),
						onClick = { connectToDevice(device) }
					) {
						Text(text = "Connect")
					}
				}
				Divider(modifier = Modifier.padding(vertical = 8.dp))
			}
		}
	}
}