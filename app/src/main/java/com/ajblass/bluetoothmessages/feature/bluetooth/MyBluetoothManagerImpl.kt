package com.ajblass.bluetoothmessages.feature.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ajblass.bluetoothmessages.data.EventResult
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManager.Companion.BLUETOOTH_KEY
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManager.Companion.TAG
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the state of the Bluetooth feature on a user's device and acts as a centralized class for
 * Bluetooth operations and related logic.
 */
@ActivityScoped
class MyBluetoothManagerImpl @Inject constructor(
	@ActivityContext private val context: Context,
	private val bluetoothAdapter: BluetoothAdapter?,
) : DefaultLifecycleObserver, MyBluetoothManager {

	private val activity = context as ComponentActivity
	private val registry: ActivityResultRegistry = activity.activityResultRegistry

	private val deviceList: MutableList<BluetoothDevice> = mutableListOf()

	private val bluetoothStateReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent) {
			activity.lifecycleScope.launch {
				if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
					if (bluetoothAdapter?.state == BluetoothAdapter.STATE_OFF) {
						Log.d(TAG, "Bluetooth is OFF")
						_state.value = BluetoothState.DISABLED
						requestActionEnableBluetooth()
					}
					if (bluetoothAdapter?.state == BluetoothAdapter.STATE_ON) {
						Log.d(TAG, "Bluetooth is ON")
						_state.value = BluetoothState.ENABLED
					}
				}
			}
		}
	}

	private val deviceFoundReceiver = object : BroadcastReceiver() {
		@Suppress("DEPRECATION")
		@SuppressLint("MissingPermission")
		override fun onReceive(context: Context, intent: Intent) {
			when (intent.action.toString()) {
				BluetoothDevice.ACTION_FOUND -> {
					val device: BluetoothDevice? =
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
							intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
						else
							intent.getParcelableExtra(
								BluetoothDevice.EXTRA_DEVICE,
								BluetoothDevice::class.java
							)

					device?.let { deviceList.add(it) }
					Log.d(TAG, "Found device ${device?.name} at ${device?.name}")
				}
			}
		}
	}

	private lateinit var enableBluetoothResultLauncher: ActivityResultLauncher<Intent>

	private var isAllowed = true

	private val _state: MutableState<BluetoothState> =
		mutableStateOf(
			if (bluetoothAdapter?.isEnabled == true) BluetoothState.ENABLED
			else BluetoothState.DISABLED
		)
	val state: State<BluetoothState> = _state

	override fun onCreate(owner: LifecycleOwner) {
		super.onCreate(owner)
		activity.lifecycle.addObserver(this)
		enableBluetoothResultLauncher = registerHandler(owner)

		ContextCompat.registerReceiver(
			activity,
			deviceFoundReceiver,
			IntentFilter(BluetoothDevice.ACTION_FOUND),
			ContextCompat.RECEIVER_EXPORTED
		)
	}

	override fun onResume(owner: LifecycleOwner) {
		super.onResume(owner)

		ContextCompat.registerReceiver(
			activity,
			bluetoothStateReceiver,
			IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
			ContextCompat.RECEIVER_EXPORTED
		)

		if (bluetoothAdapter?.isEnabled == false) {
			requestActionEnableBluetooth()
		}
	}

	override fun onPause(owner: LifecycleOwner) {
		super.onPause(owner)
		activity.unregisterReceiver(bluetoothStateReceiver)
	}

	override fun onDestroy(owner: LifecycleOwner) {
		super.onDestroy(owner)
		activity.unregisterReceiver(deviceFoundReceiver)
	}

	@SuppressLint("MissingPermission")
	override suspend fun discoverDevices(): EventResult<List<BluetoothDevice>, MyBluetoothManager.BluetoothServiceError> {
		Log.d(TAG, "discoverDevices()")

		if (isPermissionGranted()) {
			deviceList.clear()
			bluetoothAdapter?.startDiscovery()
			delay(MyBluetoothManager.SCAN_DURATION)
			return cancelDiscovery()
		}

		return EventResult.Error(MyBluetoothManager.BluetoothServiceError.PermissionNotGranted)
	}

	@SuppressLint("MissingPermission")
	override suspend fun cancelDiscovery(): EventResult<List<BluetoothDevice>, MyBluetoothManager.BluetoothServiceError> {
		Log.d(TAG, "cancelDiscovery()")

		if (isPermissionGranted()) {
			bluetoothAdapter?.cancelDiscovery()
			return EventResult.Success(deviceList.distinctBy { it.address })
		}

		return EventResult.Error(MyBluetoothManager.BluetoothServiceError.PermissionNotGranted)
	}

	private fun registerHandler(owner: LifecycleOwner): ActivityResultLauncher<Intent> {
		return registry.register(
			BLUETOOTH_KEY,
			owner,
			ActivityResultContracts.StartActivityForResult()
		) { result ->
			when (result.resultCode) {
				Activity.RESULT_OK -> {
					Log.d(TAG, "Bluetooth is ON")
					_state.value = BluetoothState.ENABLED
				}

				Activity.RESULT_CANCELED -> {
					isAllowed = false
				}
			}
		}
	}

	private fun requestActionEnableBluetooth() {
		try {

			// Don't spam the user with requests if they already denied bluetooth access
			if (isAllowed) {
				val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
				enableBluetoothResultLauncher.launch(intent)
			}

		} catch (e: Exception) {
			Log.e(TAG, "${e.message}")
		}
	}

	private fun isPermissionGranted(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
				hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
		} else {
			hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
		}
	}

	private fun hasPermission(permissionType: String): Boolean {
		return ActivityCompat.checkSelfPermission(
			context,
			permissionType
		) == PackageManager.PERMISSION_GRANTED
	}
}