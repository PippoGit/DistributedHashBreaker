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
            "percentage": 35
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
    "numCompletedBuckets" : 25
}
`;

var current_state = {};

$(document).ready(function() {
    current_state = JSON.parse(test_configuration);

    load_state();
    init_graphs();
    generate_heatmap();
});

function load_state() {
    $("#attack-id-label").text("#" + current_state.idAttack);
    $("#total-percentage").text('' + current_state.totalPercentage + "%");
    $("#total-percentage").css('width', '' + current_state.totalPercentage + "%");
    $("#num-collisions-label").text(current_state.numCollisions);
}

function generate_heatmap(){
    var heatmap = $("#heatmap");

    current_state.buckets.forEach( function(b) {
        var percentage = b.percentage;
        var id = b.id;
        heatmap.append("<div class='" + bucket_style(percentage) + "' id='bucket-" + id + "' onclick='load_bucket(" + id +")' style='opacity:" + 0.5 + ";'>" + id +  "</div")
    });
}

function bucket_style(percentage) {

    if(percentage == 100) return "completed-bucket";

    var bucket_style = ['bucket-l-25', 
                        'bucket-m-25',
                        'bucket-m-50',
                        'bucket-m-75'];
    return bucket_style[Math.floor(percentage/25)]
}

function load_bucket(bucket) {
    var percentage = current_state.buckets[bucket].percentage;
    $("#bucket-inspector").slideDown();
    $("#bucket-id").text("Bucket " + bucket);
    $("#bucket-progress").css("width", percentage + "%");
    $("#bucket-progress").text(percentage + "%");
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