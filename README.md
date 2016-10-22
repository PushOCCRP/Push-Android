# Push-Android
This is the repository for the Android app portion of the Push app ecosystem. Theoretically, you should never have to actually touch this code. The idea is that it is pulled once, and sits in a folder. The generator will then automatically customize and build the code.

# Features
- Offline caching of stories
- Push notification support
- Analytics support
- In-line images in stories
- YouTube player support

# Setup steps

1. Clone this repository ```git clone https://github.com/PushOCCRP/Push-Android```
2. Run the generator https://github.com/PushOCCRP/Push-Generator in offline mode with the ```-o``` flag
3. Open Android Studio. The studio will take some time to build the app up with your local settings.
4. Do a test build, if it breaks, submit a bug report.
