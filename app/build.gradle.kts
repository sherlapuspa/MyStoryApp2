import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.dicoding.mystoryapp"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    defaultConfig {
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        manifestPlaceholders["googleMapsApiKey"] = properties.getProperty("MAPS_API_KEY")


        applicationId = "com.dicoding.mystoryapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        create("customDebugType") {
            isDebuggable = true
        }

        getByName("debug") {
            buildConfigField("String", "API_URL", "\"https://story-api.dicoding.dev/v1/\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), file("proguard-rules.pro")
            )
            buildConfigField("String", "API_URL", "\"https://story-api.dicoding.dev/v1/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"

    }
}

dependencies {
    testImplementation ("org.mockito:mockito-core:4.0.0")

    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    implementation("androidx.room:room-paging:2.5.1")


    implementation("androidx.test.espresso:espresso-contrib:3.4.0")

    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")

    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.mockito:mockito-inline:3.12.4")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    testImplementation("io.strikt:strikt-core:0.31.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver3:5.0.0-alpha.2")

    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")

    debugImplementation("androidx.fragment:fragment-testing:1.4.1")
    implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")//IntentsTestRule

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-places:17.0.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    implementation("androidx.room:room-paging:2.5.1")

    implementation("com.google.android.gms:play-services-maps:18.1.0")
    testImplementation("junit:junit:4.12")
    testImplementation("junit:junit:4.12")

    implementation("androidx.room:room-runtime:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")

    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation("id.zelory:compressor:3.0.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.5")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}