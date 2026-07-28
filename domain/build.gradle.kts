plugins {

    kotlin("jvm")

    kotlin("kapt")

}


kotlin {

    jvmToolchain(17)

}


dependencies {


    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2"
    )


    implementation(
        "javax.inject:javax.inject:1"
    )


}