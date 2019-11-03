/* global WS_BUCKET_STATUS_ENDPOINT, WS_ATTACK_STATUS_ENDPOINT */

// Status objects
var current_bucket = { id:-1 };
var current_status;
    
// GUI Elements
var bucketAllocationChart, 
    attackIdLabel,
    totalPercentageBar,
    etcLabel,
    numCollisionsLabel,
    bucketIdLabel,
    bucketProgress,
    bucketUsernameLabel,
    bucketAllocationDateLabel,
    bucketLastHeartbeatLabel,
    heatmap;

// Websockets
var attackStatusWS;

$(document).ready(function() {
    current_status = new AttackStatus();
    build_GUI();    
    
    // Temporary function to show time-plot (performance)
    test_chart();

    attackStatusWS = new WebSocket(WS_ATTACK_STATUS_ENDPOINT);
    attackStatusWS.onopen = function() {
        attackStatusWS.send("{}");
    };
    
    attackStatusWS.onmessage = function(event) {
        var state = JSON.parse(event.data);
        
        Object.keys(state).forEach(function(k){
            current_status.updateStatusVariable(k, state[k]);
        });
           
        update_GUI();
    };
});

function build_GUI() {
    attackIdLabel             = $("#attack-id-label");
    totalPercentageBar        = $("#total-percentage");
    etcLabel                  = $("#etc-label");
    numCollisionsLabel        = $("#num-collisions-label");
    bucketIdLabel             = $("#bucket-id");
    bucketProgress            = $("#bucket-progress");
    bucketUsernameLabel       = $("#bucket-username-label");
    bucketAllocationDateLabel = $("#bucket-allocation-date-label");
    bucketLastHeartbeatLabel  = $("#bucket-last-heartbeat-label");
    
    bucketAllocationChart = new BucketAllocationChart('bucket-allocation-chart');    
    heatmap               = new BucketsHeatmap('heatmap');
}

function update_GUI() {
    // Load main GUI elements 
    attackIdLabel.text("#" + current_status.idAttack);
    totalPercentageBar.text('' + current_status.totalPercentage + "%");
    totalPercentageBar.css('width', '' + Math.max(3, current_status.totalPercentage) + "%");
    etcLabel.text(current_status.etc);
    numCollisionsLabel.text(current_status.numCollisions);

    // Update graphs
    bucketAllocationChart.updateData(current_status.numWorkingBuckets,
                                     current_status.numCompletedBuckets,
                                     current_status.numAvailableBuckets);
    
    // ONLY IF IT'S THE FIRST TIME...
    if(!heatmap.initialized) {
        heatmap.init(current_status.buckets);
        heatmap.onBucketSelection(function() {
           load_bucket($(this).attr('data-id'));
        });
    }
    else {
        // Update the buckets heatmap
        current_status.buckets.forEach(function(b) {
            heatmap.updateBucket(b);
        });
    }
    
    // It might be necessary to update current bucket
    update_current_bucket();
}

function load_bucket(id) {
    // double selection on a bucket makes bucket section disappear!
    if(current_bucket.id !== undefined && current_bucket.id == id) { 
        $("#bucket-inspector").slideUp();
        current_bucket = {};
        return;
    }
    
    current_bucket = current_status.buckets[id];
    update_current_bucket();
    $("#bucket-inspector").slideDown();
}

function update_current_bucket() {
    if(current_bucket === {}) return;
    bucketIdLabel.text("Bucket " + current_bucket.id);
    bucketProgress.css("width", Math.max(3, current_bucket.percentage) + "%");
    bucketProgress.text(current_bucket.percentage + "%");

    var idWorker       = (current_bucket.available)?"Not assigned":current_bucket.idWorker;
    var dateAllocation = (current_bucket.available)?"Not assigned":current_bucket.dateAllocation;
    var lastHeartbeat  = (current_bucket.available)?"Not assigned":current_bucket.lastHeartbeat;

    bucketUsernameLabel.text(idWorker);
    bucketAllocationDateLabel.text(dateAllocation);
    bucketLastHeartbeatLabel.text(lastHeartbeat);
}

function test_chart() {
    var config = {
        type: 'line',
        data: {
            labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
            datasets: [{
                label: 'My First dataset',
                backgroundColor:"#ec407a",
                borderColor:"#ec407a",
                data: [15, 115, 15, 315, 145, 155, 144],
                fill: false
            }, {
                label: 'My Second dataset',
                fill: false,
                backgroundColor: "#536de6",
                borderColor: "#536de6",
                data: [123, 133, 144, 122, 155, 125, 145]
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