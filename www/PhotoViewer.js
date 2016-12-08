var exec = require('cordova/exec');

exports.show = function(url, title, options) {
    if ( title == undefined ) {
        title = '';
    }

    if (typeof options == "undefined") {
        options = {};
    }

    exec(
         function(result) {
            var closed = result && result.closed
            if (closed) {
                if (typeof options.closedCallback === "function") {
                    options.closedCallback();
                }
                return;
            }
         },
         function() {},
         "PhotoViewer",
         "show",
         [url, title, options]
    );
};
