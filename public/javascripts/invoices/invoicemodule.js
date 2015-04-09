
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
            controller: ['$scope', '$modalInstance', '$filter', function($scope, $modalInstance, $filter) {

                $scope.invoice = { customer: {}, invoiceLines: [], date: new Date()};


                $scope.$watch('invoice.date', function(value) {
                    $http.get('/invoices/nextinvoicenumber?query=' + $filter('date')(value, 'yyyy-MM-')).then(function(response) {
                        $scope.invoice.invoiceNumber = response.data;
                    });
                }, true);


                $scope.open = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    $scope.opened = true;
                };


                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.invoice);
                };

                $scope.getCustomer = function($viewValue) {
                    return $http.get('/customers/search?query=' + $viewValue).then(function(response) { return response.data });
                };

                $scope.$watch('customertypeahead', function(value) {
                   if(angular.isObject(value)) {
                       $scope.invoice.customer = angular.copy(value);
                       $scope.$watch('[invoice.customer.address, invoice.customer.vat]', function() {
                           if($scope.invoice.customer.id && !(angular.equals($scope.customertypeahead.address, $scope.invoice.customer.address) && angular.equals($scope.customertypeahead.vat, $scope.invoice.customer.vat))) {
                               delete $scope.invoice.customer.id;
                           }
                       }, true);

                   } else {
                       delete $scope.invoice.customer.id;
                       $scope.invoice.customer.name = value;
                       $scope.$watch('invoice.customer.address', function() { }, true);
                   }
                });

            }]
        }).result.then(function(result) {
                console.log(result)
                $http.put('/invoices', result).then(function(response) {
                    $location.path('/invoices/' + response.data.id);
                });
            });
    }

}]);


invoiceModule.controller('InvoiceDetailController', ['$scope', '$http', '$modal', '$routeParams', function($scope, $http, $modal, $routeParams){

    $scope.invoice = {};

    $http.get('/invoices/' + $routeParams.invoiceId).then(function(result) {
        return $scope.invoice = result.data
    });

    $scope.getProduct = function(viewValue) {
        return $http.get('/products/search?query=' + viewValue).then(function(response) { return response.data });
    };

    $scope.addInvoiceLine = function() {
        $modal.open({
            templateUrl: 'assets/partials/invoices/invoice-line.html',
            resolve: {
                getProduct: function() { return $scope.getProduct },
                invoice: function() { return $scope.invoice }
            },
            controller: ['$scope', '$modalInstance', '$http', 'getProduct', 'invoice', function($scope, $modalInstance, $http, getProduct, invoice) {

                $scope.title = 'Add invoice line';

                $scope.invoice = invoice;

                $scope.getProduct = getProduct;

                $scope.$watch('producttypeahead', function(value) {
                    if(angular.isObject(value)) {
                        $scope.invoiceLine.product = angular.copy(value);
                        $scope.invoiceLine.vat = $scope.invoiceLine.product.vat;
                        $scope.invoiceLine.price = $scope.invoiceLine.product.price;
                    } else {
                        delete $scope.invoiceLine.product.id;
                        $scope.invoiceLine.product.description = value;
                    }
                });

                $scope.invoiceLine = { product: {} };

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.invoiceLine);
                };

            }]
        }).result.then(function(result) {
            $http.put('/invoices/' + $scope.invoice.id + '/invoicelines', result).then(function(response){
                $scope.invoice.invoiceLines.push(response.data);
            });
        });
    };

    $scope.editInvoiceLine = function(invoiceLine) {
        $modal.open({
            templateUrl: 'assets/partials/invoices/invoice-line.html',
            resolve: {
                getProduct: function() { return $scope.getProduct },
                invoice: function() { return $scope.invoice }
            },
            controller: ['$scope', '$modalInstance', '$http', 'getProduct', 'invoice', function($scope, $modalInstance, $http, getProduct, invoice) {

                $scope.title = 'Edit invoice line';

                $scope.invoice = invoice;

                $scope.getProduct = getProduct;

                $scope.$watch('producttypeahead', function(value, oldvalue) {
                    if(value === oldvalue) {
                        return;
                    }
                    if(angular.isObject(value)) {
                        $scope.invoiceLine.product = angular.copy(value);
                        $scope.invoiceLine.price = $scope.invoiceLine.product.price;
                        $scope.invoiceLine.vat = $scope.invoiceLine.product.vat;
                    } else {
                        delete $scope.invoiceLine.product.id;
                        $scope.invoiceLine.product.description = value;
                    }
                });

                $scope.invoiceLine = angular.copy(invoiceLine);
                $scope.producttypeahead = $scope.invoiceLine.product.description;

                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.ok = function() {
                    $modalInstance.close($scope.invoiceLine);
                };

            }]
        }).result.then(function(result) {
                $http.put('/invoices/' + $scope.invoice.id + '/invoicelines', result).then(function(response){
                    $scope.invoice.invoiceLines[$scope.invoice.invoiceLines.indexOf(invoiceLine)] = response.data;

                });
            });
    };

}]);