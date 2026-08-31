rootProject.name = "backend"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

include("common")
include("manager")
include("worker")
include("database")
