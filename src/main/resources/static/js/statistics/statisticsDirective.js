/**
 * Directive for statistic category colour representations (not implemented due to time restrictions)
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .directive('category',[ '$rootScope', '$state', function($rootScope, $state) {
        var directive = {};
        directive.restrict = 'E';
        directive.template = "<div></div>";
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
                element.css("background-color", $scope.budget.category_colour);
                element.css("border-radius", "5px");
                element.css("height", "10px");
                element.css("width", "10px");
                element.css("display", "inline-block");
            };
            return linkFunction;
        };
        return directive;
    }]);