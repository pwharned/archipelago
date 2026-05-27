import org.scalajs.linker.interface.ModuleSplitStyle
val scala3Version     = "3.8.3"
val http4sVersion     = "0.23.32"
val catsEffectVersion = "3.7.0"
val fs2Version        = "3.13.0"
val laminarVersion    = "17.2.1"
val jsoniterVersion   = "2.38.10"
val scalajsDomVersion = "2.8.0"
// shared deps cross-compiled for both JVM and JS
lazy val sharedDependencies = Def.setting(Seq(
  "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core"   % jsoniterVersion,
  "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-macros" % jsoniterVersion
))
// ── shared ──────────────────────────────────────────────────────────────────
lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
    name         := "archipelago-shared",
    scalaVersion := scala3Version,
    libraryDependencies ++= sharedDependencies.value
  )
lazy val sharedJVM = shared.jvm
lazy val sharedJS  = shared.js
// ── backend ─────────────────────────────────────────────────────────────────
lazy val backend = project
  .in(file("backend"))
  .dependsOn(sharedJVM)
  .enablePlugins(RevolverPlugin)
  .settings(
    name         := "archipelago-backend",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "co.fs2"     %% "fs2-core"            % fs2Version,
    ),
    reStart / mainClass := Some(
      "com.github.pwharned.archipelago.backend.Main"
    ),
reStart / baseDirectory := (ThisBuild / baseDirectory).value,

    Compile / resourceGenerators += Def.task {
      val _ = (frontend / Compile / fullLinkJS).value
      val jsDir =
        (frontend / Compile / fullLinkJS / scalaJSLinkerOutputDirectory).value
      val resourceDir =
        (Compile / resourceManaged).value / "static" / "js"
      IO.createDirectory(resourceDir)
      IO.copyDirectory(jsDir, resourceDir)
      IO.listFiles(resourceDir).toSeq
    }.taskValue
  )
// ── frontend ─────────────────────────────────────────────────────────────────
lazy val frontend = project
  .in(file("frontend"))
  .dependsOn(sharedJS)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name         := "archipelago-frontend",
    scalaVersion := scala3Version,
    scalaJSUseMainModuleInitializer := false,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
       .withModuleSplitStyle(
         ModuleSplitStyle.SmallModulesFor(
           List("com.github.pwharned.archipelago.frontend.islands")
         )
       )
    },
    libraryDependencies ++= Seq(
      "com.raquo"    %%% "laminar"     % laminarVersion,
      "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    ) ++ sharedDependencies.value,
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory :=
      (ThisBuild / baseDirectory).value / "static" / "js",
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory :=
      (ThisBuild / baseDirectory).value / "static" / "js"
  )
// ── root ─────────────────────────────────────────────────────────────────────
lazy val root = project
  .in(file("."))
  .aggregate(sharedJVM, sharedJS, backend, frontend)
  .settings(
    name         := "archipelago",
    scalaVersion := scala3Version
  )

