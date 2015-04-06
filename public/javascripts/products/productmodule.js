

var productModule = angular.module('ProductModule', []);


productModule.controller('ProductsController', ['$scope', function($scope) {

    $scope.products = [createProduct(),createProduct(),createProduct()];


    function createProduct() {
        return {
            id: 1,
            description: 'Example product',
            price: 22.0,
            vat: 21
        }

    }

}]);