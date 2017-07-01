/**
 * Controller for createEditBudget.html
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
.controller("createEditBudgetController", ['$scope', '$rootScope', '$http', '$state', '$translate', 'dialogs', function($scope, $rootScope, $http, $state, $translate, dialogs){
    $scope.lang = 'en-US';
    $scope.language = 'English';
    var view = $scope;
    var budget = {};
    view.error = false;
    view.selectedPresent = false;
    view.CEB_date = new Date();
    view.CEB_interval = "WEEK";
    if ($rootScope.selectedBudget != null){
        view.selectedPresent = true;
        budget = $rootScope.selectedBudget;
        view.CEB_category_name = budget.categoryName;
        view.CEB_category_colour = budget.categoryColour;
        view.CEB_description = budget.description;
        view.CEB_amount = budget.amount;
        view.CEB_interval = budget.budgetInterval;
        view.CEB_date = new Date(budget.effectiveDate);
    }
    if(view.CEB_category_colour == null|| view.CEB_category_colour.toString() == ""){
        view.CEB_category_colour = "#FFFFFF";
    }
    view.createEditBudget = function(){
        view.error = false;
        view.CEB_category_name_error = "";
        view.CEB_category_colour_error = "";
        view.CEB_amount_error = "";
        view.CEB_interval_error = "";
        view.CEB_date_error = "";
        view.CEB_general_error = "";
        if(budget.categoryName != null && view.CEB_category_name != null && budget.categoryName != view.CEB_category_name){
            budget.categoryId = null;
        }
        budget.categoryName = view.CEB_category_name;
        budget.categoryColour = view.CEB_category_colour;
        budget.description = view.CEB_description;
        budget.amount = view.CEB_amount;
        budget.budgetInterval = view.CEB_interval;
        budget.effectiveDate = view.CEB_date.getFullYear() + "-" + (view.CEB_date.getMonth()+1) + "-" + view.CEB_date.getDate();
        $http.post('/budget', budget)
            .then(
                function (){
                    $rootScope.$broadcast("refresh_statistics");
                    $state.go("home.budget");
                },
                function (response) {
                    if (response != null && response.data != null) {
                        var errorMap = response.data;
                        if (errorMap.categoryName != null) {
                            view.CEB_category_name_error = errorMap.categoryName;
                        }
                        if (errorMap.categoryColour != null) {
                            view.CEB_category_colour_error = errorMap.categoryColour;
                        }
                        if (errorMap.amount != null) {
                            view.CEB_amount_error = errorMap.amount;
                        }
                        if (errorMap.budgetInterval != null) {
                            view.CEB_interval_error = errorMap.budgetInterval;
                        }
                        if (errorMap.effectiveDate != null) {
                            view.CEB_date_error = errorMap.effectiveDate;
                        }
                        if (errorMap.general != null) {
                            view.error = true;
                            view.CEB_general_error = errorMap.general;
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
    view.cancelBudget = function(){
        view.error = false;
        view.CEB_category_name = null;
        view.CEB_category_colour = null;
        view.CEB_description = null;
        view.CEB_amount = null;
        view.CEB_interval = null;
        view.CEB_date = null;
        $state.go('home.budget');
    };
}]);