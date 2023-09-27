package com.ajblass.bluetoothmessages.feature.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Build
import com.ajblass.bluetoothmessages.data.EventResult

interface MyBluetoothManager {

	companion object {
		internal const val TAG = "MyBluetoothManager"

		internal const val BLUETOOTH_KEY = "EnableBluetooth"
		internal const val MY_DEVICE_NAME = "Bluetooth 5.1 Keyboard"
		internal const val MY_UUID = "00001124-0000-1000-8000-00805f9b34fb"

		const val SCAN_DURATION: Long = 5000

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

	suspend fun discoverDevices(): EventResult<List<BluetoothDevice>, BluetoothError>
	suspend fun cancelDiscovery(): EventResult<List<BluetoothDevice>, BluetoothError>
	suspend fun connect(device: BluetoothDevice): EventResult<Boolean, BluetoothError>
	suspend fun disconnect(): EventResult<Boolean, BluetoothError>

	sealed class BluetoothError {
		data object PermissionNotGranted : BluetoothError()
		data object UnableToPair : BluetoothError()
	}
}