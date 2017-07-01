/**
 * Controller for update_password.html
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('reset_password', [ ])
    .controller("resetPasswordController", function($scope){
        $scope.isMobile = function(){
            var md = new MobileDetect(window.navigator.userAgent);
            return (md.mobile() || md.phone());
        };
    });