# AmiiboWiki

![Acceptance Tests Quality](https://github.com/oscarg798/AmiiboWiki/workflows/Acceptance%20Tests%20Quality/badge.svg)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

</br>

<img src="./logo.png"  width="312" height="312"/>

<a href='https://play.google.com/store/apps/details?id=com.oscarg798.amiibowiki&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' height="100" src='https://play.google.com/intl/en-419/badges/static/images/badges/en_badge_web_generic.png'/></a>

Application to read and watch the Amiibo data, find games you would like to play and much more.
It has a feature to read the nfc tag associated with it and watch the detail of the scanned Amiibo.

<img src="./demo.gif"  width="200" height="400"/>

## Setup

In order to run the project please follow [Our Setup Guide](./SETUP.md)

## Features

* Amiibo list
* Amiibo filtering
* Amiibo Details
* Amiibo Related Games
* Game Details and Trailer
* Scan Amiibos
* Offline mode

### Architecture

* Clean + MVI in presentation layer
* Modules per feature + a common module called **Core**
* We have interfaces for the repositories in order to respect the dependencies rule on the usecases,
as they should not be aware of the repositories impl that will be in an upper layer than usecases
* We do not have interfaces for usecases and some components following YAGNI

## Built With

* Kotlin
* [Compose](https://developer.android.com/jetpack/compose)
* [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
* [Navigation Component] (https://developer.android.com/guide/navigation)
* [Retrofit](https://github.com/square/retrofit)
* [Airbnb DeepLink dispatcher](https://github.com/airbnb/DeepLinkDispatch)
* [Lottie](https://lottiefiles.com/)
* Espresso
* [Mockk](https://mockk.io/)

## License & Acknowledgments

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

Coins made by FreePick from www.flaticon.com

Coins made by FreePick from www.flaticon.com

Pawn made by Nikita Golubev from www.flaticon.com

pokeball made by Those Icons from www.flaticon.com

Mushroom made by Those Icons from www.flaticon.com

Psyduck made by Those Icons from www.flaticon.com

Amiibo is a Nintendo trademark and all the rights belongs to them

The font used by the project is https://fonts.google.com/specimen/Rubik

ont used by the project is https://fonts.google.com/specimen/Rubik
