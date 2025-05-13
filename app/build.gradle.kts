plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "edu.alfonsaco.codezen"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.alfonsaco.codezen"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        android.buildFeatures.buildConfig = true
        buildConfigField("String", "GITHUB_CLIENTE", "\"${project.property("GITHUB_CLIENTE")}\"")
        buildConfigField("String", "GITHUB_CLIENTE_SECRET", "\"${project.property("GITHUB_CLIENTE_SECRET")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation(libs.activity)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Nueva versión para evitar errores de Gradle
    implementation ("com.google.guava:guava:33.0.0-android")

    // Botón Google
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Material
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.prolificinteractive:material-calendarview:1.4.3")

    // Progress bar meditacion
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")

    // Api de GitHub
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // JSON
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.google.android.material:material:1.11.0")
}