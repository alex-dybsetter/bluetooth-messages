package com.ajblass.bluetoothmessages.feature.bluetooth

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The state of the bluetooth feature on a user's device.
 */
enum class BluetoothState {
	DISABLED,
	ENABLED
}

var LocalBluetoothState = staticCompositionLocalOf { BluetoothState.DISABLED }