# SETUP

In order to setup the project please follow the next steps 

## Google-Services.json
We provide a fake implementation of `google-service.json` file, but you can provide your own. 

Create a Firebase Project, then add the  package name of the project as an Android App. 
Notice that you need to include the package name for all the flavours, each package name is added as a separate app in the same firebase project
or you can remove the ones you do not want to use from `app.gradle`


## Android Studio Version

Currently we are using Android Studio Version [Artic Fox or Canary version](https://developer.android.com/studio/preview) this because
is the version supporting [Compose](https://developer.android.com/jetpack/compose)

## Remote Config

The project use remote config as Feature Flag provider, you will need to create the following boolean values and set up a value for them

```kotlin
show_related_games
amiibo_detail
show_game_detail
update_flexible
update_immediate
```

## Local Properties File

Run `chmod +x ./createLocalProperties.sh && ./createLocalProperties.sh`

This will create the file for you, it should look like 

```
sdk.dir=<YourAndroidSDKPath>
keystorePassword=<Your Keystore password>
keystoreAlias=<Your Keystore Alias>
keystorepath=<Keystore path>
googleApiKey=<Google API Key>
mixpanelToken=<Mixpanel API Key>
gameAPIClientId=<Twitch API Key>
googleAccountServiceFile=<Path to a google account service file>
gameAuthUrl=https://us-central1-babyrecord-336ca.cloudfunctions.net/
firebaseToken = <Firebase cli token to publish to firebase distribution>
```
you will need to provide the following API keys

* [IGDB developer API Key](https://api-docs.igdb.com/#about) -> `gameAPIClientId`
* [MixPanel Token](https://mixpanel.com/) -> mixpanelToken
* [Youtube/google console api key](https://developers.google.com/youtube/registering_an_application) -> googleApiKey

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

## You are done!!
