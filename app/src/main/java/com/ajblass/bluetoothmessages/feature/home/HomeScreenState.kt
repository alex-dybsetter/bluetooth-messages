package com.ajblass.bluetoothmessages.feature.home

import android.bluetooth.BluetoothDevice

sealed interface HomeScreenState {
	data object Scanning : HomeScreenState

	data class ScanSuccess(val result: List<BluetoothDevice> = emptyList()) : HomeScreenState

	data class Pairing(val peripheral: BluetoothDevice) : HomeScreenState

	data class PairingSuccess(val peripheral: BluetoothDevice) : HomeScreenState

	data object Error : HomeScreenState
}