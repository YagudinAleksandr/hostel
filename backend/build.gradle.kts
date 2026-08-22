plugins {
    java
}

allprojects {
    group = "uz.backend"
    version = "0.0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks.withType<JavaCompile> {
        // имена параметров в байткоде: нужны для биндинга record-DTO
        // в @ConfigurationProperties и в аргументах контроллеров
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
