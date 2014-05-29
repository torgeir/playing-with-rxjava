var cluster = require('cluster');

var argServers = process.argv[2];
var CPUS = require('os').cpus().length;

var PORT = 3000;
var SERVERS = argServers || CPUS;

if (cluster.isMaster) {
  console.log('Spawning master..');

  times(SERVERS, function (n) {
    var data = JSON.stringify({ msg: 'listen', data: PORT + n });
    var worker = cluster.fork();
    worker.on('listening', function (address) {
      if (address.port == 3000) {
        worker.send(data);
      }
    });
  });
}
else if (cluster.isWorker) {
  var worker = require('./app');
  worker(PORT);
}

function times (n, fn) {
  var i = 1;
  while (i <= n) {
    fn(i);
    i += 1;
  }
}
