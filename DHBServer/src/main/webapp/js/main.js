/* global WS_BUCKET_STATUS_ENDPOINT, WS_ATTACK_STATUS_ENDPOINT */

// Status objects
var current_bucket = { id:-1 };
    
var idAttack,
    totalPercentage,
    numCollisions,
    etc,
    numAvailableBuckets,
    numWorkingBuckets,
    numCompletedBuckets,
    buckets;
    

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
var attackStatusWS,
    bucketStatusWS; // BUCKET STATUS WS IS USELESS!!!!!!

$(document).ready(function() {
    build_GUI();    
            
    attackStatusWS = new WebSocket(WS_ATTACK_STATUS_ENDPOINT);
    attackStatusWS.onopen = function() {
        attackStatusWS.send("{}");
    };
    
    attackStatusWS.onmessage = function(event) {
        var state = JSON.parse(event.data);
        
        idAttack            = state.idAttack;
        totalPercentage     = state.totalPercentage;
        numCollisions       = state.numCollisions;
        etc                 = state.etc;
        
        numAvailableBuckets = state.numAvailableBuckets;
        numWorkingBuckets   = state.numWorkingBuckets;
        numCompletedBuckets = state.numCompletedBuckets;
        buckets             = state.buckets;
           
        load_state();
    };
    
    
    /*
    bucketStatusWS = new WebSocket(WS_BUCKET_STATUS_ENDPOINT);    
    bucketStatusWS.onmessage = function(event) {
        update_current_bucket(JSON.parse(event.data));
        $("#bucket-inspector").slideDown();
    };
    */
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

function load_state() {
    
    // Load main GUI elements 
    attackIdLabel.text("#" + idAttack);
    totalPercentageBar.text('' + totalPercentage + "%");
    totalPercentageBar.css('width', '' + Math.max(3, totalPercentage) + "%");
    etcLabel.text(etc);
    numCollisionsLabel.text(numCollisions);

    // Init graphs and buckets stuff
    bucketAllocationChart.init(numWorkingBuckets, 
                               numCompletedBuckets, 
                               numAvailableBuckets);
                           
    heatmap.init(buckets);
    heatmap.onBucketSelection(function() {
       load_bucket($(this).attr('data-id'));
    });
            
    init_graphs();

}

function load_bucket(id) {
    // double selection on a bucket makes bucket section disappear!
    if(current_bucket.id !== undefined && current_bucket.id == id) { 
        $("#bucket-inspector").slideUp();
        current_bucket = {};
        return;
    }
    update_current_bucket(buckets[id]);
    $("#bucket-inspector").slideDown();

}

function update_current_bucket(bucket) {
    current_bucket = bucket;
    bucketIdLabel.text("Bucket " + current_bucket.id);
    bucketProgress.css("width", Math.max(3, current_bucket.percentage) + "%");
    bucketProgress.text(current_bucket.percentage + "%");

    var idWorker = (current_bucket.available)?"Not assigned":current_bucket.idWorker;
    var dateAllocation = (current_bucket.available)?"Not assigned":current_bucket.dateAllocation;
    var lastHeartbeat = (current_bucket.available)?"Not assigned":current_bucket.lastHeartbeat;

    bucketUsernameLabel.text(idWorker);
    bucketAllocationDateLabel.text(dateAllocation);
    bucketLastHeartbeatLabel.text(lastHeartbeat);
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