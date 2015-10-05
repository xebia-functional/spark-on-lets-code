import sbt._

trait Excludes {

  implicit class Exclude(module: ModuleID) {

    def excludingLog4j: ModuleID =
      module excludeAll ExclusionRule("log4j")

    def excludingSlf4j: ModuleID =
      module excludeAll ExclusionRule("org.slf4j")

    def excludingGuava: ModuleID =
      module exclude("com.google.guava", "guava")

    def excludingSpark: ModuleID =
      module
          .excludingGuava
          .exclude("org.apache.spark", s"spark-core_${V.scala}")
          .exclude("org.apache.spark", s"spark-streaming_${V.scala}")
          .exclude("org.apache.spark", s"spark-sql_${V.scala}")
          .exclude("org.apache.spark", s"spark-streaming_${V.scala}")

    def excludingLogback: ModuleID = module
        .exclude("ch.qos.logback", "logback-classic")
        .exclude("ch.qos.logback", "logback-core")

    def excludingAkka: ModuleID = module
        .exclude("com.typesafe.akka", "akka-actor")

    def exclusionsForKafka: ModuleID =
      module
          .excludingLog4j
          .excludingSlf4j
          .exclude("com.sun.jmx", "jmxri")
          .exclude("com.sun.jdmk", "jmxtools")
          .exclude("net.sf.jopt-simple", "jopt-simple")
  }

}