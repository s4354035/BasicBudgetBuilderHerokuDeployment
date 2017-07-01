angular.module('mainApp', [ 'ui.router', 'ngTouch', 'ui.grid', 'ui.grid.pagination', 'ui.grid.selection', 'ui.grid.exporter',"chart.js",'ui.bootstrap','dialogs.main','pascalprecht.translate','dialogs.default-translations'])
    .config(function($stateProvider,  $httpProvider, $translateProvider) {
        $translateProvider.useSanitizeValueStrategy('sanitize');
        // uses mobile detect to determine whether it is mobile or desktop
        var md = new MobileDetect(window.navigator.userAgent);
        if(md.mobile() || md.phone()){
            // uses ui.router for paging as it allows layered pages
            var baseState = {
                name: 'base',
                templateUrl: '/mobile/base.html'
            };
            var loginState = {
                name: 'login',
                controller : 'loginController',
                templateUrl: '/mobile/login.html'
            };
            var homeState = {
                name: 'home',
                controller : 'homeController',
                templateUrl: '/mobile/home.html'
            };
            var registerState = {
                name: "register",
                controller : 'registrationController',
                templateUrl: '/mobile/register1.html'
            };
            var forgotPasswordState = {
                name: "forgot_password",
                controller : 'forgotPasswordController',
                templateUrl: '/mobile/forgot_password.html'
            };
            var budgetState = {
                name: "home.budget",
                controller : 'budgetController',
                templateUrl: '/mobile/budget1.html'
            };
            var debitState = {
                name: "home.debit",
                controller : 'debitController',
                templateUrl: '/mobile/debit1.html'
            };
            var autoDebitState = {
                name: "home.autoDebit",
                controller : 'autoDebitController',
                templateUrl: '/mobile/auto_debit.html'
            };
            var statisticsState = {
                name: "home.statistics",
                controller : 'statisticsController',
                templateUrl: '/mobile/statistics1.html'
            };
            var searchState = {
                name: "home.search",
                controller : 'searchResultController',
                templateUrl: '/mobile/fullTextSearch1.html'
            };
            var budgetEditState = {
                name: "createEditBudget",
                controller : 'createEditBudgetController',
                templateUrl: '/mobile/create_edit_budget.html'
            };
            var debitEditState = {
                name: "createEditDebit",
                controller : 'createEditDebitController',
                templateUrl: '/mobile/create_edit_debit.html'
            };
            var autoDebitEditState = {
                name: "createEditAutoDebit",
                controller : 'createEditAutoDebitController',
                templateUrl: '/mobile/create_edit_auto_debit.html'
            };
        }
        else {
            var baseState = {
                name: 'base',
                templateUrl: '/desktop/base.html'
            };
            var loginState = {
                name: 'login',
                controller: 'loginController',
                templateUrl: '/desktop/login.html'
            };
            var homeState = {
                name: 'home',
                controller: 'homeController',
                templateUrl: '/desktop/home.html'
            };
            var registerState = {
                name: "register",
                controller: 'registrationController',
                templateUrl: '/desktop/register1.html'
            };
            var forgotPasswordState = {
                name: "forgot_password",
                controller: 'forgotPasswordController',
                templateUrl: '/desktop/forgot_password.html'
            };
            var budgetState = {
                name: "home.budget",
                controller: 'budgetController',
                templateUrl: '/desktop/budget1.html'
            };
            var debitState = {
                name: "home.debit",
                controller: 'debitController',
                templateUrl: '/desktop/debit1.html'
            };
            var autoDebitState = {
                name: "home.autoDebit",
                controller: 'autoDebitController',
                templateUrl: '/desktop/auto_debit.html'
            };
            var statisticsState = {
                name: "home.statistics",
                controller: 'statisticsController',
                templateUrl: '/desktop/statistics1.html'
            };
            var searchState = {
                name: "home.search",
                controller: 'searchResultController',
                templateUrl: '/desktop/fullTextSearch1.html'
            };
            var budgetEditState = {
                name: "createEditBudget",
                controller: 'createEditBudgetController',
                templateUrl: '/desktop/create_edit_budget.html'
            };
            var debitEditState = {
                name: "createEditDebit",
                controller: 'createEditDebitController',
                templateUrl: '/desktop/create_edit_debit.html'
            };
            var autoDebitEditState = {
                name: "createEditAutoDebit",
                controller: 'createEditAutoDebitController',
                templateUrl: '/desktop/create_edit_auto_debit.html'
            };
        }
        $stateProvider.state(baseState);
        $stateProvider.state(loginState);
        $stateProvider.state(homeState);
        $stateProvider.state(registerState);
        $stateProvider.state(forgotPasswordState);
        $stateProvider.state(budgetState);
        $stateProvider.state(debitState);
        $stateProvider.state(autoDebitState);
        $stateProvider.state(statisticsState);
        $stateProvider.state(budgetEditState);
        $stateProvider.state(debitEditState);
        $stateProvider.state(searchState);
        $stateProvider.state(autoDebitEditState);
        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    })
    .controller("indexController", function ($state, $scope, $rootScope) {
        $scope.isMobile = function(){
            var md = new MobileDetect(window.navigator.userAgent);
            $rootScope.isMobile = (md.mobile() || md.phone());
            return $rootScope.isMobile;
        };
        if($scope.authenticated){
            $scope.fromLogin = true;
            $state.go("home");
        }
        else{
            $state.go("login");
        }
    });