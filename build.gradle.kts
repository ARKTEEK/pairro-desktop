plugins {
  application
  java
  id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
  implementation("org.jmdns:jmdns:3.6.3")
  implementation("org.bouncycastle:bcpkix-jdk18on:1.78.1")
}

javafx {
  version = "17"
  modules("javafx.controls", "javafx.fxml")
}

application {
  mainClass.set("pairro.App")
  applicationName = "Pairro"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.test {
  useJUnitPlatform()
}
