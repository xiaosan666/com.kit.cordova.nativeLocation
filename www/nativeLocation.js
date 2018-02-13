var exec = require('cordova/exec');

exports.getLocation = function (success, error) {
  exec(success, error, 'nativeLocation', 'getLocation');
};

// exports.coolMethod = function (arg0, success, error) {
//   exec(success, error, 'nativeLocation', 'coolMethod', [arg0]);
// };


