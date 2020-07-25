# AmiiboWiki
[<img src="http://oscarg-teamcity.eu.ngrok.io.ngrok.io/app/rest/builds/aggregated/strob:(buildType:(project:(id:amiibo_wiki)))/statusIcon?guest=guest"/>](http://oscarg-teamcity.eu.ngrok.io.ngrok.io/project.html?projectId=amiibo_wiki&tab=projectOverview)

<img src="https://github.com/oscarg798/AmiiboWiki/blob/master/logo.png"  width="512" height="512"/>

Application to read and watch the Amiibo data.  It has a feature to read the nfc tag associated with it and watch the detail of the scanned Amiibo.

<img src="https://github.com/oscarg798/AmiiboWiki/blob/master/amiibo_demo.gif"  width="200" height="400"/>

## Features

* Amiibo list
* Amiibo filtering
* Amiibo Details
* Scan Amiibos
* Offline support

### Architecture

* Clean + MVI in presentation layer
* Modules per feature + a common module called **Core**
* We have interfaces for the repositories in order to respect the dependencies rule on the usecases,
as they should not be aware of the repositories impl that will be in an upper layer than usecases
* We do not have interfaces for usecases following YAGNI

## Built With

* [Dagger 2.*](https://github.com/google/dagger)
* [Retrofit](https://github.com/square/retrofit)
* [Airbnb DeepLink dispatcher](https://github.com/airbnb/DeepLinkDispatch)
* [ShimmerLayout] (https://github.com/team-supercharge/ShimmerLayout) it's deprecated but it works also is just an UI detail ...
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
