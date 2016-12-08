var exec = require('cordova/exec');

exports.show = function(url, title, options, successCallback, errorCallback) {
    if( title == undefined ) {
      title = '';
    }

    if(typeof options == "undefined"){
        options = {};
    }

    exec(successCallback, errorCallback, "PhotoViewer", "show", [url, title, options]);
};
