

var productModule = angular.module('ProductModule', []);


productModule.controller('ProductsController', ['$scope', '$http', '$modal', function($scope, $http, $modal) {

    //$scope.products = [createProduct(),createProduct(),createProduct()];
    $scope.products = [];

    $http.get('/products').then(function(result) {
        return $scope.products = result.data
    });

    $scope.addProduct = function() {
        $modal.open({
            templateUrl: 'assets/partials/products/productmodal.html',
            controller: ['$scope', '$modalInstance', function($scope, $modalInstance) {

                $scope.product = {};

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.product);
                };

            }]
        }).result.then(function(result) {
            console.log(result);
            $http.put('/products', result).then(function(response) {
                $scope.products.push(response.data);
            })
        });
    };

    $scope.editProduct = function(product) {
        $modal.open({
            templateUrl: 'assets/partials/products/productmodal.html',
            controller: ['$scope', '$modalInstance', function($scope, $modalInstance) {

                $scope.product = angular.copy(product);

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.product);
                };

            }]
        }).result.then(function(result) {
                console.log(result);
                $http.put('/products', result).then(function(response) {
                    $scope.products[$scope.products.indexOf(product)] = response.data;
                })
            });
    };



}]);