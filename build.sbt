name := "scala-tron"

scalaVersion := "2.12.4"

version := "0.1"

scalacOptions += "-Ypartial-unification"
scalacOptions in Test ++= Seq("-Yrangepos")

val defaultSettings = Seq(
  // Test

  // Main
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.25",
  "log4j" % "log4j" % "1.2.17",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "commons-codec" % "commons-codec" % "1.11",
  "com.madgag.spongycastle" % "core" % "1.53.0.0",
  "com.madgag.spongycastle" % "prov" % "1.53.0.0",
  "com.google.guava" % "guava" % "18.0",
  "org.iq80.leveldb" % "leveldb" % "0.10",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "org.apache.commons" % "commons-collections4" % "4.0",
  "com.typesafe" % "config" % "1.3.2",
  "com.google.code.findbugs" % "jsr305" % "3.0.0",
  "com.cedarsoftware" % "java-util" % "1.8.0",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.apache.commons" % "commons-collections4" % "4.0",
  "com.beust" % "jcommander" % "1.72",

  "org.apache.kafka" %% "kafka" % "0.11.0.2",

  // Akka
  "com.typesafe.akka" %% "akka-actor" % "2.5.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.8",

  // Alpakka
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "0.15.1",

  "com.google.inject" % "guice" % "4.1.0",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.typelevel" %% "cats-effect" % "0.5",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.18",
  "commons-io" % "commons-io" % "2.6",

  "org.specs2" %% "specs2-core" % "4.0.2" % "test"
)


lazy val protocol = (project in file("tron-protocol"))
  .settings(
    libraryDependencies ++= defaultSettings,
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    )
  )

lazy val api = (project in file("tron-api"))
  .dependsOn(protocol)

lazy val cli = (project in file("tron-cli"))
    .settings(
      mainClass in Compile := Some("org.tron.cli.App"),
      libraryDependencies ++= defaultSettings
    )
  .dependsOn(protocol)
  .aggregate(protocol)
