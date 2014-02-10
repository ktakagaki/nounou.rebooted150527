//Using IntelliJ Project for now

organization := "de.lin-magdeburg"

name := "nounous"

scalaVersion := "2.10.3"

//addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full)
//
publishMavenStyle := true

libraryDependencies ++= Seq(
  "org.scalanlp" %% "breeze" % "0.6-SNAPSHOT" % "compile",
  "org.scalanlp" %% "breeze-macros" % "0.2-SNAPSHOT" % "compile",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "org.scalafx" % "scalafx_2.10" % "1.0.0-M7"
  )

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

resolvers ++= Seq(
//  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
//  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/" ,
  //"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases")
    )
