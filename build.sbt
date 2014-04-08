name := "contacts44"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "2.9.3",
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings
