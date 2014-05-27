function listen (port) {
  var app  = require('./app')();
  app.listen(port, function () {
    console.log("Listening in %s mode on port %s, pid %s",
                process.env.NODE_ENV,
                port,
                process.pid);
  });
}

listen(3001);
listen(3002);
listen(3003);
listen(3004);

var addresses = [
  { target: 'http://localhost:3001' },
  { target: 'http://localhost:3002' },
  { target: 'http://localhost:3003' },
  { target: 'http://localhost:3004' }
];


var httpProxy = require('http-proxy');
var proxy = httpProxy.createProxyServer({});

var http = require('http');
var server = http.createServer(function (req, res) {
  var target = addresses.shift();
  proxy.web(req, res, target);
  addresses.push(target);
});

proxy.on('error', function (err) {
  console.error(err);
});

server.listen(3000);
