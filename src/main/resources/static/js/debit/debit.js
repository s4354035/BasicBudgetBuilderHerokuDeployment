/**
 * Controller for debit.html
 * Created by Hanzi Jing on 5/3/2017.
 */
angular.module('mainApp')
    .filter('currencyFilter', function () {
        return function (value) {
            return "$" + value.toFixed(2);
        }
    })
    .controller("debitController",['$scope', '$rootScope', '$http', '$state', '$translate', 'dialogs', function($scope, $rootScope, $http, $state, $translate, dialogs){
        var view = $scope;
        $('#debit-nav').click();
        view.allBudgets = [];
        var loadBudget = function(){
            $http.get('/budget')
                .then(
                    function (response) {
                        view.allBudgets = response.data;
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
        loadBudget();
        view.allDebits = [];
        var loadDebit = function(){
            var budgets = [];
            $rootScope.selectedDebit = null;
            if($rootScope.selectedBudgets != null){
                budgets = $rootScope.selectedBudgets;
            }
            $http.post('/debitAll', budgets)
                .then(
                    function (response) {
                        view.mainGridOptions.data =  response.data;
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
                    });
        };
        var columnDefs = [];
        if($rootScope.isMobile){
            columnDefs = [{name: 'categoryName', width:"430"},
                {name: 'description', width:"400"},
                {name: 'amount', width:"250", cellFilter: 'currencyFilter'},
                {name: 'date', width:"330"}]
        }
        else{
            columnDefs = [{name: 'categoryName'},
                {name: 'description'},
                {name: 'amount', cellFilter: 'currencyFilter'},
                {name: 'date'}]
        }
        view.mainGridOptions = {
            columnDefs: columnDefs,
            paginationPageSizes: [15, 30, 45],
            paginationPageSize: 15,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            enableRowSelection : true,
            enableRowHeaderSelection: false,
            enableHorizontalScrollbar: 1,
            onRegisterApi: function(gridApi){
                view.gridApi = gridApi;
            },
            data: []
        };
        view.createDebit = function(){
            $rootScope.selectedDebit = null;
            $state.go('createEditDebit');
        };
        view.editDebit = function(){
            var selected =  view.gridApi.selection.getSelectedRows();
            if(selected != null && selected.length > 0) {
                $rootScope.selectedDebit = selected[0];
                $state.go('createEditDebit');
            } else {
                dialogs.notify("MESSAGE", "No Entry selected!", {size:'sm', mobile:$rootScope.isMobile});
            }
        };
        view.deleteDebit = function(){
            $rootScope.selectedDebit = null;
            var selected = view.gridApi.selection.getSelectedRows();
            if(selected != null && selected.length > 0) {
                if(selected[0].id != null){
                    dialogs.confirm("WARNING", "Auto-pay: " + selected.categoryName + " will be deleted", {size :'sm', mobile:$rootScope.isMobile})
                        .result.then(function(){
                            $http.delete('/debit', {params: {id: selected[0].id}})
                            .then(
                                function () {
                                    $rootScope.$broadcast("refresh_statistics");
                                    loadDebit();
                                },
                                function (response) {
                                    if(response!= null && response.data != null){
                                        if(response.data.delete != null){
                                            dialogs.error("ERROR",  response.data.delete, {size:'sm', mobile:$rootScope.isMobile});
                                        }
                                        else if(response.data.general != null){
                                            dialogs.error("ERROR",  response.data.general, {size:'sm', mobile:$rootScope.isMobile});
                                        }
                                        else if(response.data.session != null){
                                            dialogs.error("ERROR",  response.data.session, {size:'sm', mobile:$rootScope.isMobile});
                                            $state.go("login");
                                        }

                                    }
                                    else{
                                        dialogs.error("ERROR", "System Error", {size:'sm', mobile:$rootScope.isMobile});
                                    }
                                })
                    },function(b){
                    });
                }
            } else {
                dialogs.notify("MESSAGE", "No Entry selected!", {size:'sm', mobile:$rootScope.isMobile});
            }
        };
        loadDebit();
        view.$on("reload_debit", function (){
            loadDebit();
        });
    }
    ]);