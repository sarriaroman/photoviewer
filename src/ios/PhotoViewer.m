/********* PhotoViewer.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <FSBasicImage.h>
#import <FSBasicImageSource.h>
#import <FSImageViewerViewController.h>

@interface PhotoViewer : CDVPlugin {
  // Member variables go here.
}

- (void)show:(CDVInvokedUrlCommand*)command;
@end

@implementation PhotoViewer

- (void)show:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* url = [command.arguments objectAtIndex:0];

    if (url != nil && [url length] > 0) {
        FSBasicImage *firstPhoto = [[FSBasicImage alloc] initWithImageURL:[NSURL URLWithString:url]];
        
        FSBasicImageSource *photoSource = [[FSBasicImageSource alloc] initWithImages:@[firstPhoto]];

        FSImageViewerViewController *imageViewController = [[FSImageViewerViewController alloc] initWithImageSource:photoSource];
        UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:imageViewController];
        [self.viewController presentViewController:navigationController animated:YES completion:nil];
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
