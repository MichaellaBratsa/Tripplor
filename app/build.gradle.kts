plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.maps)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.mbrats01.epl498_group_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mbrats01.epl498_group_project"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    implementation(libs.net.cronet.api)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Google Play services for location [This wasn't added by default]
    implementation(libs.google.location)
    // Call API Package [This wasn't added by default]
    implementation(libs.cronet.api)
    // Display Image Package [This wasn't added by default]
    implementation(libs.picasso)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)


    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
}