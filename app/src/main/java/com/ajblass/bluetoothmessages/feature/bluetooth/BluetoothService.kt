package com.ajblass.bluetoothmessages.feature.bluetooth

import android.Manifest
import android.os.Build

/**
 * A layer of abstraction between the Android Bluetooth apis and the application code.
 */
interface BluetoothService {

	companion object {
		val requiredPermissions =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(
				Manifest.permission.BLUETOOTH_SCAN,
				Manifest.permission.BLUETOOTH_CONNECT,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			)
			else listOf(
				Manifest.permission.BLUETOOTH_ADMIN,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			)
	}
}