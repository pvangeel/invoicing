
var invoiceModule = angular.module('InvoiceModule', []);

invoiceModule.controller('InvoicesController', ['$scope', '$http', '$location', '$modal', function($scope, $http, $location, $modal){

    $scope.invoices = [];

    $http.get('/invoices').then(function(result) {
        return $scope.invoices = result.data
    });

    $scope.getDetail = function(invoice) {
        $location.path('/invoices/' + invoice.id);
    };

    $scope.createInvoice = function() {
        $modal.open({
            templateUrl: 'assets/partials/invoices/create-invoice-modal.html',
            controller: ['$scope', '$modalInstance', function($scope, $modalInstance) {

                $scope.customer = {};

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.customer);
                };

            }]
        }).result.then(function(result) {

                $location.path('/invoices/' + 123);
            });
    }

}]);


invoiceModule.controller('InvoiceDetailController', ['$scope', '$modal', '$routeParams', function($scope, $modal, $routeParams){

    $scope.invoiceId = $routeParams.invoiceId;

    $scope.invoice = {
        invoiceNumber: '2013-01',
        customer: createCustomer(),
        invoiceLines: [createInvoiceLine(),createInvoiceLine(),createInvoiceLine()]
    };

    $scope.getProduct = function(viewValue) {
        var newVar = [createProduct('Product one'), createProduct('Product two'), createProduct('Product three')];
        console.log(newVar);
        return newVar;
    };

    $scope.addInvoiceLine = function() {
        $modal.open({
            templateUrl: 'assets/partials/invoices/invoice-line.html',
            resolve: { getProduct: function() { return $scope.getProduct }},
            controller: ['$scope', '$modalInstance', 'getProduct', function($scope, $modalInstance, getProduct) {

                $scope.title = 'Add invoice line';

                $scope.getProduct = getProduct;

                $scope.invoiceLine = {};

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.invoiceLine);
                };

            }]
        }).result.then(function(result) {
                console.log(result);
            $scope.invoice.invoiceLines.push(result);
        });
    };

    $scope.editInvoiceLine = function(invoiceLine) {
        $modal.open({
            templateUrl: 'assets/partials/invoices/invoice-line.html',
            resolve: { getProduct: function() { return $scope.getProduct }},
            controller: ['$scope', '$modalInstance', 'getProduct', function($scope, $modalInstance, getProduct) {

                $scope.title = 'Edit invoice line';

                $scope.getProduct = getProduct;

                $scope.invoiceLine = angular.copy(invoiceLine);

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.invoiceLine);
                };

            }]
        }).result.then(function(result) {
                console.log(result);
                $scope.invoice.invoiceLines[$scope.invoice.invoiceLines.indexOf(invoiceLine)] = result;
            });
    };

    function createCustomer() {
        return {
            name: 'Ntrinsic Consulting SPRL',
            vat: 'BE1234567890',
            address: {
                street: 'Boulevard Brand Whitlock',
                number: '114',
                postalCode: '1200',
                city: 'Brussels'
            }
        }


    }

    function createProduct(description) {
        return {id: 123, description: description, price: 22, vat: 21}
    }

    function createInvoiceLine() {
        return {
            product: {
                id: 1,
                description: 'example product description',
                price: 22.00
            },
            productPrice: 40.50,
            amount: 2,
            vat: 21,
            total: 200
        }
    }



}]);