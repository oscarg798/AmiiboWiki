# SETUP

In order to setup the project please follow the next steps 

## Google-Services.json

Create a Firebase Project, then add the  package name of the project as an Android App. Notice that you need to include the package name for all the build types, each package name is added as a separate app in the same firebase project.

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

Create/Edit this file in the **root** of the project, it should look like 

```
sdk.dir=<YourAndroidSDKPath>
keystorePassword=<Your Keystore password>
keystoreAlias=<Your Keystore Alias>
keystorepath=<Keystore path>
googleApiKey=<Google API Key>
gameAPIKey=<Game API Key>
```
### Help 

1. Create a keystore key https://developer.android.com/studio/publish/app-signing
2. Create Google API Key for **YouTube* Player** https://developers.google.com/youtube/android/player/register
3. Create Game API Key https://api-docs.igdb.com/#about

## You are done!!