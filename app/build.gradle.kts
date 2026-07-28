plugins {

    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)

    alias(libs.plugins.kotlin.kapt)

}


android {

    namespace = "com.pelvictrainer"

    compileSdk = 35


    defaultConfig {

        applicationId = "com.pelvictrainer"

        minSdk = 26

        targetSdk = 35

        versionCode = 1

        versionName = "1.0"

    }


    buildTypes {

        release {

            isMinifyEnabled = false

        }

    }


    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_17

        targetCompatibility = JavaVersion.VERSION_17

    }


    kotlinOptions {

        jvmTarget = "17"

    }


    buildFeatures {

        compose = true

    }

}


dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(project(":core:common"))

    implementation(project(":core:navigation"))

    implementation(project(":core:datastore"))

    implementation(project(":core:database"))

    implementation(project(":core:network"))

    implementation(project(":core:designsystem"))


    implementation(project(":feature:training"))


    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.activity.compose)


    implementation(platform(libs.compose.bom))

    implementation(libs.compose.ui)

    implementation(libs.compose.material3)


    implementation(libs.hilt.android)

    implementation(libs.hilt.navigation.compose)

    kapt(libs.hilt.compiler)

}