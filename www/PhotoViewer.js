"use strict";
// @ts-ignore
var exec = require('cordova/exec');
var PhotoViewer = /** @class */ (function () {
    function PhotoViewer() {
    }
    PhotoViewer.show = function (url, title, options) {
        if (title === void 0) { title = ''; }
        if (options === void 0) { options = {
            share: false,
            closeButton: true,
            copyToReference: false,
            headers: '',
            piccasoOptions: {
                fit: true,
                centerInside: true,
                centerCrop: false
            }
        }; }
        if (url && url.trim() == '') {
            // Do nothing
            return;
        }
        var args = [url, title, options.share, options.closeButton, options.copyToReference, options.headers, options.piccasoOptions];
        exec(function () { }, function () { }, "PhotoViewer", "show", args);
    };
    return PhotoViewer;
}());
// @ts-ignore
module.exports = PhotoViewer;
