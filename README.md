# Exo Player Assignment Application
Formally called Simple player application this is the Android application that to play simple video playback contents.

## Glossary
1. [Getting Started](#getting-started)
2. [Prerequisites](#prerequisites)
3. [Description](#description)
4. [Technical Requirements](#technical requirements)
5. [Dependencies](#dependencies)
6. [Authors](#authors)
7. [Future Improvements](#future improvements)


## Getting Started

Clone repository and open in Android studio, then it will be ready to run
git_repo:https://github.com/Jegadeeswaran250/assignment_exoplayer.git

### Prerequisites

Tech stack currently in use/supported

```
Android Studio Iguana | 2023.2.1
Target API Version 35
Exo player Version 1.5.1
```
### Description
This project majorly deals with the video play back using the basic  **ExoPlayer** implementation.
The player has the basic playback controls(play/pause/forward and backward seek/ progressbar) also the player supports playback of at least two videos that are "stitched" together.
The mid-roll ad insertion has been done by transitioning to an advertisement video at a every 30 secs of the main playback content.

used playback stream 
```
-hls(main content)
-dash(main content)
-mp4(ad content)
```

## Technical Requirements
- Java is used for the implementation.
- Basic error handling is included.
- Smooth transitions between Stream and Ads

### Dependencies
```
implementation("androidx.media3:media3-exoplayer:1.5.1")
implementation("androidx.media3:media3-exoplayer-dash:1.5.1")
implementation("androidx.media3:media3-ui:1.5.1")
implementation("androidx.media3:media3-exoplayer-hls:1.5.1")
```


### Author
* **Jegadeeswaran J**


### Future Improvements
*Thumbnail images can be added for main playback  when the content get seek using the progress bar.
*can do the implementation for DRM Configuration.
*can use ImaAdConfiguration for server-side ad insertion.




