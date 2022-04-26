# Kaleyra Android Collaboration Suite

<p align="center">
<img src="img/kaleyra.png" alt="Kaleyra" title="Kaleyra" />
</p>

[![Download](https://badgen.net/maven/v/metadata-url/https/maven.bandyer.com/releases/com/kaleyra/collaboration-suite/maven-metadata.xml?label=maven.bandyer.com/releases) ](https://maven.bandyer.com/index.html#releases/com/kaleyra/collaboration-suite/)

**Kaleyra Collaboration Suite** enables audio/video/chat and more collaboration from any platform and browser!

## Overview

**Kaleyra Collaboration Suite** makes it easy to add video conference and chat communication from mobile apps.

**Even though this sdk encloses strongly the UI/UX, it is fully styleable through default Android style system.**

## Requirements

Supported API level 21+ (Android 5.0 Lollipop).

**Requires compileOptions for Java8 and code desugaring for api < 26**

```java
android{
    compileSdkVersion 31
    
    compileOptions{
        // https://developer.android.com/studio/write/java8-support
        coreLibraryDesugaringEnabled true
        sourceCompatibility JavaVersion.VERSION_1_8 
        targetCompatibility JavaVersion.VERSION_1_8
    }

    // For Kotlin projects
    kotlinOptions{
        jvmTarget="1.8"
    }
}
```

## Documentation

### Introduction

[Home](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Home)

### Integration

1. [Get your credentials](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Get-Your-Credentials)
1. [Get started](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Get-Started)
1. [Terminology](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Terminology)
1. [Android Studio Setup](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Android-Studio-Setup)
1. [Initialize](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Initialize)
1. [Call](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Call)
1. [Join Link](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Join-Link)
1. [Observe Call](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Observe-Call)
1. [Push Notifications](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Push-Notifications)

### More

[Enable Call Sounds & Audio Routing](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Call-Sounds-&-Audio-Routing)

[Logging](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Logging)

[User Description](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/User-Description)

[Proguard](https://github.com/Bandyer/Kaleyra-Android-Collaboration-Suite/wiki/Proguard)

### Customize UI

🎨 &nbsp; [Color System](https://github.com/Bandyer/Bandyer-Android-Design/wiki/Color-System)

🎑 &nbsp; [Customize Colors & Themes](https://github.com/Bandyer/Bandyer-Android-Design/wiki/Customize-Colors-&-Themes)

🆎 &nbsp; [Customize Font](https://github.com/Bandyer/Bandyer-Android-Design/wiki/Customize-Font)

### Code documentation

[Kotlin](https://docs.bandyer.com/Kaleyra-Android-Collaboration-Suite/kDoc/)

## Credits

- [WebRTC](https://webrtc.org/) by Google, Mozilla, Opera, W3C and ITF
- [Kotlin](https://github.com/JetBrains/kotlin) by JetBrains
- [OkHttp](https://github.com/square/okhttp) by square
- [Socket.io](https://github.com/socketio/socket.io-client-java) by socket.io
- [Android-weak-handler](https://github.com/badoo/android-weak-handler) by Badoo
- [Picasso](https://github.com/square/picasso) by square
- [FastAdapter](https://github.com/mikepenz/FastAdapter) by mikepenz
