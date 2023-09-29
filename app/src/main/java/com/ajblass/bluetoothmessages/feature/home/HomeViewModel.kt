package com.ajblass.bluetoothmessages.feature.home

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajblass.bluetoothmessages.data.EventResult
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
	private val bluetoothManager: MyBluetoothManager
) : ViewModel() {

	private var _uiState: MutableStateFlow<HomeScreenState> =
		MutableStateFlow(HomeScreenState.ScanSuccess(emptyList()))
	val uiState: StateFlow<HomeScreenState> = _uiState

	fun connectToKeyboard() {
		viewModelScope.launch(Dispatchers.IO) {
			bluetoothManager.connectToKeyboard()
		}
	}

	fun scanDevices() {
		_uiState.value = HomeScreenState.Scanning
		viewModelScope.launch(Dispatchers.IO) {
			val result = bluetoothManager.discoverDevices()
			_uiState.value =
				if (result is EventResult.Success) HomeScreenState.ScanSuccess(result.data)
				else HomeScreenState.Error
		}
	}

	fun stopScan() {
		viewModelScope.launch(Dispatchers.IO) {
			val result = bluetoothManager.cancelDiscovery()
			_uiState.value =
				if (result is EventResult.Success) HomeScreenState.ScanSuccess(result.data)
				else HomeScreenState.Error
		}
	}

	fun clearResults() {
		_uiState.value = HomeScreenState.ScanSuccess(emptyList())
	}

	fun connect(device: BluetoothDevice) {
		_uiState.value = HomeScreenState.Pairing(device)
		viewModelScope.launch(Dispatchers.IO) {
			val result = bluetoothManager.connect(device)
			_uiState.value =
				if (result is EventResult.Success) HomeScreenState.PairingSuccess(device)
				else HomeScreenState.Error
		}
	}

	fun disconnect() {
		_uiState.value = HomeScreenState.ScanSuccess()
		viewModelScope.launch(Dispatchers.IO) {
			bluetoothManager.disconnect()
		}
	}
}