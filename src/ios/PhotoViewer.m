/********* PhotoViewer.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <MobileCoreServices/MobileCoreServices.h>

@interface PhotoViewer : CDVPlugin <UIDocumentInteractionControllerDelegate, UIScrollViewDelegate> {
    // Member variables go here.
    Boolean isOpen;
    UIScrollView *fullView;
    UIImageView *imageView;
    UIButton *closeBtn;
    BOOL showCloseBtn;
    BOOL copyToReference;
}

@property (nonatomic, strong) UIDocumentInteractionController *docInteractionController;
@property (nonatomic, strong) NSMutableArray *documentURLs;

- (void)show:(CDVInvokedUrlCommand*)command;
@end




@implementation PhotoViewer

- (void)setupDocumentControllerWithURL:(NSURL *)url andTitle:(NSString *)title
{
    if (self.docInteractionController == nil) {
        self.docInteractionController = [UIDocumentInteractionController interactionControllerWithURL:url];
        self.docInteractionController.name = title;
        self.docInteractionController.delegate = self;
    } else {
        self.docInteractionController.name = title;
        self.docInteractionController.URL = url;
    }
}

- (UIDocumentInteractionController *) setupControllerWithURL: (NSURL*) fileURL
                                               usingDelegate: (id <UIDocumentInteractionControllerDelegate>) interactionDelegate {

    UIDocumentInteractionController *interactionController = [UIDocumentInteractionController interactionControllerWithURL: fileURL];
    interactionController.delegate = interactionDelegate;

    return interactionController;
}

- (UIViewController *) documentInteractionControllerViewControllerForPreview:(UIDocumentInteractionController *) controller {
    isOpen = false;
    return self.viewController;
}

- (void)show:(CDVInvokedUrlCommand*)command
{
    if (isOpen == false) {
        [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
        [[NSNotificationCenter defaultCenter]
         addObserver:self selector:@selector(orientationChanged:)
         name:UIDeviceOrientationDidChangeNotification
         object:[UIDevice currentDevice]];
        isOpen = true;
        UIActivityIndicatorView *activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:self.viewController.view.frame];
        [activityIndicator setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [activityIndicator.layer setBackgroundColor:[[UIColor colorWithWhite:0.0 alpha:0.30] CGColor]];
        CGPoint center = self.viewController.view.center;
        activityIndicator.center = center;
        [self.viewController.view addSubview:activityIndicator];

        [activityIndicator startAnimating];

        CDVPluginResult* pluginResult = nil;
        NSString* url = [command.arguments objectAtIndex:0];
        NSString* title = [command.arguments objectAtIndex:1];
        BOOL isShareEnabled = [[command.arguments objectAtIndex:2] boolValue];
        showCloseBtn = [[command.arguments objectAtIndex:3] boolValue];
        copyToReference = [[command.arguments objectAtIndex:4] boolValue];

        if ([url rangeOfString:@"http"].location != NSNotFound) {
            copyToReference = true;
        }

        if (url != nil && [url length] > 0) {
            [self.commandDelegate runInBackground:^{
                if(isShareEnabled) {
                    self.documentURLs = [NSMutableArray array];
                }

                NSURL *URL = [self localFileURLForImage:url];

                if (URL) {
                    if(isShareEnabled){
                        [self.documentURLs addObject:URL];
                        [self setupDocumentControllerWithURL:URL andTitle:title];
                        double delayInSeconds = 0.1;
                        dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
                        dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
                            [activityIndicator stopAnimating];
                            [self.docInteractionController presentPreviewAnimated:YES];
                            //[self.docInteractionController presentPreviewAnimated:NO];

                        });
                    } else {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self showFullScreen:URL andTitle:title];
                            [activityIndicator stopAnimating];
                        });
                    }

                }
            }];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (NSURL *)localFileURLForImage:(NSString *)image
{
    NSString* webStringURL = [image stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]];
    NSURL* fileURL = [NSURL URLWithString:webStringURL];

    if (copyToReference && ![fileURL isFileReferenceURL]) {
        NSData *data = [NSData dataWithContentsOfURL:fileURL];
        if( data ) {
            // save this image to a temp folder
            NSURL *tmpDirURL = [NSURL fileURLWithPath:NSTemporaryDirectory() isDirectory:YES];
            NSString *filename = [[NSUUID UUID] UUIDString];
            fileURL = [[tmpDirURL URLByAppendingPathComponent:filename] URLByAppendingPathExtension:[self contentTypeForImageData:data]];

            [[NSFileManager defaultManager] createFileAtPath:[fileURL path] contents:data attributes:nil];
        }
    }
    return fileURL;
}

- (NSString *)contentTypeForImageData:(NSData *)data {
    uint8_t c;
    [data getBytes:&c length:1];

    switch (c) {
        case 0xFF:
            return @"jpeg";
        case 0x89:
            return @"png";
        case 0x47:
            return @"gif";
        case 0x42:
            return @"bmp";
        case 0x49:
        case 0x4D:
            return @"tiff";
    }
    return nil;
}


-(UIView *) viewForZoomingInScrollView:(UIScrollView *)inScroll {
    NSArray *subviews = [inScroll subviews];
    return subviews[0];
}

//This will create a temporary image view and animate it to fullscreen
- (void)showFullScreen:(NSURL *)url andTitle:(NSString *)title {

    CGFloat viewWidth = self.viewController.view.bounds.size.width;
    CGFloat viewHeight = self.viewController.view.bounds.size.height;

    //fullView is gloabal, So we can acess any time to remove it
    fullView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, viewWidth, viewHeight)];
    [fullView setBackgroundColor:[UIColor blackColor]];

    // For supporting zoom,
    fullView.minimumZoomScale = 1.0;
    fullView.maximumZoomScale = 3.0;
    fullView.clipsToBounds = YES;
    fullView.delegate = self;

    imageView = [[UIImageView alloc]init];
    [imageView setContentMode:UIViewContentModeScaleAspectFit];
    UIImage *image = [UIImage imageWithContentsOfFile:url.path];
    [imageView setBackgroundColor:[UIColor clearColor]];
    imageView.image = image;
    imageView.contentMode = UIViewContentModeScaleAspectFit;

    [imageView setFrame:CGRectMake(0, 0, viewWidth, viewHeight)];

    [fullView addSubview:imageView];
    fullView.contentSize = imageView.frame.size;

    [self.viewController.view addSubview:fullView];

    if(showCloseBtn) {
        closeBtn = [UIButton buttonWithType:UIButtonTypeSystem];
        [closeBtn setTitle:@"✕" forState:UIControlStateNormal];
        closeBtn.titleLabel.font = [UIFont systemFontOfSize: 32];
        [closeBtn setTitleColor:[UIColor colorWithRed:255/255.0 green:255/255.0 blue:255/255.0 alpha:0.6] forState:UIControlStateNormal];
        [closeBtn setFrame:CGRectMake(0, viewHeight - 50, 50, 50)];
        [closeBtn setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]];
        [closeBtn addTarget:self action:@selector(closeButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        [self.viewController.view addSubview:closeBtn];
    } else {
        UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(fullimagetapped:)];
        singleTap.numberOfTapsRequired = 1;
        singleTap.numberOfTouchesRequired = 1;
        [fullView addGestureRecognizer:singleTap];
        [fullView setUserInteractionEnabled:YES];
    }
}

- (void)fullimagetapped:(UIGestureRecognizer *)gestureRecognizer {
    [self closeImage];
}

- (void)closeButtonPressed:(UIButton *)button {
    [closeBtn removeFromSuperview];
    closeBtn = nil;
    [self closeImage];
}

- (void)closeImage {
    isOpen = false;
    [fullView removeFromSuperview];
    fullView = nil;
}

- (void) orientationChanged:(NSNotification *)note
{
    if(fullView != nil) {
        CGFloat viewWidth = self.viewController.view.bounds.size.width;
        CGFloat viewHeight = self.viewController.view.bounds.size.height;

        [fullView setFrame:CGRectMake(0, 0, viewWidth, viewHeight)];
        [imageView setFrame:CGRectMake(0, 0, viewWidth, viewHeight)];
        fullView.contentSize = imageView.frame.size;
        [closeBtn setFrame:CGRectMake(0, viewHeight - 50, 50, 50)];
    }
}
@end
