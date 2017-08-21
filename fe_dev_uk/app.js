var http = require('http');
var express = require('express');
var app = express();
var httpServer = http.createServer(app);
var node_env = process.env.node_env || 'development';
app.use(express.static(__dirname + '/app'));


var server = http.createServer(app).listen(process.env.VCAP_APP_PORT || 3000, function () {
    console.log('Express server listening on port %s', process.env.VCAP_APP_PORT);
});

if(node_env === 'development'){
    var open = require('open');
    open('http://localhost:3000/', function (err) {
        if (err) throw err;
    });
}
