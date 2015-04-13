var securityModule = angular.module('SecurityModule', []);


securityModule.controller('LoginController', ['$scope', '$http', '$location', function($scope, $http, $location) {

    $scope.login = {};

    $scope.doLogin = function() {
        $http.post('/login', $scope.login).then(function(response) {
            $location.path('/invoices');

        })
    }

}]);