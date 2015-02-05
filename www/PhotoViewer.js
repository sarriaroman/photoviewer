var exec = require('cordova/exec');

exports.show = function(url) {
    exec(function(){}, function(){}, "PhotoViewer", "show", [url]);
};
