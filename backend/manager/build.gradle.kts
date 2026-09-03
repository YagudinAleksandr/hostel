plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":database"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.graphql)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.camunda.engine.rest.client)
    implementation(libs.liquibase.core)
    implementation(libs.spring.boot.liquibase)

    runtimeOnly(libs.h2)
    runtimeOnly(libs.postgresql)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.graphql.test)
    testImplementation(libs.spring.security.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.jar {
    enabled = false
}
