// Angular
var cooksterAdminApp = angular.module('cooksterAdminApp', [
    'ngRoute',
    'ui.bootstrap'
]);

cooksterAdminApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/', {
                templateUrl: 'dashboard.html',
                controller: 'DashboardCtrl',
                title: 'Dashboard'
            }).
            when('/recipes', {
                templateUrl: 'recipes.html',
                controller: 'RecipesCtrl',
                title: 'Recipes'
            }).
            when('/recipes/create', {
                templateUrl: 'newRecipe.html',
                controller: 'RecipesCtrl',
                title: 'New Recipe'
            }).
            otherwise({
                redirectTo: '/'
            });
    }]);

cooksterAdminApp.run(['$location', '$rootScope', function($location, $rootScope) {
    $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
        $rootScope.title = current.$$route.title;
    });
}]);










var server = "http://localhost:8080/rest/",
recipe = {
    steps : []
};

$(function(){
    var stepCount = 0;
    $('#addStep').on('click', function(e) {
        e.preventDefault();
        console.log('adding step '+stepCount);

        recipe.steps.push({});

        $('#stepsDiv').append(
            '<div class="form-group col-md-4" id="stepDiv'+stepCount+'"><div><h4>Step '+stepCount+'</h4></div>' +
            '<input id="stepOrderNumber'+stepCount+'" class="form-control" name="stepOrderNumber'+stepCount+'" type="text" placeholder="Step '+stepCount+' order number" value="'+stepCount+'">' +
            '<input id="stepTime'+stepCount+'" class="form-control" name="stepTime'+stepCount+'" type="text" placeholder="Step '+stepCount+' time">' +
            '<input id="stepDescription'+stepCount+'" class="form-control" name="stepDescription'+stepCount+'" type="text" placeholder="Step '+stepCount+' description">' +
            '<span id="upload-step' + stepCount + '-picture" class="btn btn-success fileinput-button"><i class="glyphicon glyphicon-plus"></i><span>Select Image...</span> </span> <div class="thumbnail-step' + stepCount + '-picture"></div></div>');

        // Add upload input field
        $('#upload-step' + stepCount + '-picture').append($.cloudinary.unsigned_upload_tag("vdexbpws",
            { cloud_name: 'cookster' }));

        // Add picture upload listener
        // Thumbnail upload listener
        $('#upload-step' + stepCount + '-picture .cloudinary_fileupload').unsigned_cloudinary_upload("vdexbpws",
            { cloud_name: 'cookster', tags: 'admin_uploads' },
            { multiple: false }
        ).bind('cloudinarydone', function(e, data) {

                var stepId = $(this).parent().attr('id').match('[^0-9]*([0-9]*)')[1];
                recipe.steps[stepId].pictureUrl = data.result.url;
                recipe.steps[stepId].pictureId = data.result.public_id;

                $(e.target.parentElement.parentElement).append($.cloudinary.image(data.result.public_id,
                    {width: 150, height: 100, crop: 'fill'} ))}

        ).bind('cloudinaryprogress', function(e, data) {

                //TODO

            });

        // Initialize step json data
        recipe.steps[stepCount].orderNumber = stepCount;
        recipe.steps[stepCount].pictureUrl = "";
        recipe.steps[stepCount].pictureId = "";
        recipe.steps[stepCount].time = 0;
        recipe.steps[stepCount].description = "";

        stepCount++;

    });
    $('#removeStep').on('click', function(e) {
        e.preventDefault();
        if (stepCount === 0) {
            console.log('no more steps to remove!');
            return;
        } else {
            console.log('removing step '+stepCount);
        }

        var lastStepEl = window.document.getElementById('stepDiv' + (stepCount - 1));
        lastStepEl.parentNode.removeChild(lastStepEl);

        recipe.steps.pop();
        stepCount--;

    });
});


$(document).ready(function(){

    ////Cloudinary
    //$.cloudinary.config({ cloud_name: 'cookster', api_key: '923899252498486'});
    //
    //$('#upload-picture').append($.cloudinary.unsigned_upload_tag("vdexbpws",
    //    { cloud_name: 'cookster' }));
    //
    //$('#upload-thumbnail').append($.cloudinary.unsigned_upload_tag("vdexbpws",
    //    { cloud_name: 'cookster' }));
    //
    //// Picture upload listener
    //$('#upload-picture .cloudinary_fileupload').unsigned_cloudinary_upload("vdexbpws",
    //    { cloud_name: 'cookster', tags: 'admin_uploads' },
    //    { multiple: false }
    //).bind('cloudinarydone', function(e, data) {
    //
    //        //console.log($.cloudinary.image(data.result.public_id)[0].src);
    //
    //        recipe.pictureUrl = data.result.url;
    //        recipe.pictureId = data.result.public_id;
    //
    //        //console.log(recipe);
    //        //console.log(data.result);
    //
    //        //console.log(e);
    //        $(e.target.parentElement.parentElement).append($.cloudinary.image(data.result.public_id,
    //            {width: 150, height: 100, crop: 'fill'} ))}
    //
    //).bind('cloudinaryprogress', function(e, data) {
    //
    //        $('.progress-bar').css('width',
    //            Math.round((data.loaded * 100.0) / data.total) + '%');
    //
    //    });
    //
    //
    //// Thumbnail upload listener
    //$('#upload-thumbnail .cloudinary_fileupload').unsigned_cloudinary_upload("vdexbpws",
    //    { cloud_name: 'cookster', tags: 'admin_uploads' },
    //    { multiple: false }
    //).bind('cloudinarydone', function(e, data) {
    //
    //        recipe.thumbnailUrl = data.result.url;
    //        recipe.thumbnailId = data.result.public_id;
    //
    //        $(e.target.parentElement.parentElement).append($.cloudinary.image(data.result.public_id,
    //            { format: 'jpg', width: 150, height: 100,
    //                crop: 'thumb', gravity: 'face', effect: 'saturation:50' } ))}
    //
    //).bind('cloudinaryprogress', function(e, data) {
    //
    //        //TODO
    //
    //    });


});
