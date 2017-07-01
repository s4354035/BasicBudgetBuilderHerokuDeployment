/**
 * Controller for showing search results
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .controller('searchResultController',[ '$scope', '$rootScope', function($scope, $rootScope){
        $scope.searchResults =  $rootScope.searchResults;
        $scope.searchResultMessage = $rootScope.searchResultMessage;
        $scope.$on("refreshSearch", function () {
            $scope.searchResults =  $rootScope.searchResults;
            $scope.searchResultMessage = $rootScope.searchResultMessage;
        });
    }]);
