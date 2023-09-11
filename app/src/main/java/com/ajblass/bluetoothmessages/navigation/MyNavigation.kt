package com.ajblass.bluetoothmessages.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManagerImpl
import com.ajblass.bluetoothmessages.feature.home.HomeRoute

@Composable
fun MyNavigation(myBluetoothManager: MyBluetoothManagerImpl) {
	val navController = rememberNavController()
	NavHost(
		navController = navController,
		startDestination = Screens.HomeScreen.name
	) {
		composable(route = Screens.HomeScreen.name) {
			HomeRoute(myBluetoothManager = myBluetoothManager)
		}
	}
}