# Photo Viewer  
> This plugin is intended to show a picture from an URL into a Photo Viewer with zoom features.

## How to Install

```bash
cordova plugin add com-sarriaroman-photoviewer
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
