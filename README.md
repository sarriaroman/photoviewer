# Photo Viewer  
> This plugin is intended to show a picture from an URL into a Photo Viewer with zoom features.

## How to Install

Cordova:
```bash
cordova plugin add com-sarriaroman-photoviewer
```

Ionic 2:
```bash
$ ionic cordova plugin add com-sarriaroman-photoviewer
$ npm install --save @ionic-native/photo-viewer
```

### Android
> Out of the box

### iOS
> Out of the box


### API

#### Show an image

```
PhotoViewer.show('http://my_site.com/my_image.jpg', 'Optional Title');
```

You have to pass as third parameter the following options as object.

Options:
* share: boolean - Option is used to hide and show the share option.
* closeBtn: boolean - Option for close button visibility when share false [ONLY FOR iOS]
* copyToReference: boolean - If you need to copy image to reference before show then set it true [ONLY FOR iOS]
* headers: string - HTTP Headers [MUST BE PROVIDED]
* picassoOptions: object - Options for picasso dependency [ONLY FOR ANDROID]

##### Usage

```
var options = {
    share: true, // default is false
    closeButton: false, // default is true
    copyToReference: true, // default is false
    headers: '',  // If this is not provided, an exception will be triggered
    piccasoOptions: { } // If this is not provided, an exception will be triggered
};

PhotoViewer.show('http://my_site.com/my_image.jpg', 'Optional Title', options);
```

### Versions  
(1.0.2) Removed Podfile and the dependency  
(1.1.0)
- Removing project dependencies.  
- Moving to Gradle  
- Adding Square's Picasso as Image Loader  
- New Optional Title
- Share button and title bar
- Automatic close on error.
- Support for content:// Uris from Cordova
- Replaced old namespace
- Published to NPM  

(1.1.1)
- Fix for sharing due to online restriction

(1.1.2)
- Fix issues on iOS
- iOS title not updating

(1.1.3)
- Issue fixes

(1.1.4)
- Base64 Support on Android

(1.1.5)
- Option to hide and show share button on Android

(1.1.7)  
- Fix OOM issues on Android

(1.1.9/10)  
- Fix how image is shown on Android

(1.1.17)  
- Additional options added for iOS
- Fix share issue with SDK version 24 or above on Android

(1.1.9)
- Support for Headers
- Enable or Disable Picasso Options ( Only Android ): fit, centerInside, centerCrop.

(1.1.21)
- Fix Typo in PhotoViewer.js
- Fix Typo in PhotoActivity.java

(1.1.22)
- Ask always for permissions on Android.

(1.2.0)
- Adding TS files from next version to support TypeScript projects

(1.2.1)
- Native fixes on Android platform

(1.2.2)
- PRs: #164 #165 #167

(1.2.3)
- PRs: #179

(1.2.4)
- Removing Source Maps because of errors

(1.2.5) 
- Update Picasso Library to 2.71828
- Fixed Picasso Bug
- Thanks to @TdoubleG