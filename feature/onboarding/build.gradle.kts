plugins {

    alias(libs.plugins.android.library)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

}


android {

    namespace = "com.pelvictrainer.feature.onboarding"

    compileSdk = 35


    defaultConfig {

        minSdk = 26

    }


    buildFeatures {

        compose = true

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
        project(":domain")
    )


    implementation(
        project(":core:designsystem")
    )


    implementation(
        libs.androidx.lifecycle.runtime
    )


    implementation(
        libs.androidx.lifecycle.viewmodel.compose
    )


    implementation(
        platform(
            libs.compose.bom
        )
    )


    implementation(
        libs.compose.ui
    )


    implementation(
        libs.compose.material3
    )


    implementation(
        libs.hilt.android
    )


    implementation(
        libs.hilt.navigation.compose
    )

}