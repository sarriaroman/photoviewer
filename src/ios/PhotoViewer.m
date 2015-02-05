/********* PhotoViewer.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>

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
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
