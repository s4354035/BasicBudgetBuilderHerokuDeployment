/**
 * Controller for createEditDebit.html
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .controller("createEditDebitController", ['$scope', '$rootScope', '$http', '$state', '$translate', 'dialogs', function($scope, $rootScope, $http, $state, $translate, dialogs){
        var view = $scope;
        var debit = {};
        view.lang = 'en-US';
        view.language = 'English';
        view.error = false;
        view.allBudgets = [];
        view.selectedPresent = false;
        view.CED_date = new Date();
        if ($rootScope.selectedDebit != null) {
            view.selectedPresent = true;
            debit = $rootScope.selectedDebit;
            view.CED_description = debit.description;
            view.CED_amount = debit.amount;
            view.CED_date = new Date(debit.date);
            view.CED_attachments = debit.attachments;
        }
        view.loadBudget = function () {
            view.error = false;
            $http.get('/budget')
                .then(
                    function (response) {
                        view.allBudgets = response.data;
                        if (view.allBudgets != null && view.allBudgets.length > 0) {
                            view.sel = view.allBudgets[0];
                            if ($rootScope.selectedDebit != null) {
                                debit = $rootScope.selectedDebit;
                                var index = 0;
                                while (index < view.allBudgets.length) {
                                    if (debit.categoryName == view.allBudgets[index].categoryName) {
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
        view.createEditDebit = function () {
            view.error = false;
            view.CED_category_name_error = "";
            view.CED_amount_error = "";
            view.CED_date_error = "";
            view.CED_general_error = "";
            debit.categoryName = view.sel.categoryName;
            debit.categoryColour = view.sel.categoryColour;
            debit.description = view.CED_description;
            debit.amount = view.CED_amount;
            debit.date = view.CED_date.getFullYear() + "-" + (view.CED_date.getMonth() + 1) + "-" + view.CED_date.getDate();
            debit.attachments = view.CED_attachments;
            $http.post('/debit', debit)
                .then(
                    function () {
                        $rootScope.$broadcast("refresh_statistics");
                        $state.go("home.debit");
                    },
                    function (response) {
                        if (response != null && response.data != null) {
                            var errorMap = response.data;
                            if (errorMap.amount != null) {
                                view.CED_amount_error = errorMap.amount;
                            }
                            if (errorMap.general != null) {
                                view.error = true;
                                view.CED_general_error = errorMap.general;
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
        view.cancelDebit = function () {
            view.sel = null;
            view.error = false;
            view.CED_description = null;
            view.CED_amount = null;
            view.CED_date = null;
            view.CED_attachments = null;
            $state.go('home.debit');
        };
    }]);