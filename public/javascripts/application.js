invoicing = angular.module('invoicing', ['ngRoute', 'xeditable', 'ui.bootstrap', 'InvoiceModule', 'CustomerModule', 'ProductModule', 'SecurityModule']);

invoicing.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {

    $httpProvider.responseInterceptors.push('httpInterceptor');

    $routeProvider
        .when('/login', {
            templateUrl: 'assets/partials/security/login.html',
            controller: 'LoginController'
        })
        .when('/invoices', {
            templateUrl: 'assets/partials/invoices/invoice-list.html',
            controller: 'InvoicesController'
        })
        .when('/invoices/:invoiceId', {
            templateUrl: 'assets/partials/invoices/invoice-detail.html',
            controller: 'InvoiceDetailController'
        })
        .when('/customers', {
            templateUrl: 'assets/partials/customers/customer-list.html',
            controller: 'CustomersController'
        })
        .when('/customers/:customerId', {
            templateUrl: 'assets/partials/customers/customer-detail.html',
            controller: 'CustomerDetailController'
        })
        .when('/products', {
            templateUrl: 'assets/partials/products/product-list.html',
            controller: 'ProductsController'
        })
        .otherwise({
            redirectTo: '/invoices'
        });
}]);

invoicing.factory('httpInterceptor', function httpInterceptor ($q, $location) {
    return function (promise) {
        var success = function (response) {
            return response;
        };

        var error = function (response) {
            if (response.status === 401) {
                $location.url('/login');
            }

            return $q.reject(response);
        };

        return promise.then(success, error);
    };
});


invoicing.run(function(editableOptions) {
    editableOptions.theme = 'bs3';
});

