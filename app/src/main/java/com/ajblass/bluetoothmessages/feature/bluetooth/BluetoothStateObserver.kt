package com.ajblass.bluetoothmessages.feature.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Observes the state of the device's Bluetooth feature to prompt the user to turn on Bluetooth
 * when necessary.
 */
@ActivityScoped
class BluetoothStateObserver @Inject constructor(
	@ActivityContext context: Context,
	private val bluetoothAdapter: BluetoothAdapter?,
) : DefaultLifecycleObserver {

	companion object {
		private const val BLE_KEY = "EnableBLE"
		private const val TAG = "BluetoothStateObserver"
	}

	private val activity = context as ComponentActivity
	private val registry: ActivityResultRegistry = activity.activityResultRegistry

	private lateinit var broadcastReceiver: BroadcastReceiver
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
		broadcastReceiver = createBroadcastReceiver()
		enableBluetoothResultLauncher = registerHandler(owner)
	}

	override fun onResume(owner: LifecycleOwner) {
		super.onResume(owner)

		ContextCompat.registerReceiver(
			activity,
			broadcastReceiver,
			IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
			ContextCompat.RECEIVER_EXPORTED
		)

		if (bluetoothAdapter?.isEnabled == false) {
			requestActionEnableBluetooth()
		}
	}

	override fun onPause(owner: LifecycleOwner) {
		super.onPause(owner)
		activity.unregisterReceiver(broadcastReceiver)
	}

	private fun createBroadcastReceiver() = object : BroadcastReceiver() {
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

	private fun registerHandler(owner: LifecycleOwner): ActivityResultLauncher<Intent> {
		return registry.register(
			BLE_KEY,
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
}