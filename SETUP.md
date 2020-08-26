# SETUP

In order to setup the project please follow the next steps 

## Google-Services.json

Create a Firebase Project, then add the  package name of the project as an Android App. Notice that you need to include the package name for all the build types, each package name is added as a separate app in the same firebase project.

## Android Studio Version

Currently we are using Android Studio Version 4.2

Then classpatch is using version `4.2.0-alpha08` is you do not want to use this version use `4.0.1`
Also distribution url  for 4.2 is `gradle-6.6-rc-6-bin` but if you do not want change it in `gradle-wrapper.properties`

* Release
```kotlin
com.oscarg798.amiibowiki
```

* Debug
```kotlin
com.oscarg798.amiibowiki.debug
```

* Alpha
```kotlin
com.oscarg798.amiibowiki.alpha
```

Enable crashlytics for the Firebase Project, then download the `google-services.json` file and paste it inside the project in the `app` module.

## Remote Config

The project use remote config as Feature Flag provider, you will need to create the following boolean values and set up a value for them

```kotlin
show_related_games
amiibo_detail
show_game_detail
```

## Local Properties File

The project use a `local.properties` file to setup a Google API Key for **YouTube* Player** and an API Key for the Game API https://api.igdb.com/.

Also the build type `Alpha` creates a minified signed build, and in order to make it work the file `signing.gradle` needs a specification
of a **KeyStore Path**, **KeyStore Alias**, and the **Password** to access to it

We use mixpanel to track some user events, create a project and paste the token as `mixpanelToken`

Create/Edit this file in the **root** of the project, it should look like 

```
sdk.dir=<YourAndroidSDKPath>
keystorePassword=<Your Keystore password>
keystoreAlias=<Your Keystore Alias>
keystorepath=<Keystore path>
googleApiKey=<Google API Key>
gameAPIKey=<Game API Key>
mixpanelToken=<Mixpanel API Key>
```
### Help 

1. Create a keystore key https://developer.android.com/studio/publish/app-signing
2. Create Google API Key for **YouTube* Player** https://developers.google.com/youtube/android/player/register
3. Create Game API Key https://api-docs.igdb.com/#about

## Properties.gradle

In order to automate the apk publishing to the playstore, we are this plugin using https://github.com/Triple-T/gradle-play-publisher, its
we setup the configuration in the `signing.gradle`, and as you see it's using a property `googleAccountServiceFile` that is the json file to
for authentication purposes in the PlayStore. So you can ignore this step commenting/removing the following line from the `build.gradle` file in the `app` module

```groovy
apply from: '../gradlescripts/release.gradle'
```

## CiTask.Gradle.kts

In order to run some tasks on the CI we have create the file `citask.gradle`, it uses some properties that should be
in the `local.properties` file. To make the app work comment/remove  the following line from  the `build.gradle` file in the `app` module

```
apply from: '../gradlescripts/citask.gradle.kts'
```

If you do not want to comment this file and you one to use those tasks add the following properties to `local.properties`

```
firebaseProjectId=[Firebase Project ID]
firebaseToken=[Firebase Auth Token]
```

Where `firebaseProjectId` is the firebase project id that you created on firebase, and  `firebaseToken` is the firebase token
from the `firebase cli` tool. https://firebase.google.com/docs/cli


## You are done!!
