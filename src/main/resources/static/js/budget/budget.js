/**
 * Controller for budget.html
 * Created by Hanzi Jing on 5/3/2017.
 */
angular.module('mainApp')
    .controller("budgetController", [ '$scope','$rootScope', '$http', '$state', '$translate', 'dialogs', function($scope, $rootScope, $http, $state, $translate, dialogs){
        var view = $scope;
        $('#budget-nav').click();
        view.lang = 'en-US';
        view.language = 'English';
        view.allBudgets = [];
        view.loadBudget = function(){
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
        view.loadBudget();
        view.createBudget = function(){
            $rootScope.selectedBudget = null;
            $state.go('createEditBudget');
        };
        view.editBudget = function(){
            var selected = $rootScope.selectedBudgets;
            if(selected != null && selected.length > 0) {
                $rootScope.selectedBudget = selected[selected.length - 1];
                $state.go('createEditBudget');
            } else {
                dialogs.notify("MESSAGE", "No budget selected!", {size:'sm', mobile:$rootScope.isMobile});
            }
        };
        view.deleteBudget = function(){
            var selected = null;
            var selectedBudgets = $rootScope.selectedBudgets;
            if(selectedBudgets != null && selectedBudgets.length > 0) {
                selected = selectedBudgets[selectedBudgets.length - 1];
                if (selected.id != null){
                    dialogs.confirm("WARNING", "Budget: " + selected.categoryName + " will be deleted", {size :'sm', mobile:$rootScope.isMobile})
                        .result.
                    then(function(){
                        $http.delete('/budget', {params: {id: selected.id}})
                            .then(
                                function (response) {
                                    $rootScope.allBudgets =  response.data;
                                    view.allBudgets = $rootScope.allBudgets;
                                    var index = $rootScope.selectedBudgets.indexOf(selected);
                                    if(index >= 0){
                                        $rootScope.selectedBudgets.splice(index, 1);
                                    }
                                    $rootScope.$broadcast("refresh_statistics");
                                    view.loadBudget();
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
                                    } else {
                                        dialogs.error("ERROR", "System Error", {size:'sm', mobile:$rootScope.isMobile});
                                    }
                                })
                    },function(b){
                    });
                }
            } else {
                dialogs.notify("MESSAGE", "No budget selected!", {size:'sm', mobile:$rootScope.isMobile});
            }
        };
    }]);