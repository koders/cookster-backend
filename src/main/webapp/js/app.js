$(function(){
    var stepsCount = 0;
    $('#addStep').on('click', function(e) {
        e.preventDefault();
        stepsCount++;
        console.log('adding step '+stepsCount);

        $('#stepsDiv').append(
            '<div class="pure-group" id="stepDiv'+stepsCount+'">Step '+stepsCount+
            '<input id="stepOrderNumber'+stepsCount+'" name="stepOrderNumber'+stepsCount+'" type="text" placeholder="Step '+stepsCount+' order number" value="'+stepsCount+'">' +
            '<input id="stepTime'+stepsCount+'" name="stepTime'+stepsCount+'" type="text" placeholder="Step '+stepsCount+' time">' +
            '<input id="stepDescription'+stepsCount+'" name="stepDescription'+stepsCount+'" type="text" placeholder="Step '+stepsCount+' description">' +
            '<input id="stepPicture'+stepsCount+'" name="stepPicture'+stepsCount+'" type="file">' +
            '</div>');

    });
    $('#removeStep').on('click', function(e) {
        e.preventDefault();
        if (stepsCount === 0) {
            console.log('no more steps to remove!');
            return;
        } else {
            console.log('removing step '+stepsCount);
        }

        var lastStepEl = window.document.getElementById('stepDiv'+stepsCount);
        lastStepEl.parentNode.removeChild(lastStepEl);

        stepsCount--;

    });
});