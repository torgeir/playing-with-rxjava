var http = require('http');

var stats = {
  ongoingRequests: 0
};

process.on('message', function (msg) {
  var event = JSON.parse(msg);

  switch (event.msg) {
    case 'listen':
      var port = event.data;
      http.createServer(respondWithSse)
          .listen(port, function () {
            console.log('Worker SSE listening on %s', port);

          });
    break;
  }
});

module.exports = function start (port) {

  var server = http.createServer(function (req, res) {
    switch (req.url) {

      case '/':
        // count requests
        stats.ongoingRequests += 1;

        var end = res.end;
        res.end = function () {
          end.apply(res, arguments);
          stats.ongoingRequests -= 1;
        };

        respondWithHtml(req, res);
      break;

      default:
        res.writeHead(404);
        res.end();
      break
    }
  });

  server.listen(port, function () {
    console.log("Worker listening on port %s, pid %s (%s mode)",
                port,
                process.pid,
                process.env.NODE_ENV);
  });
};

function respondWithSse (req, res) {

  if (req.url !== '/stream') {
    res.end();
  }

  res.writeHead(200, {
    'content-type': 'text/event-stream',
    'transfer-encoding': 'chunked',
    'cache-control': 'no-cache',
    'connection': 'keep-alive'
  });

  var timeout;

  (function sendStatusDataLoop () {
    sendSSE('data', stats);
    timeout = setTimeout(sendStatusDataLoop, 1000);
  })();

  req.on('close', function () {
    clearTimeout(timeout);
  });

  function sendSSE(event, data) {
    var sseEvent = event + ": " + JSON.stringify(data) + "\n\n";
    res.write(sseEvent);
  }
}

function respondWithHtml (req, res) {

  setTimeout(function respond () {
    res.end("the industrys standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.");
  }, 1000);
}
