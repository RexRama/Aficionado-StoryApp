plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
    id("kotlin-parcelize")
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.rexrama.aficionado"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rexrama.aficionado"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            // Use the BASE_URL and API_KEY from local.properties
            buildConfigField("String", "BASE_URL", "\"${project.findProperty("BASE_URL")}\"")
            val mapsApiKey: String = project.findProperty("MAPS_API_KEY") as String? ?: ""
            buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
            manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            // Use the BASE_URL and API_KEY from local.properties
            buildConfigField("String", "BASE_URL", "\"${project.findProperty("BASE_URL")}\"")
            val mapsApiKey: String = project.findProperty("MAPS_API_KEY") as String? ?: ""
            buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
            manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Map
    implementation (libs.play.services.maps)
    implementation (libs.play.services.location)

    //Testing
    testImplementation(libs.androidx.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)


    //Circle ImageView
    implementation(libs.circleimageview)

    //Camera
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.exifinterface)


    //Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    //DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

}


