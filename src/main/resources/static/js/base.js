/**
 * Controller for base.html
 * Created by Hanzi Jing on 17-May-17.
 */
angular.module('mainApp')
    .controller("indexController", ['$scope', '$state', function ($state, $scope) {
        if($scope.authenticated){
            $scope.fromLogin = true;
            $state.go("home");
        }
        else{
            $state.go("login");
        }
    }]);