plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.myfloralscanapp"
    compileSdk = 34

    defaultConfig {
<<<<<<< HEAD
=======
        buildConfigField("String", "AWS_ACCESS_KEY", "\"${properties["AWS_ACCESS_KEY"]}\"")
        buildConfigField("String", "AWS_SECRET_KEY", "\"${properties["AWS_SECRET_KEY"]}\"")
        buildConfigField("String", "ACCESS_KEY", "\"${properties["ACCESS_KEY"]}\"")
>>>>>>> 181e3eb (Application Push)
        applicationId = "com.example.myfloralscanapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
<<<<<<< HEAD
=======

>>>>>>> 181e3eb (Application Push)
        }
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
    buildFeatures {
        viewBinding = true
        compose = true
<<<<<<< HEAD
=======
        buildConfig = true
>>>>>>> 181e3eb (Application Push)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

<<<<<<< HEAD
dependencies {


    implementation ("com.amazonaws:aws-android-sdk-s3:2.72.0")
    implementation ("com.amazonaws:aws-android-sdk-mobile-client:2.72.0")
    implementation ("com.amazonaws:aws-android-sdk-core:2.72.0")
    implementation ("com.google.android.material:material:1.2.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
=======

dependencies {
    androidTestImplementation("androidx.test.ext:junit:1.1.3") {}
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") {}
    androidTestImplementation ("androidx.test:runner:1.4.0")
    implementation("com.squareup.picasso:picasso:2.71828") {}
    implementation("com.squareup.okhttp3:okhttp:4.9.0") {}
    implementation("com.google.android.material:material:1.2.0") {}
    implementation("androidx.appcompat:appcompat:1.6.1") {}
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") {}
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2") {}
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") {}
    implementation("androidx.navigation:navigation-fragment:2.7.6") {}
    implementation("androidx.navigation:navigation-ui:2.7.6") {}
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2") {}
    implementation("androidx.activity:activity-compose:1.8.2") {}
    implementation(platform("androidx.compose:compose-bom:2023.03.00")) {}
    implementation("androidx.compose.ui:ui") {}
    implementation("androidx.compose.ui:ui-graphics") {}
    implementation("androidx.compose.ui:ui-tooling-preview") {}
    implementation("androidx.compose.material3:material3") {}

    // AWS SDK unified version
    implementation("com.amazonaws:aws-android-sdk-s3:2.72.0") {}
    implementation("com.amazonaws:aws-android-sdk-mobile-client:2.72.0") {}
    implementation("com.amazonaws:aws-android-sdk-core:2.72.0") {}
    implementation("com.amazonaws:aws-android-sdk-ddb:2.46.0") {}
    implementation("com.amazonaws:aws-android-sdk-ddb-mapper:2.46.0") {}

    // Testing
    testImplementation("junit:junit:4.13.2") {}
    testImplementation("org.mockito:mockito-core:3.11.2") {}
    androidTestImplementation("androidx.test.ext:junit:1.1.3") {}
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") {}
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00")) {}
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") {}
    androidTestImplementation ("androidx.test:runner:1.4.0") {}
    androidTestImplementation ("androidx.test:rules:1.4.0") {}

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling") {}
    debugImplementation("androidx.compose.ui:ui-test-manifest") {}
}
>>>>>>> 181e3eb (Application Push)
