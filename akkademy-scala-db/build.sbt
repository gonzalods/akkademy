name := """akkademy-scala-db"""
organization := "com.gms.akka"
version := "1.0"

scalaVersion := "2.11.8"

// Change this to another test framework if you prefer
//libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

// Uncomment to use Akka
libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.4.11",
	"com.typesafe.akka" %% "akka-remote" % "2.4.11",
	"com.typesafe.akka" %% "akka-testkit" % "2.4.11" % "test",
	"org.scalatest" %% "scalatest" % "2.2.6" % "test",
	"com.gms.akka" %% "akkademy-scala-message" % "1.0")


scalacOptions += "-feature"
fork in run := true