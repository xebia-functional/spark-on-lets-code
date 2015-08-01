import sbt._

object Build extends Build with Settings with Dependencies {

  lazy val root = project.in(file("."))
      .settings(projectSettings)
      .settings(dependencies)
}