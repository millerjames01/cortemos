name := "cortemos"

version := "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(
	"com.typesafe.slick" %% "slick" % "2.0.0",
	"org.slf4j" % "slf4j-nop" % "1.6.4",
	"com.h2database" % "h2" % "1.3.167",
	"org.scalatest" % "scalatest_2.10" % "2.2.1" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
