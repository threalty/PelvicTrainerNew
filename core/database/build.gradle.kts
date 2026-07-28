plugins {

    alias(libs.plugins.android.library)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.ksp)

    alias(libs.plugins.hilt)

    alias(libs.plugins.kotlin.kapt)

}


android {

    namespace = "com.pelvictrainer.database"

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


    implementation("androidx.room:room-runtime:2.7.0")

    implementation("androidx.room:room-ktx:2.7.0")

    ksp("androidx.room:room-compiler:2.7.0")


    implementation(project(":domain"))


    implementation(libs.androidx.core.ktx)


    // Hilt
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)

}