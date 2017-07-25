angular.module('product', [])
  .controller('home', function($http) {
  var self = this;
  $http.get('/api/v1/products/').success(function(data) {
    self.product = data;
  })
});