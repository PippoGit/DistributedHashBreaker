/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var BucketAllocationChart = function(ctx){
    this._ctx = document.getElementById(ctx).getContext('2d');
    this. _chart;
    this.numWorkingBuckets   = 0;
    this.numCompletedBuckets = 0;
    this.numAvailableBuckets = 0;
};

BucketAllocationChart.prototype.init = function(nw, nc, na) {
    this.numWorkingBuckets   = nw;
    this.numCompletedBuckets = nc;
    this.numAvailableBuckets = na;

    this._chart = new Chart(this._ctx, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [nw, nc, na],
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
};

BucketAllocationChart.prototype.updateNumWorkingBuckets = function(num) {
    console.log(num);
    this.numWorkingBuckets = num;
};

BucketAllocationChart.prototype.updateNumCompletedBuckets = function(num) {
    console.log(num);
    this.numCompletedBuckets = num;
};

BucketAllocationChart.prototype.updateNumAvailableBuckets = function(num) {
    console.log(num);
    this.numAvailableBuckets = num;
};

