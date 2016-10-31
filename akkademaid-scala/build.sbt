name := """akkademaid-scala"""

version := "1.0"

scalaVersion := "2.11.8"

// Uncomment to use Akka
libraryDependencies ++= Seq("com.gms.akka" %% "akkademy-scala-db" % "1.0",
	"com.syncthemall" % "boilerpipe" % "1.2.2")

