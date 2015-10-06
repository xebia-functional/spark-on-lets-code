import sbt._
import sbtdocker.DockerPlugin

object Build extends Build with Settings with SettingsDocker with Dependencies {

  lazy val root = project
    .in(file("."))
    .aggregate(common, persistence, services, api, test)

  lazy val common = project
    .in(file("modules/common"))
    .settings(projectSettings ++ commonDeps)

  lazy val persistence = project
      .in(file("modules/persistence"))
      .dependsOn(common % "test->test;compile->compile")
      .settings(projectSettings ++ persistenceDeps)

  lazy val services = project.in(file("modules/services"))
      .dependsOn(
        common % "test->test;compile->compile",
        persistence)
      .settings(projectSettings ++ servicesDeps)

  lazy val api = project.in(file("modules/api"))
      .enablePlugins(DockerPlugin)
      .dependsOn(
        common % "test->test;compile->compile",
        services)
      .settings(apiSettings ++ apiDeps)

  lazy val test = project.in(file("modules/test"))
      .dependsOn(
        common % "test->test;compile->compile",
        persistence % "test->test;compile->compile",
        services % "test->test;compile->compile",
        api % "test->test;compile->compile")
      .settings(projectSettings ++ testDeps)
}
