name := "cortemos"

version := "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(
	"com.typesafe.slick" %% "slick" % "2.0.0",
	"org.slf4j" % "slf4j-nop" % "1.6.4"
)

resolvers += "linter" at "http://hairyfotr.github.io/linteRepo/releases"

addCompilerPlugin("com.foursquare.lint" %% "linter" % "0.1-SNAPSHOT")

lazy val root = (project in file(".")).enablePlugins(PlayScala)
