# Push-Android
This is the repository for the Android app portion of the Push app ecosystem. Theoretically, you should never have to actually touch this code. The idea is that it is pulled once, and sits in a folder. The generator will then automatically customize and build the code.

# Features
- Offline caching of stories
- Push notification support
- Analytics support
- In-line images in stories
- YouTube player support

# Requirements

- \*nix based system (tested on MacOS 10.13 but should work similarly on Linux.)
- Git [Installation instructions here](https://git-scm.com/book/en/v1/Getting-Started-Installing-Git)
- Android Studio [found here](https://developer.android.com/studio/index.html)
- The [Push Backend](https://github.com/PushOCCRP/Push-Backend) must be set up before you start here. These instructions are pretty short since most of the work is done by that part of the project.

*Note:* This project with _not_ work without the generator. The generator adds in specifically needed and customized files, without them everything will break.

# Setup steps

1. Clone this repository ```git clone https://github.com/PushOCCRP/Push-Android```
2. Create the app in the [Google Play Developer Console](https://play.google.com/apps/publish)
3. Create the app in the [Firebase Conosle](https://console.firebase.google.com/?pli=1)
4. After choosing your new app, look to the left side of the conosle, click the "notifications" menu item.
5. Enter the app id and name (make sure it's the same as in the push generator configuration file)
6. A file called ```google-services.json``` should automatically download.
7. Copy the ```google-services.json``` file to the ```/google-services/``` folder in the generator.
8. Run the generator https://github.com/PushOCCRP/Push-Generator in offline mode with the ```-o``` flag
9. Open Android Studio. The studio will take some time to build the app up with your local settings.
10. Do a test build, if it breaks, submit a bug report.
