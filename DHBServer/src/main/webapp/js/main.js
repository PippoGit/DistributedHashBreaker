var test_configuration = `
{
    "idAttack" : "001",
    "totalPercentage" : 40,
    "numCollisions" : 1234,
    "buckets": [
        {
            "id": 0,
            "percentage": 23
        },
        {
            "id": 1,
            "percentage": 32
        },
        {
            "id": 2,
            "percentage": 56
        },
        {
            "id": 3,
            "percentage": 89
        },
        {
            "id": 4,
            "percentage": 2
        },
        {
            "id": 5,
            "percentage": 12
        },
        {
            "id": 6,
            "percentage": 100
        },
        {
            "id": 7,
            "percentage": 100
        },
        {
            "id": 8,
            "percentage": 25
        },
        {
            "id": 9,
            "percentage": 28
        },
        {
            "id": 10,
            "percentage": 35,
            "available" : true
        },
        {
            "id": 11,
            "percentage": 23
        },
        {
            "id": 12,
            "percentage": 23
        },
        {
            "id": 13,
            "percentage": 56
        },
        {
            "id": 14,
            "percentage": 23
        },
        {
            "id": 15,
            "percentage": 78
        },
        {
            "id": 16,
            "percentage": 76
        },
        {
            "id": 17,
            "percentage": 77
        },
        {
            "id": 18,
            "percentage": 36
        }
    ],
    "numAvailableBuckets": 10,
    "numWorkingBuckets" : 20,
    "numCompletedBuckets" : 25,
    "etc" : "2h 30m"
}`;

var test_bucket = `
{
    "id"             : "id",
    "percentage"     : 0.0,
    "idWorker"       : "username",
    "dateAllocation" : "2016-07-24 19:20:13",
    "lastHeartbeat"  : "2016-07-25 00:00:00",
    "available"      : false
}`;

var current_state = {};

$(document).ready(function() {
    
    $("#heatmap").on("click", "div", function(event){
        load_bucket($(this).attr('data-id'));
    });
    
    var webSocket = new WebSocket("ws://localhost:8080/DHBServer/test_initial_config");
    webSocket.onopen = function(message) {
        console.log(message);
        current_state = JSON.parse(message);
        load_state();
    };
    

});

function load_state() {
    
    // Load main GUI elements 
    $("#attack-id-label").text("#" + current_state.idAttack);
    $("#total-percentage").text('' + current_state.totalPercentage + "%");
    $("#total-percentage").css('width', '' + Math.max(3, current_state.totalPercentage) + "%");
    $("#etc-label").text(current_state.etc);
    $("#num-collisions-label").text(current_state.numCollisions);

    // Init graphs and buckets stuff
    init_allocation_chart();
    generate_heatmap();
    init_graphs();

}

function generate_heatmap(){
    var heatmap = $("#heatmap");
    current_state.buckets.forEach( function(b) {
        var percentage = b.percentage;
        var bucketClass = bucket_style(b.id);
        heatmap.append("<div data-id='" + b.id + "' class='" + bucketClass + "' id='bucket-" + b.id + "' >" + b.id +  "</div>")
    });
}

function bucket_style(id) {
    var percentage = current_state.buckets[id].percentage;
    var bucket_style = ['bucket-l-25', 
                        'bucket-m-25',
                        'bucket-m-50',
                        'bucket-m-75'];

    if(percentage == 100) return "completed-bucket";
    else if (current_state.buckets[id].available) return "available-bucket";

    return bucket_style[Math.floor(percentage/25)]
}

function load_bucket(id) {
    
    var bucket = JSON.parse(test_bucket);
    
    // temp stuff to be removed...
    var temp = current_state.buckets[id];
    bucket.percentage = temp.percentage;
    bucket.id = temp.id;
    ////////

    $("#bucket-id").text("Bucket " + bucket.id);
    $("#bucket-progress").css("width", Math.max(3, bucket.percentage) + "%");
    $("#bucket-progress").text(bucket.percentage + "%");
    
    var idWorker = (bucket.available)?"Not assigned":bucket.idWorker;
    var dateAllocation = (bucket.available)?"Not assigned":bucket.dateAllocation;
    var lastHeartbeat = (bucket.available)?"Not assigned":bucket.lastHeartbeat;


    $("#bucket-username-label").text(idWorker);
    $("#bucket-allocation-date-label").text(dateAllocation);
    $("#bucket-last-heartbeat-label").text(lastHeartbeat);
    
    $("#bucket-inspector").slideDown();
}

function init_graphs() {
    var config = {
        type: 'line',
        data: {
            labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
            datasets: [{
                label: 'My First dataset',
                backgroundColor:"#ec407a",
                borderColor:"#ec407a",
                data: [
                    15,
                    115,
                    15,
                    315,
                    145,
                    155,
                    144
                ],
                fill: false,
            }, {
                label: 'My Second dataset',
                fill: false,
                backgroundColor: "#536de6",
                borderColor: "#536de6",
                data: [
                    123,
                    133,
                    144,
                    122,
                    151,
                    125,
                    145
                ],
            }]
        },
        options: {
            aspectRatio: 2,
            responsive: true,
            title: {
                display: true,
                text: 'Some kind of Performance Chart'
            },
            tooltips: {
                mode: 'index',
                intersect: false,
            },
            hover: {
                mode: 'nearest',
                intersect: true
            },
            scales: {
                xAxes: [{
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: 'Month'
                    }
                }],
                yAxes: [{
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: 'Value'
                    }
                }]
            }
        }
    };
    var ctx = document.getElementById('myChart').getContext('2d');
    var myChart = new Chart(ctx, config);
}

function init_allocation_chart() {
    var bucketAllocationCtx = document.getElementById('bucket-allocation-chart').getContext('2d');
    var bucketAllocationChart = new Chart(bucketAllocationCtx, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [current_state.numWorkingBuckets, current_state.numCompletedBuckets, current_state.numAvailableBuckets],
                backgroundColor: ["#fff48f", "#3cba9f", "#e2eaef"]
            }],
            labels: ['Working', 'Completed', 'Available']
        },
        options: {
            legend: {
                display: true,
                position: "right"
            },
            responsive: true
        }
    });
}