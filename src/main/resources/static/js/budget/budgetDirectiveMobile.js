/**
 * Directive for mobile version of budget entries
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .directive('budgetMobile',[ '$rootScope', '$state', function($rootScope, $state) {
        var directive = {};
        directive.restrict = 'E';
        directive.template = "<div class='category-colour' style='background-color:{{budget.categoryColour}}'></div><div class='category'>{{budget.categoryName}}</div><div class='amount'>{{budget.amount | currency}}" +
            " </div><div class='spent'>Spent: {{budget.spent | currency}} <span class='interval'>{{budget.budgetInterval}}</span></div>";
        directive.scope = {
            budget : "=name"
        };
        var replaceBudget = function(budgets, budget){
            if(budgets == null || budgets.length <= 0 || budget == null){
                return false;
            }
            for (var index = 0; index < budgets.length; index++){
                if(budgets[index].id == budget.id ){
                    budgets.splice(index, 1);
                    budgets.push(budget);
                    return true;
                }
            }
            return false;
        };
        directive.compile = function(element, attributes) {
            var linkFunction = function($scope, element, attributes) {
                $scope.selected = replaceBudget($rootScope.selectedBudgets, $scope.budget);

                element.css("background-color", $scope.budget.colour);
                if($scope.selected){
                    element.css("border", "9px solid #006600");
                }
                else{
                    element.css("border", "9px solid "+ $scope.budget.colour);
                }
                element.css("border-radius", "30px");
                element.css("height", "187px");
                element.css("width", "386px");
                element.css("padding", "9px");
                element.css("margin", "9px 9px");
                element.css("display", "inline-block");
                element.bind('click', function () {
                    if($scope.selected ){
                        element.css("border", "9px solid "+ $scope.budget.colour);
                        $scope.selected = false;
                        var index = $rootScope.selectedBudgets.indexOf($scope.budget);
                        if(index >= 0){
                            $rootScope.selectedBudgets.splice(index, 1);
                        }
                    }
                    else{
                        element.css("border", "9px solid #006600");
                        $scope.selected = true;
                        if($rootScope.selectedBudgets.indexOf($scope.budget) < 0){
                            $rootScope.selectedBudgets.push($scope.budget);

                        }
                    }
                    $rootScope.$broadcast("reload_debit");
                    $rootScope.$broadcast("reload_auto_debit");
                    $rootScope.$broadcast("refresh_statistics");
                });
                element.bind('dblclick', function (){
                    $rootScope.selectedBudget = $scope.budget;
                    $state.go('createEditBudget');
                });
            };
            return linkFunction;
        };
        return directive;
    }]);