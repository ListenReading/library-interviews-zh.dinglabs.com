name := "ChineseCulturalInterviews"

scalaVersion := "2.10.0"

version := "1.0"

libraryDependencies ++= Seq(
  "org.ccil.cowan.tagsoup"        % "tagsoup"        % "1.2.1",
  "net.sf.opencsv"                % "opencsv"        % "2.3",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2"
)

// Test dependencies
libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test"
)