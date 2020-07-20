// @ts-ignore
let exec = require('cordova/exec');

interface IPhotoViewerOptions {
    share?: boolean;
    closeButton?: boolean;
    copyToReference?: boolean;
    headers?: string;
    piccasoOptions?: {
        fit?: boolean,
        centerInside?: boolean,
        centerCrop?: boolean
    };
}

class PhotoViewer {

    public static show(url: string, title: string = '', options: IPhotoViewerOptions = {
        share: false,
        closeButton: true,
        copyToReference: false,
        headers: '',
        piccasoOptions: {
            fit: true,
            centerInside: true,
            centerCrop: false
        }
    }) {
        if (url && url.trim() === '') {
            // Do nothing
            return;
        }

        const args = [url, title, options.share, options.closeButton, options.copyToReference, options.headers, options.piccasoOptions];

        exec(() => { }, () => { }, 'PhotoViewer', 'show', args);
    }
}

// @ts-ignore
module.exports = PhotoViewer;
