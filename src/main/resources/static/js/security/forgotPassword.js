/**
 * Controller for forgot_password.html
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .controller('forgotPasswordController',[ '$scope', '$http', '$state', function($scope, $http){
        var view = $scope;
        view.error = false;
        view.success = false;
        view.resetPassword = function(){
            view.error = false;
            view.success = false;
            view.email_error ="";
            view.email_message = "";
            $http.post('/resetPassword', view.emailAddr)
                .then(
                    function () {
                        view.success = true;
                        view.email_message = "The email for resetting password was set to your account!";
                    },
                    function () {
                        view.error = true;
                        view.email_error = "Error In E-mail Address";
                    });
        }
    }]);