/**
 * Controller for createEditAutoDebit.html
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .controller("createEditAutoDebitController", ['$scope', '$rootScope', '$http', '$state', '$translate', 'dialogs', function($scope, $rootScope, $http, $state, $translate, dialogs){
       var view = $scope;
        var autoDebit = {};
        view.lang = 'en-US';
        view.language = 'English';
        view.error = false;
        view.allBudgets = [];
        view.selectedPresent = false;
        view.CEA_interval = "WEEK";
        if ($rootScope.selectedAutoDebit != null) {
            view.selectedPresent = true;
            autoDebit = $rootScope.selectedAutoDebit;
            view.CEA_description = autoDebit.description;
            view.CEA_amount = autoDebit.amount;
            view.CEA_interval = autoDebit.debitInterval;
        }

        view.loadBudget = function(){
            view.error = false;
            $http.get('/budget')
                .then(
                    function (response) {
                        view.allBudgets = response.data;
                        if(view.allBudgets != null && view.allBudgets.length > 0){
                            view.sel = view.allBudgets[0];
                            if ($rootScope.selectedAutoDebit != null) {
                                autoDebit = $rootScope.selectedAutoDebit;
                                var index = 0;
                                while (index < view.allBudgets.length) {
                                    if (autoDebit.categoryName == view.allBudgets[index].categoryName) {
                                        view.sel = view.allBudgets[index];
                                    }
                                    index++
                                }
                            }
                        }
                    },
                    function (response) {
                        if(response!= null && response.data != null){
                            if(response.data.session != null){
                                dialogs.error("ERROR",  response.data.session, {size:'sm', mobile:$rootScope.isMobile});
                                $state.go("login");
                            }
                            else if(response.data.general != null){
                                dialogs.error("ERROR",  response.data.general, {size:'sm', mobile:$rootScope.isMobile});
                            }
                        }
                        else{
                            dialogs.error("ERROR", "System Error", {size:'sm', mobile:$rootScope.isMobile});
                        }
                    })
        };
        view.loadBudget();
        view.createEditAutoDebit = function(){
            if(autoDebit == null) {
                return;
            }
            view.error = false;
            view.CEA_amount_error = "";
            view.CEA_interval_error = "";
            view.CEA_general_error = "";
            autoDebit.categoryName = view.sel.categoryName;
            autoDebit.categoryColour = view.sel.categoryColour;
            autoDebit.description = view.CEA_description;
            autoDebit.amount = view.CEA_amount;
            autoDebit.debitInterval = view.CEA_interval;
            $http.post('/autoDebit', autoDebit).then(
                function (){
                    $rootScope.$broadcast("refresh_statistics");
                    $state.go("home.autoDebit");
                },
                function (response) {
                    if (response != null && response.data != null) {
                        var errorMap = response.data;
                        if (errorMap.amount != null) {
                            view.CEA_amount_error = errorMap.amount;
                        }
                        if(errorMap.debitInterval != null){
                            view.CEA_interval_error = errorMap.debitInterval;
                        }
                        if (errorMap.general != null) {
                            view.error = true;
                            view.CEA_general_error = errorMap.general;
                        }
                        if(response.data.session != null){
                            dialogs.error("ERROR",  response.data.session, {size:'sm', mobile:$rootScope.isMobile});
                            $state.go("login");
                        }
                        else{
                            dialogs.error("ERROR", "System Error", {size:'sm', mobile:$rootScope.isMobile});
                        }
                    }
                }
            )
        };
        view.cancelAutoDebit = function(){
            view.sel = null;
            view.error = false;
            view.CEA_description = null;
            view.CEA_amount = null;
            view.CEA_interval = null;
            $state.go('home.autoDebit');
        };
    }]);