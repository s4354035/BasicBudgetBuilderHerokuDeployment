/**
 * Controller for registration.html
 * Created by Hanzi Jing on 7/05/2017.
 */
angular.module('mainApp')
    .controller("registrationController",['$scope', '$http', '$state', function($scope, $http, $state){
        var view = $scope;
        view.newUser = {};
        view.error = false;
        view.registration = function(){
            view.error = false;
            view.registration_name_error = "";
            view.registration_password_error = "";
            view.registration_confirm_password_error = "";
            view.registration_email_error = "";
            view.registration_general_error = "";
            $http.post('/registration', view.newUser)
                .then(
                    function () {
                        $state.go("login");
                    },
                    function (response) {
                        if (response != null && response.data != null){
                            var errorMap = response.data;
                            if (errorMap.name != null){
                                view.registration_name_error = errorMap.name;
                            }
                            if (errorMap.password != null){
                                view.registration_password_error = errorMap.password;
                            }
                            if (errorMap.confirmPassword != null){
                                view.registration_confirm_password_error = errorMap.confirmPassword;
                            }
                            if (errorMap.email != null){
                                view.registration_email_error = errorMap.email;
                            }
                            if (errorMap.general != null){
                                view.error = true;
                                view.registration_general_error = errorMap.general;
                            }
                        }
                    }
                )
        };
    }]);
