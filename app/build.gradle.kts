plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("kotlin-kapt")
	id("kotlin-android")
	id("com.google.dagger.hilt.android")
	id("dagger.hilt.android.plugin")
}

android {
	namespace = "com.ajblass.bluetoothmessages"
	compileSdk = 33

	defaultConfig {
		applicationId = "com.ajblass.bluetoothmessages"
		minSdk = 26
		targetSdk = 33
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.1"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {

	val accompanistPermissionsVersion = "0.30.1"
	val androidxCoreTestingVersion = "2.2.0"
	val androidxEspressoVersion = "3.5.1"
	val androidxJunitVersion = "1.1.5"
	val androidxVersion = "1.10.1"
	val composeVersion = "1.7.2"
	val hiltVersion = "2.48"
	val junitVersion = "4.13.2"
	val lifecycleVersion = "2.6.1"

	implementation("androidx.core:core-ktx:$androidxVersion")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
	implementation("androidx.activity:activity-compose:$composeVersion")
	implementation(platform("androidx.compose:compose-bom:2023.03.00"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	// Hilt
	implementation("com.google.dagger:hilt-android:$hiltVersion")
	implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
	annotationProcessor("com.google.dagger:hilt-compiler:$hiltVersion")
	kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
	kapt("androidx.hilt:hilt-compiler:1.0.0")

	// Permissions
	implementation("com.google.accompanist:accompanist-permissions:$accompanistPermissionsVersion")

	// Android test
	androidTestImplementation("androidx.test.ext:junit:$androidxJunitVersion")
	androidTestImplementation("androidx.test.espresso:espresso-core:$androidxEspressoVersion")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")

	// Tests
	testImplementation("junit:junit:$junitVersion")
	testImplementation("androidx.arch.core:core-testing:$androidxCoreTestingVersion")
}