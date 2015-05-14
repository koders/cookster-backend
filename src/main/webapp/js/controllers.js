cooksterAdminApp.controller('MainCtrl',function($scope){
    $scope.numbers = [0,1,2,3];
});

cooksterAdminApp.controller('DashboardCtrl',function($scope){
    $scope.numbers = [0,1,2,3];
});

cooksterAdminApp.controller('RecipesCtrl',function($scope, $http, api){

    $scope.recipe = {};
    $scope.recipe.steps = [];
    $scope.recipe.ingredients = [];

    api.listRecipes('recipes',function(results) {
        $scope.recipes = results;
    });
    api.listCategories('categories',function(results) {
        $scope.categories = results;
    });
    api.listAuthors('authors',function(results) {
        $scope.authors = results;
    });
    api.listLevels('levels',function(results) {
        $scope.levels = results;
    });
    api.listProducts('products',function(results) {
        $scope.products = results;
    });
    api.listMeasurements('measurements',function(results) {
        $scope.measurements = results;
    });

    //Cloudinary
    $.cloudinary.config({ cloud_name: 'cookster', api_key: '923899252498486'});

    $('#upload-picture').append($.cloudinary.unsigned_upload_tag("vdexbpws",
        { cloud_name: 'cookster' }));

    $('#upload-thumbnail').append($.cloudinary.unsigned_upload_tag("vdexbpws",
        { cloud_name: 'cookster' }));

    // Picture upload listener
    $('#upload-picture .cloudinary_fileupload').unsigned_cloudinary_upload("vdexbpws",
        { cloud_name: 'cookster', tags: 'admin_uploads' },
        { multiple: false }
    ).bind('cloudinarydone', function(e, data) {

            //console.log($.cloudinary.image(data.result.public_id)[0].src);

            $scope.recipe.pictureUrl = data.result.url;
            $scope.recipe.pictureId = data.result.public_id;

            $(e.target.parentElement.parentElement).append('<a href="" class="thumbnail" style="width: 150px;">' + ($.cloudinary.image(data.result.public_id,
                { width: 150, height: 100,crop: 'fill'}).prop('outerHTML')) + '</a>')}

    ).bind('cloudinaryprogress', function(e, data) {

            $('.progress-bar').css('width',
                Math.round((data.loaded * 100.0) / data.total) + '%');

        });


    // Thumbnail upload listener
    $('#upload-thumbnail .cloudinary_fileupload').unsigned_cloudinary_upload("vdexbpws",
        { cloud_name: 'cookster', tags: 'admin_uploads' },
        { multiple: false }
    ).bind('cloudinarydone', function(e, data) {

            $scope.recipe.thumbnailUrl = data.result.url;
            $scope.recipe.thumbnailId = data.result.public_id;

            $(e.target.parentElement.parentElement).append('<a href="" class="thumbnail" style="width: 150px;">' + ($.cloudinary.image(data.result.public_id,
                { width: 150, height: 100,crop: 'fill'}).prop('outerHTML')) + '</a>')}

    ).bind('cloudinaryprogress', function(e, data) {

        //TODO

    });

    $scope.addIngredient = function($event) {
        $event.preventDefault();
        var pos = $scope.recipe.ingredients.length;
        $scope.recipe.ingredients.push({id:pos});
    };

    $scope.removeIngredient = function(id) {
        for(var i = 0; i < $scope.recipe.ingredients.length; i++) {
            if($scope.recipe.ingredients[i].id == id) {
                $scope.recipe.ingredients.splice(i, 1);
                return;
            }
        }
    };

    $scope.addStep = function($event) {
        $event.preventDefault();
        var orderNumber = $scope.recipe.steps.length;
        $scope.recipe.steps.push({orderNumber: orderNumber});

        setTimeout(function(){
            // Add upload input field
            $('#upload-step' + orderNumber + '-picture').append($.cloudinary.unsigned_upload_tag("vdexbpws",
                { cloud_name: 'cookster' }));

            // Add picture upload listener
            // Thumbnail upload listener
            $('#upload-step' + orderNumber + '-picture .cloudinary_fileupload').unsigned_cloudinary_upload("vdexbpws",
                { cloud_name: 'cookster', tags: 'admin_uploads' },
                { multiple: false }
            ).bind('cloudinarydone', function(e, data) {

                    var stepId = $(this).parent().attr('id').match('[^0-9]*([0-9]*)')[1];
                    var step;
                    for(var i = 0; i < $scope.recipe.steps.length; i++) {
                        if($scope.recipe.steps[i].orderNumber == stepId) {
                            step = $scope.recipe.steps[i];
                            break;
                        }
                    }
                    if(step == null)return;
                    step.pictureUrl = data.result.url;
                    step.pictureId = data.result.public_id;

                    $(e.target.parentElement.parentElement).append('<a href="" class="thumbnail" style="width: 150px;">' + ($.cloudinary.image(data.result.public_id,
                        { width: 150, height: 100,crop: 'fill'}).prop('outerHTML')) + '</a>')}

            ).bind('cloudinaryprogress', function(e, data) {

                    //TODO

                });
        }, 100);
    };

    $scope.submitRecipe = function($event) {
        $event.preventDefault();

        var res = $http.post("/rest/admin/recipes/create", $scope.recipe);
        res.success(function(){
            alert("ok");
        });
        res.error(function(){
            alert("failed");
        });
    };

});

cooksterAdminApp.controller('NavigationCtrl',function($scope, $location){
    $scope.isActive = function (viewLocation) {
        var active = (viewLocation === $location.path());
        return active;
    };
});

cooksterAdminApp.factory('api', function($http){
    function getData(resource, callback){
        $http({
            method: 'GET',
            url: '/rest/' + resource,
            cache: true
        }).success(callback);
    }
    return {
        listRecipes: getData,
        listCategories: getData,
        listAuthors: getData,
        listLevels: getData,
        listProducts: getData,
        listMeasurements: getData
    };
});

//cooksterAdminApp.factory('recipes', function($http){
//    function getData(callback){
//        $http({
//            method: 'GET',
//            url: '/rest/recipes',
//            cache: true
//        }).success(callback);
//    }
//    return {
//        list: getData,
//        find: function(name, callback){
//            getData(function(data) {
//                var recipe = data.filter(function(entry){
//                    return entry.name === name;
//                })[0];
//                callback(recipe);
//            });
//        }
//    };
//});
//
//cooksterAdminApp.factory('categories', function($http){
//    function getData(callback){
//        $http({
//            method: 'GET',
//            url: '/rest/categories',
//            cache: true
//        }).success(callback);
//    }
//    return {
//        list: getData,
//        find: function(name, callback){
//            getData(function(data) {
//                var category = data.filter(function(entry){
//                    return entry.name === name;
//                })[0];
//                callback(category);
//            });
//        }
//    };
//});
//
//cooksterAdminApp.factory('levels', function($http){
//    function getData(callback){
//        $http({
//            method: 'GET',
//            url: '/rest/levels',
//            cache: true
//        }).success(callback);
//    }
//    return {
//        list: getData,
//        find: function(name, callback){
//            getData(function(data) {
//                var level = data.filter(function(entry){
//                    return entry.name === name;
//                })[0];
//                callback(level);
//            });
//        }
//    };
//});


cooksterAdminApp.directive('loading',   ['$http' ,function ($http)
    {
        return {
            restrict: 'A',
            link: function (scope, elm, attrs)
            {
                scope.isLoading = function () {
                    return $http.pendingRequests.length > 0;
                };

                scope.$watch(scope.isLoading, function (v)
                {
                    if(v){
                        elm.show();
                    }else{
                        elm.hide();
                    }
                });
            }
        };

    }]);
