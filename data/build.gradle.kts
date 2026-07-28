plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}


android {

    namespace = "com.pelvictrainer.data"

    compileSdk = 35


    defaultConfig {
        minSdk = 26
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


    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1"
    )


    implementation(
        "androidx.room:room-runtime:2.7.0"
    )


    implementation(
        "androidx.room:room-ktx:2.7.0"
    )


    implementation(project(":domain"))

    implementation(project(":core:database"))

    implementation(project(":core:datastore"))


    implementation(libs.androidx.core.ktx)


    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)

}