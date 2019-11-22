/* global WS_BUCKET_STATUS_ENDPOINT, WS_ATTACK_STATUS_ENDPOINT, WS_PLAN_ATTACK_ENDPOINT */

// Status objects
var current_bucket = { id:-1 };
var current_status;
var plan_mode = true;
    
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
    
    // Set events and stuff
    $("#plan-form").submit( function(e) {
       test_plan_attack();
       return false;
    });
    
    // Websockets and stuff
    attackStatusWS = new WebSocket(WS_ATTACK_STATUS_ENDPOINT);
    attackStatusWS.onopen = function() {
        // attackStatusWS.send("{}");
        console.log("Subscribed to ws");
    };
    
    attackStatusWS.onmessage = function(event) {
        var state = JSON.parse(event.data);
        
        if(state.error != undefined) {
            // ERROR => ATTACK NOT PLANNED
            set_plan_mode();
            return;
        }
        
        if(plan_mode) set_dashboard_mode();
        Object.keys(state).forEach(function(k){
            current_status.updateStatusVariable(k, state[k]);
        });
           
        update_GUI();
    };
});

function test_plan_attack() {
    var planAttackWS = new WebSocket(WS_PLAN_ATTACK_ENDPOINT);

    planAttackWS.onopen = function() {
        // attackStatusWS.send("{}");
        console.log("Planning a new attack...");
        planAttackWS.send(JSON.stringify({hash: $("#hash-input-form").val()}));
    };
}

function revoke_current_bucket() {
    
    var revoke = confirm("Are you sure you want to revoke the bucket assigned to " + current_bucket.workerNickname + "?");
    
    if(revoke) {
        var revokeWS = new WebSocket(WS_REVOKE_BUCKET_ENDPOINT);
        revokeWS.onopen = function() {
            // attackStatusWS.send("{}");
            console.log("revoking a bucket...");
            revokeWS.send(JSON.stringify({uuid: current_bucket.UUIDWorker}));
        };
    }
}

function set_plan_mode() {
    plan_mode = true;
    $("#dashboard").hide();
    $("#plan").show();
}

function set_dashboard_mode() {
    plan_mode = false;
    $("#plan").hide();
    $("#dashboard").show();
}

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
    totalPercentageBar.text('' + Math.round(current_status.totalPercentage) + "%");
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
    if(current_bucket.id == id) { 
        $("#bucket-inspector").fadeOut();
        current_bucket = {id: -1};
        return;
    }
    
    current_bucket = current_status.buckets[id];
    update_current_bucket();
    $("#bucket-inspector").fadeIn();
    
    // scroll to div (this is going to be lagg af)
    $('html,body').animate({
        scrollTop: $('#bucket-inspector').offset().top
    },'slow');
}

function update_current_bucket() {
    if(current_bucket.id === -1) return;
    
    current_bucket = current_status.buckets[current_bucket.id]; // this thing here makes no sense to me.
    
    bucketIdLabel.text("Bucket " + current_bucket.id);
    bucketProgress.css("width", Math.max(3, current_bucket.percentage) + "%");
    bucketProgress.text(current_bucket.percentage + "%");

    var workerNickname = (current_bucket.available)?"Not assigned":current_bucket.workerNickname;
    var dateAllocation = (current_bucket.available)?"Not assigned":current_bucket.dateAllocation;
    var lastHeartbeat  = (current_bucket.available)?"Not assigned":current_bucket.lastHeartbeat;

    bucketUsernameLabel.text(workerNickname);
    bucketAllocationDateLabel.text(dateAllocation);
    bucketLastHeartbeatLabel.text(lastHeartbeat);

    $("#revoke-btn").toggle(!(current_bucket.available || current_bucket.percentage == 100));
}

function test_chart() {
    var config = {
        type: 'line',
        data: {
            labels: [
                new Date().toLocaleTimeString(), 
                new Date().toLocaleTimeString(),
                new Date().toLocaleTimeString(),
                new Date().toLocaleTimeString(),
                new Date().toLocaleTimeString(),
                new Date().toLocaleTimeString(),
                new Date().toLocaleTimeString()
            ],
            datasets: [{
                fill: false,
                backgroundColor: "#536de6",
                borderColor: "#536de6",
                data: [
                    123, 
                    142,
                    150,
                    20,
                    30,
                    166,
                    12
                ]
            }]
        },
        options: {
            legend: {
                display: false
            },
            aspectRatio: 2,
            responsive: true,
            title: {
                display: true,
                text: 'Inspected Plaintexts [plaintext/s]'
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
                        labelString: 'Time'
                    }
                }],
                yAxes: [{
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: 'plaintext/s'
                    }
                }]
            }
        }
    };
    var ctx = document.getElementById('myChart').getContext('2d');
    var myChart = new Chart(ctx, config);
}