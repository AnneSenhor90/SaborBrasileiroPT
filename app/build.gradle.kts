plugins {
    alias(libs.plugins.android.application)
        //Firebase
        //id("com.android.application")
        // Add the Google services Gradle plugin
        id("com.google.gms.google-services")
    }

android {
    namespace = "pt.saborbrasileiro.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "pt.saborbrasileiro.app"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Dependências base do Android (Mantidas as suas referências do libs)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Navegação entre Telas (Jetpack Navigation Component)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // --- CONFIGURAÇÃO DO FIREBASE ---
    // Importa a BoM mais recente que definiu (v34.13.0)
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))

    // Serviços do Firebase atualizados e sem versões manuais (geridos pela BoM)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Login com conta Google
    implementation("com.google.android.gms:play-services-auth:21.4.0")

    // Carregamento de imagens dos restaurantes por URL
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
