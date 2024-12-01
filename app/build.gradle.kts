plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")

}

android {
    namespace = "org.mdw32.trakki"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.mdw32.trakki"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation(libs.firebase.firestore.ktx)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")
    implementation("androidx.transition:transition:1.4.1")
    implementation("androidx.appcompat:appcompat:1.4.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.material:material:1.12.0")

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore:24.0.1")

    implementation("androidx.cardview:cardview:1.0.0")


    // Lifecycle ViewModel with KTX support
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")

    // Fragment KTX for activityViewModels
    implementation("androidx.fragment:fragment-ktx:1.6.0")


    // Google Sign-In SDK
    implementation("com.google.android.gms:play-services-auth:20.3.0")



    // Retrofit and Gson dependencies for API requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")




}