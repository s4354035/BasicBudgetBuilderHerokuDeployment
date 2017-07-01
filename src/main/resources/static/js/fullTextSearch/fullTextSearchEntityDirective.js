/**
 * Directory for search result entries
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .directive('search1',[ '$rootScope', '$state', function($rootScope, $state) {
        var directive = {};
        directive.restrict = 'E';
        directive.template = "<b>[Type:{{search1.type}}]</b>{{search1.description}}";
        directive.scope = {
            search1 : "=value"
        };

        directive.compile = function(element, attributes) {
            var linkFunction = function($scope, element, attributes) {
                // element.css("foreground-color", "#73AD21");
                element.bind('click', function () {
                    if ($scope.search1.type == "BUDGET") {
                        $rootScope.selectedBudget = $scope.search1.budgetRep;
                        $state.go('createEditBudget');
                    }
                    else if ($scope.search1.type == "DEBIT") {
                        $rootScope.selectedDebit = $scope.search1.debitRep;
                        $state.go('createEditDebit');
                    }
                    else if ($scope.search1.type == "FIXED_COST") {
                        $rootScope.selectedAutoDebit = $scope.search1.autoDebitRep;
                        $state.go('createEditAutoDebit');
                    }
                });
            };
            return linkFunction;
        };
        return directive;
    }]);