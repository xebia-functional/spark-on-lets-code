import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._
import spray.revolver.RevolverPlugin.Revolver.{settings => revolverSettings}
import sbtassembly.AssemblyPlugin._
import sbtassembly.MergeStrategy._

trait Settings {
  this: Build =>

  lazy val projectSettings: Seq[Def.Setting[_]] = assemblySettings ++ revolverSettings ++ 
  Seq(
    scalaVersion := V.scala,
    scalaVersion in ThisBuild := V.scala,
    organization := "com.fortysevendeg",
    organizationName := "47 Degrees",
    organizationHomepage := Some(new URL("http://47deg.com")),
    version := V.buildVersion,
    conflictWarning := ConflictWarning.disable,
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),
    javaOptions in Test ++= Seq("-XX:MaxPermSize=128m", "-Xms512m", "-Xmx512m"),
    sbt.Keys.fork in Test := true,
    publishMavenStyle := true,
    publishArtifact in(Test, packageSrc) := true,
    publishArtifact in(Test, packageBin) := false,
    logLevel := Level.Info,
    resolvers ++= Seq(
      Resolver.mavenLocal,
      Resolver.defaultLocal,
      Classpaths.typesafeReleases,
      DefaultMavenRepository,
      Resolver.typesafeIvyRepo("snapshots"),
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      "Sonatype staging" at "http://oss.sonatype.org/content/repositories/staging",
      "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
      "Twitter Repository" at "http://maven.twttr.com",
      Resolver.bintrayRepo("websudos", "oss-releases")
    ),
    doc in Compile <<= target.map(_ / "none"),
    unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "src/main/scala"),
    assemblyJarName in assembly := "sparkon-1.0.0.jar",
    assembleArtifact in assemblyPackageScala := true,
    Keys.test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case "application.conf" => concat
      case "reference.conf" => concat
      case "unwanted.txt" => discard
      case entry =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        val mergeStrategy = oldStrategy(entry)
        mergeStrategy == deduplicate match {
          case true => first
          case _ => mergeStrategy
        }
    }
  )
}