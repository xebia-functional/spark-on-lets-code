import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._
import sbtdocker.DockerPlugin.autoImport._

trait SettingsDocker {
  this: Build =>

  lazy val dockerSettings = Seq(
    docker <<= docker dependsOn assembly,
    imageNames in docker := Seq(ImageName("47deg/aurora")),
    dockerfile in docker := {
      val workingDir = s"/opt/aurora"
      val artifact = (assemblyOutputPath in assembly).value

      val artifactTargetPath = s"/opt/aurora/${artifact.name}"
      val sparkPath = "/usr/local/spark/assembly/target/scala-2.11/spark-assembly-1.5.1-hadoop2.4.0.jar"

      val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
      val classpathString = s"$sparkPath:$artifactTargetPath"

      new Dockerfile {
        // Base image
        from("47deg/spark:1.5.1")
        // Mantainer
        maintainer("47 Degrees", "juanpedro.m@47deg.com>")

        // Set working directory
        workDir(workingDir)

        // Add the JAR file
        add(artifact, artifactTargetPath)

        cmdRaw(s"java " +
            s"-verbose:gc " +
            s"-XX:+PrintGCDetails " +
            s"-XX:+PrintGCTimeStamps " +
            s"-Xmx2G " +
            s"-XX:MaxPermSize=1G -cp $classpathString $mainclass")
      }
    }
  )
}