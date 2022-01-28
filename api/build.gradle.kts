plugins {
    id ("java-library")
    id ("kotlin")
    id ("maven-publish")
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.zeroconfig"
                artifactId = "api"
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