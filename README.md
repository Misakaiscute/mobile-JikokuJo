# JikokuJó, a public transport helper.

## JikokuJó is trusty companion for finding your way around the streets of Budapest, created by Máté Demény and Hunor Horchy as part of their highschool exit masterpiece.

## Using offcial data from the local public transport provider (BKK), the app is capable of:

- ### 🔎 Searching for either line numbers or specific stops around Budapest for a given timeframe.

- ### 🗺️ Displaying the route of any local vehicle on the map.

- ### 📌 Following the line on its way if the given vehicle is equipped with trackers.

- ### ⭐ Storing users favorite trips and notifying them about it them depending on how the user set it up.

- ### ✈️ Sharing routes as links with friend to aid with planning.

## Using what?

### This part of the project is a native android mobile application, written in [Kotlin](https://gradle.org) with the help of the [Jetpack Compose framwork](https://developer.android.com/compose)
### Dependency injection is handled by [Dagger-Hilt](https://dagger.dev/hilt), for network request [Retrofit]() is used, and as for the map, [Mapsforge](https://github.com/mapsforge/mapsforge) displays and [OSM](https://www.openstreetmap.org/about) provides the map data.
### The package manager and build tool for this project is [Gradle](https://gradle.org).

## How to install

### Requirements:

- #### JDK 21 set to the JAVA_HOME environment variable
- #### Minimum Android SDK Version 28
- #### Internet access

### Android Studio:

- #### It's highly recommended to use [Android Studio](https://developer.android.com/studio) for running the app. The installation instuctions will be utilizing Android Studio as well.

### Installation process:

0. #### As the [api](https://github.com/Misakaiscute/backend-JikokuJo) is necessary to use this app, it's highly recommended to set that up first.
1. #### Clone the repository into your selected directory
2. #### Edit the **local.properties.example** file, and rename it to **local.properties**. (set the sdk.dir to your Android SDKs' directory)
3. #### Run the **gradlew.bat build** command from the directory, to build the application.
4. #### Run the application through Android Studio [like this](https://developer.android.com/studio/run).
5. #### 🏁You're ready to start using the app!🏁

## ⚠️Warning⚠️

### This is just one repository of the three, which make up the complete project. This repository doesn't provide any functionality without the [api](https://github.com/Misakaiscute/backend-JikokuJo), and for the full experience please consider checking out the [web app](https://github.com/Misakaiscute/frontend-JikokuJo).