/**
 * Controller for statistics.html
 * Created by Hanzi Jing on 5/3/2017.
 */
angular.module('mainApp')
    .controller("statisticsController",['$scope', '$rootScope', '$http', '$state', '$translate', 'dialogs', function($scope, $rootScope, $http, $state, $translate, dialogs){
        var view = $scope;
        view.lang = 'en-US';
        view.language = 'English';
        view.totalBudget = [];
        view.totalSpent = [];
        view.totalRemainder = [];
        view.totalRealRemainder = [];
        view.categoryNames = [];
        view.categoryColours = [];
        view.currentBudgets = [];
        view.currentDebits = [];
        view.intervalDates = [];
        view.sixIntervalDebits = [];
        var loadBudget = function(){
            $http.get('/budget')
                .then(
                    function (response) {
                        $scope.allBudgets = response.data;
                    },
                    function (response) {
                        if(response!= null && response.data != null){
                            if(response.data.session != null){
                                dialogs.error("ERROR",  response.data.session, {size:'sm', mobile:$rootScope.isMobile});
                                $state.go("login");
                            }
                            else if(response.data.general != null){
                                dialogs.error("ERROR",  response.data.general, {size:'sm', mobile:$rootScope.isMobile});
                            }
                        }
                        else{
                            dialogs.error("ERROR", "System Error", {size:'sm', mobile:$rootScope.isMobile});
                        }
                    })
        };
        if ($rootScope.isMobile){
            loadBudget();
        }
        var calculateStat = function(){
            var selectedBudgets = $rootScope.selectedBudgets;
            $http.post('/statistics', selectedBudgets)
                .then(
                    function (response) {
                        view.totalBudget = response.data.totalBudget;
                        view.totalSpent = response.data.totalSpent;
                        view.totalRemainder = response.data.totalRemainder;
                        view.totalRealRemainder = response.data.totalRealRemainder;
                        view.categoryNames = response.data.categoryNames;
                        view.categoryColours = response.data.categoryColours;
                        view.currentBudgets = response.data.currentBudgets;
                        view.currentDebits = response.data.currentDebits;
                        view.intervalDates = response.data.intervalDates;
                        view.sixIntervalDebits = response.data.sixIntervalDebits;
                        view.loadStatistics();
                    },
                    function (response) {
                        if(response!= null && response.data != null){
                            if(response.data.session != null){
                                dialogs.error("ERROR",  response.data.session, {size:'sm', mobile:$rootScope.isMobile});
                                $state.go("login");
                            }
                            else if(response.data.general != null){
                                dialogs.error("ERROR",  response.data.general, {size:'sm', mobile:$rootScope.isMobile});
                            }
                        }
                        else{
                            dialogs.error("ERROR", "System Error", {size:'sm', mobile:$rootScope.isMobile});
                        }
                    })
        };
        view.$on("refresh_statistics", function(){
            calculateStat();
        });
        view.intervalSelect = function (interval){
            $rootScope.selectedStatInterval = interval;
            view.data = [view.currentBudgets[interval], view.currentDebits[interval]];
            view.stackData =[view.currentBudgets[interval], view.currentDebits[interval]];
            view.lineData = view.sixIntervalDebits[interval];
            view.lineLabels = view.intervalDates[interval];
            view.interval_name = interval;
        };
        view.graphSelect = function (graph){
            $rootScope.selectedStatGraph = graph;
            if(graph == 'PIE'){
                view.graph_type = "Pie Chart";
                view.pie_show = true;
                view.line_show = false;
                view.pair_show = false;
                view.stack_show = false;
            }
            else if(graph == 'LINE'){
                view.graph_type = "Line Graph";
                view.pie_show = false;
                view.line_show = true;
                view.pair_show = false;
                view.stack_show = false;
            }
            else if(graph == 'PAIR'){
                view.graph_type = "Paired Bar Graph";
                view.pie_show = false;
                view.line_show = false;
                view.pair_show = true;
                view.stack_show = false;
            }
            else if(graph == 'STACK'){
                view.graph_type = "Stacked Bar Graph";
                view.pie_show = false;
                view.line_show = false;
                view.pair_show = false;
                view.stack_show = true;
            }
            else{
                view.graph_type = "No Graph";
                view.pie_show = false;
                view.line_show = false;
                view.pair_show = false;
                view.stack_show = false;
            }
        };
        view.loadStatistics = function () {
            view.series = ["Budget", "Debit"];
            view.labels = view.categoryNames;
            view.stackLabels = ["Budget", "Debit"];
            view.stackSeries = view.categoryNames;
            view.lineSeries = view.categoryNames;
            view.dataSetOverride = [
                {
                    fill: true,
                    backgroundColor: view.categoryColours
                },
                {
                    fill: true,
                    backgroundColor: view.categoryColours
                }];
            view.lineDataSetOverride = [];
            for (var i = 0; i < view.categoryColours.length - 1; i++) {
                view.lineDataSetOverride [i] =
                    {
                        fill: false,
                        borderColor: view.categoryColours[i]
                    };
            }
            view.resizeOptions ={
                responsive: false
            };
            view.options = {
                responsive: false,
                scales: {
                    xAxes: [{
                        stacked: true
                    }]
                }
            };
            view.lineSeries = [];
            for (var x = 0; x < view.categoryNames.length - 1; x++) {
                view.lineSeries[x] = view.categoryNames[x];
            }
            view.intervalSelect($rootScope.selectedStatInterval);
            view.graphSelect($rootScope.selectedStatGraph);
        };
        calculateStat();
        $('.interval-nav a').click(function(e) {
            e.preventDefault();
            $('.interval-nav a').removeClass('active');
            $(this).addClass('active');
        });
        $('.graph-nav a').click(function(e) {
            e.preventDefault();
            $('.graph-nav a').removeClass('active');
            $(this).addClass('active');
        });
    }]);
