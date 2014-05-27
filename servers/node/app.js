var http = require('http');

module.exports = function createApp () {

  var stats = {
    ongoingRequests: 0
  };

  var server = http.createServer(function (req, res) {

    switch (req.url) {

      case '/stream':
        respondWithSSE(req, res);
      break;

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

  return server;


  function respondWithSSE (req, res) {

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
};
