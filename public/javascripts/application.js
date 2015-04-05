invoicing = angular.module('invoicing', ['ngRoute', 'xeditable', 'ui.bootstrap', 'InvoiceModule', 'CustomerModule']);

invoicing.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/invoices', {
            templateUrl: 'assets/partials/invoices/invoice-list.html',
            controller: 'InvoicesController'
        })
        .when('/invoices/:invoiceNb', {
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
        }).
        otherwise({
            redirectTo: '/invoices'
        });
}]);


invoicing.run(function(editableOptions) {
    editableOptions.theme = 'bs3';
});

