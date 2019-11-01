// Status objects
var current_state  = {},
    current_bucket = { id:-1 };

// GUI Elements
var bucketAllocationChart, 
    attackIdLabel,
    totalPercentage,
    etcLabel,
    numCollisionsLabel,
    heatmap;

$(document).ready(function() {
    build_GUI();    
            
    var webSocket = new WebSocket("ws://localhost:8080/DHBServer/test_initial_config");
    webSocket.onopen = function() {
        webSocket.send("{}");
    };
    
    webSocket.onmessage = function(event) {
        current_state = JSON.parse(event.data);
        load_state();
    };
    

});

function build_GUI() {
    attackIdLabel         = $("#attack-id-label");
    totalPercentage       = $("#total-percentage");
    etcLabel              = $("#etc-label");
    numCollisionsLabel    = $("#num-collisions-label");
    bucketAllocationChart = new BucketAllocationChart('bucket-allocation-chart');
    heatmap               = new BucketsHeatmap('heatmap');
}

function load_state() {
    
    // Load main GUI elements 
    attackIdLabel.text("#" + current_state.idAttack);
    totalPercentage.text('' + current_state.totalPercentage + "%");
    totalPercentage.css('width', '' + Math.max(3, current_state.totalPercentage) + "%");
    etcLabel.text(current_state.etc);
    numCollisionsLabel.text(current_state.numCollisions);

    // Init graphs and buckets stuff
    bucketAllocationChart.init(current_state.numWorkingBuckets, 
                           current_state.numCompletedBuckets, 
                           current_state.numAvailableBuckets);
                           
    heatmap.init(current_state.buckets);
    heatmap.onBucketSelection(function() {
       load_bucket($(this).attr('data-id'));
    });
            
    init_graphs();

}

function load_bucket(id) {
    if(current_bucket.id !== undefined && current_bucket.id == id) { 
        $("#bucket-inspector").slideUp();
        current_bucket = {};
        return;
    }
       
    var webSocket = new WebSocket("ws://localhost:8080/DHBServer/bucket");
    webSocket.onopen = function() {
        var param = {
            id: id
        };
        webSocket.send(JSON.stringify(param));
    };
    
    webSocket.onmessage = function(event) {
        current_bucket = JSON.parse(event.data);

        $("#bucket-id").text("Bucket " + current_bucket.id);
        $("#bucket-progress").css("width", Math.max(3, current_bucket.percentage) + "%");
        $("#bucket-progress").text(current_bucket.percentage + "%");

        var idWorker = (current_bucket.available)?"Not assigned":current_bucket.idWorker;
        var dateAllocation = (current_bucket.available)?"Not assigned":current_bucket.dateAllocation;
        var lastHeartbeat = (current_bucket.available)?"Not assigned":current_bucket.lastHeartbeat;

        $("#bucket-username-label").text(idWorker);
        $("#bucket-allocation-date-label").text(dateAllocation);
        $("#bucket-last-heartbeat-label").text(lastHeartbeat);
        $("#bucket-inspector").slideDown();
    };
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
                fill: false
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
                ]
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
                intersect: false
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