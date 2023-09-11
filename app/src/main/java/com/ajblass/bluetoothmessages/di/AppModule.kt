package com.ajblass.bluetoothmessages.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManager
import com.ajblass.bluetoothmessages.feature.bluetooth.MyBluetoothManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

	companion object {

		@Singleton
		@Provides
		fun providesBluetoothManager(@ApplicationContext appContext: Context): BluetoothManager? {
			return appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
		}

		@Singleton
		@Provides
		fun providesBluetoothAdapter(bluetoothManager: BluetoothManager?): BluetoothAdapter? {
			return bluetoothManager?.adapter
		}

	}
}
