organization := "de.lin-magdeburg"

name := "nounous"

scalaVersion := "2.10.3"

//addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full)
//
publishMavenStyle := true

libraryDependencies ++= Seq(
  "org.scalanlp" %% "breeze" % "0.5.2" % "compile",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "org.scalafx" % "scalafx_2.10" % "1.0.0-M6"
  )

//unmanagedJars in Compile += Attributed.blank(
//    file(scala.util.Properties.javaHome) / "lib" / "jfxrt.jar")
//
resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases")
    )
