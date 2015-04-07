
var customerModule = angular.module('CustomerModule', []);

customerModule.controller('CustomersController', ['$scope', '$http', '$modal', function($scope, $http, $modal){

    $scope.customers = [];

    $http.get('/customers').then(function(result) {
        return $scope.customers = result.data
    });


    $scope.addCustomer = function() {
        $modal.open({
            templateUrl: 'assets/partials/customers/customermodal.html',
            controller: ['$scope', '$modalInstance', function($scope, $modalInstance) {
                $scope.title = 'Add new customer';
                $scope.customer = {};

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.customer);
                };

            }]
        }).result.then(function(result) {
            console.log(result);
            $http.put('/customers', result).then(function(response) {
                $scope.customers.push(response.data);
            })
        });
    };

    $scope.editCustomer = function(customer) {
        $modal.open({
            templateUrl: 'assets/partials/customers/customermodal.html',
            controller: ['$scope', '$modalInstance', function($scope, $modalInstance) {
                $scope.title = 'Add new customer';
                $scope.customer = angular.copy(customer);

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.customer);
                };

            }]
        }).result.then(function(result) {
                console.log(result);
                $http.put('/customers', result).then(function(response) {
                    $scope.customers[$scope.customers.indexOf(customer)] = response.data;
                })
            });
    };


}]);
