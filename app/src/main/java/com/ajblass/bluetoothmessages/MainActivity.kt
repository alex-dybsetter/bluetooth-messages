package com.ajblass.bluetoothmessages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManagerImpl
import com.ajblass.bluetoothmessages.navigation.MyNavigation
import com.ajblass.bluetoothmessages.ui.theme.BluetoothMessagesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	@Inject
	lateinit var myBluetoothManager: MyBluetoothManagerImpl

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		lifecycle.addObserver(myBluetoothManager)

		setContent {
			BluetoothMessagesTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					MyNavigation(myBluetoothManager)
				}
			}
		}
	}
}