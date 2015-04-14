var securityModule = angular.module('SecurityModule', []);


securityModule.controller('LoginController', ['$scope', '$http', '$location', '$rootScope', function($scope, $http, $location, $rootScope) {

    $scope.login = {};

    $scope.doLogin = function() {
        $http.post('/login', $scope.login).then(function(response) {
            $rootScope.isLoggedIn = true;
            $rootScope.username = response.data;
            $location.path('/invoices');

        })
    }

}]);