package com.ajblass.bluetoothmessages.feature.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Build
import com.ajblass.bluetoothmessages.data.EventResult

interface MyBluetoothManager {

	companion object {
		internal const val TAG = "MyBluetoothManager"

		internal const val BLUETOOTH_KEY = "EnableBluetooth"

		const val SCAN_DURATION: Long = 5000
		const val CONNECT_TIMEOUT_DURATION: Long = 30_000
		const val DISCONNECT_TIMEOUT_DURATION: Long = 5000

		val requiredPermissions =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(
				Manifest.permission.BLUETOOTH_SCAN,
				Manifest.permission.BLUETOOTH_CONNECT
			)
			else listOf(
				Manifest.permission.BLUETOOTH_ADMIN,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			)
	}

	suspend fun discoverDevices(): EventResult<List<BluetoothDevice>, BluetoothServiceError>
	suspend fun cancelDiscovery(): EventResult<List<BluetoothDevice>, BluetoothServiceError>

	sealed class BluetoothServiceError {
		data object PermissionNotGranted : BluetoothServiceError()
	}
}