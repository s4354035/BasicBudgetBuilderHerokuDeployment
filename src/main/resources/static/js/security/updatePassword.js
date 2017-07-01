/**
 * Controller for update_content.html
 * Created by Hanzi Jing on 21-May-17.
 */
angular.module('reset_password')
    .controller("updatePasswordController", function($scope, $http){
        var view = $scope;
        view.error = false;
        view.success = false;
        view.update = function(){
            view.error = false;
            view.success = false;
            view.update_password_error = "";
            view.update_confirm_password_error = "";
            view.update_general_error = "";
            view.update_success_message = "";
            var passwords = [view.password, view.confirmPassword];
            $http.post('/updatePassword', passwords)
                .then(
                    function(){
                        view.success = true;
                        view.update_success_message = "Password has been successfully changed";
                    },
                    function (response) {
                        if (response != null && response.data != null) {
                            var errorMap = response.data;
                            if (errorMap.password != null){
                                view.update_password_error = errorMap.password;
                            }
                            if (errorMap.confirmPassword != null){
                                view.update_confirm_password_error = errorMap.confirmPassword;
                            }
                            if (errorMap.general != null){
                                view.error = true;
                                view.update_general_error = errorMap.general;
                            }
                        }
                    }
                )
        }});