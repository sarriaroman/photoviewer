var exec = require('cordova/exec');

exports.show = function(url) {
    exec(success, error, "PhotoViewer", "show", [url]);
};
