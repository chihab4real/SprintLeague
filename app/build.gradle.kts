plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.sprintleague"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sprintleague"
        minSdk = 28
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
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    implementation("com.google.firebase:firebase-analytics")


    
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("com.google.firebase:firebase-database")

    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.firebase:firebase-storage")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")





}

apply(plugin = "com.google.gms.google-services")