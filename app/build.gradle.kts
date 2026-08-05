plugins {

    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)

    kotlin("kapt")

}



android {

    namespace = "com.pelvictrainer.app"

    compileSdk = 36



    defaultConfig {

        applicationId = "com.pelvictrainer"

        minSdk = 26

        targetSdk = 36

        versionCode = 1

        versionName = "1.0"


        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

    }



    buildTypes {

        release {

            isMinifyEnabled = false


            proguardFiles(

                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),

                "proguard-rules.pro"

            )

        }

    }



    buildFeatures {

        compose = true

        buildConfig = true

    }



    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_17

        targetCompatibility = JavaVersion.VERSION_17

    }



    kotlin {

        jvmToolchain(17)

    }



    packaging {

        resources {

            excludes += "/META-INF/{AL2.0,LGPL2.1}"

        }

    }

}





dependencies {



    implementation(project(":feature:training"))

    implementation(project(":domain"))



    implementation(

        libs.androidx.core.ktx

    )



    implementation(

        libs.androidx.activity.compose

    )



    implementation(

        libs.androidx.navigation.compose

    )



    implementation(

        libs.hilt.android

    )



    implementation(

        libs.hilt.navigation.compose

    )



    kapt(

        libs.hilt.compiler

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



    debugImplementation(

        libs.compose.ui.tooling

    )

    implementation(project(":data"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime)

}