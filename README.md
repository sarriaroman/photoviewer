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

Optionally you can pass third parameter option as object.

Options:
* share: Option is used to hide and show the share option.
* closeBtn: Option for close button visibility when share false [ONLY FOR iOS]
* copyToReference: If you need to copy image to reference before show then set it true [ONLY FOR iOS]

##### Usage

```
var options = {
    share: true, // default is false
    closeButton: false, // default is true
    copyToReference: true // default is false
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
