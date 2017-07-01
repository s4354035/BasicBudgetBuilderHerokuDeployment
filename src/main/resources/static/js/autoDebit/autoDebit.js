/**
 * Controller for debit.html
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .controller("autoDebitController",['$scope', '$rootScope', '$http', '$state', '$translate', 'dialogs', function($scope, $rootScope, $http, $state, $translate, dialogs){
        var view = $scope;
        $('#auto-nav').click();
        view.lang = 'en-US';
        view.language = 'English';
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
        view.allAutoDebits = [];
        var loadAutoDebit = function(){
            var budgets = [];
            $rootScope.selectedAutoDebit = null;
            if($rootScope.selectedBudgets != null){
                budgets = $rootScope.selectedBudgets;
            }
            $http.post('/autoDebitGet', budgets)
                .then(
                    function (response) {
                        view.autoDebitGridOptions.data =  response.data;
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
        var md = new MobileDetect(window.navigator.userAgent);
        var columnDefs  = [];
        if(md.mobile() || md.phone()){
            columnDefs = [{name: 'categoryName', width:'430'},
                {name: 'description', width:'400'},
                {name: 'amount', width:'250', cellFilter: 'currencyFilter'},
                {name: 'debitInterval', width:'330'}]
        }
        else{
            columnDefs = [{name: 'categoryName'},
                {name: 'description'},
                {name: 'amount', cellFilter: 'currencyFilter'},
                {name: 'debitInterval'}]
        }
        view.autoDebitGridOptions = {
            columnDefs: columnDefs,
            paginationPageSizes: [15, 30, 45],
            paginationPageSize: 15,
            multiSelect: false,
            modifierKeysToMultiSelect: false,
            enableRowSelection : true,
            enableRowHeaderSelection: false,
            enableHorizontalScrollbar: 1,
            onRegisterApi: function(gridApi){
                //set gridApi on scope
                view.gridApi = gridApi;
            },
            data: []
        };
        view.createAutoDebit = function(){
            $rootScope.selectedAutoDebit = null;
            $state.go('createEditAutoDebit');
        };

        view.editAutoDebit = function(){
            var selected =  view.gridApi.selection.getSelectedRows();
            if(selected != null && selected.length > 0) {
                $rootScope.selectedAutoDebit = selected[0];
                $state.go('createEditAutoDebit');
            } else {
                dialogs.notify("MESSAGE", "No Entry selected!", {size:'sm', mobile:$rootScope.isMobile});
            }
        };

        view.deleteAutoDebit = function(){
            $rootScope.selectedAutoDebit = null;
            var selected = view.gridApi.selection.getSelectedRows();
            if(selected != null && selected.length > 0) {
                if(selected[0].id != null){
                    dialogs.confirm("WARNING", "Auto-pay: " + selected.categoryName + " will be deleted", {size :'sm', mobile:$rootScope.isMobile})
                        .result.then(function(){
                            $http.delete('/autoDebit', {params: {id: selected[0].id}})
                                .then(
                                function () {
                                    $rootScope.$broadcast("refresh_statistics");
                                    loadAutoDebit();
                                },
                                function (response) {
                                    if(response!= null && response.data != null){
                                        if(response.data.delete != null){
                                            dialogs.error("ERROR",  response.data.delete, {size:'sm', mobile:$rootScope.isMobile});
                                        }
                                        else if(response.data.session != null){
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
                    },function(b){
                    });
                }
            } else {
                dialogs.notify("MESSAGE", "No Entry selected!", {size:'sm', mobile:$rootScope.isMobile});
            }
        };
        loadAutoDebit();
        view.$on("reload_auto_debit", function (){
            loadAutoDebit();
        });
    }]);
