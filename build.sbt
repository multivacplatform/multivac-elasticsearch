name := "multivac-elasticsearch"

version := "0.2"

scalaVersion := "2.11.12"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

resolvers ++= Seq(
  "Maven Central" at "https://repo1.maven.org/maven2/",
  Resolver.sonatypeRepo("public"))

libraryDependencies ++= {
  val sparkVer = "2.4.0"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer % "provided" withSources (),
    "org.apache.spark" %% "spark-sql" % sparkVer,
    "org.apache.spark" %% "spark-streaming" % sparkVer % "provided" withSources (),
    "org.apache.spark" %% "spark-mllib" % sparkVer % "provided" withSources (),
    "org.apache.spark" %% "spark-hive" % sparkVer,
    "org.apache.spark" %% "spark-graphx" % sparkVer % "provided" withSources (),
    "org.apache.spark" %% "spark-yarn" % sparkVer % "provided" withSources (),
    "com.typesafe" % "config" % "1.4.2",
    "org.elasticsearch" %% "elasticsearch-spark-20" % "7.17.2")
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter { j =>
    {
      j.data.getName.startsWith("spark-core") ||
      j.data.getName.startsWith("spark-sql") ||
      j.data.getName.startsWith("spark-hive") ||
      j.data.getName.startsWith("spark-mllib") ||
      j.data.getName.startsWith("spark-graphx") ||
      j.data.getName.startsWith("spark-yarn") ||
      j.data.getName.startsWith("spark-streaming") ||
      j.data.getName.startsWith("hadoop")
    }
  }
}
