plugins {
    id ("java-library")
    id ("kotlin")
    id ("kotlin-kapt")
    id ("maven-publish")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.zeroconfig"
                artifactId = "processor"
                version = "1.0"

                from(components["java"])
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly("com.google.auto.service:auto-service:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")
    implementation("com.squareup:kotlinpoet:1.10.2")

    implementation(project(":api"))
}