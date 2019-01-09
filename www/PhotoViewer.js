var exec = require('cordova/exec');

exports.show = function(url, title, options) {
    if (title == undefined) {
        title = '';
    }

    if (typeof options == "undefined") {
        options = {};
    }

    if (options.share === undefined) {
        options.share = false;
    }

    if (options.closeButton === undefined) {
        options.closeButton = true;
    }

    if (options.copyToReference === undefined) {
        options.copyToReference = false;
    }

    if (options.headers === undefined) {
        options.headers = '';
    }

    var piccasoOptions = {
        fit: true,
        centerInside: true,
        centerCrop: false
    };

    if(options.picassoOptions) {
        piccasoOptions = Object.assign(piccasoOptions, options.picassoOptions);
    }

    var args = [url, title, options.share, options.closeButton, options.copyToReference, options.headers, picassoOptions];

    exec(function() {}, function() {}, "PhotoViewer", "show", args);
};
