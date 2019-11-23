/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var BucketAllocationChart = function(ctx){
    this._ctx = document.getElementById(ctx).getContext('2d');
    this._chart = {};
    this.numWorkingBuckets   = 0;
    this.numCompletedBuckets = 0;
    this.numAvailableBuckets = 0;
    this.init();
};

BucketAllocationChart.prototype.updateData = function(nw, nc, na) {
    this.numWorkingBuckets   = (nw === undefined)?this.numWorkingBuckets  :nw;
    this.numCompletedBuckets = (nc === undefined)?this.numCompletedBuckets:nc;
    this.numAvailableBuckets = (na === undefined)?this.numAvailableBuckets:na;

    this._chart.data.datasets[0].data = [this.numWorkingBuckets,
                                         this.numCompletedBuckets,
                                         this.numAvailableBuckets];
    this._chart.update();
};

BucketAllocationChart.prototype.init = function() {
    this._chart = new Chart(this._ctx, {
        type: 'doughnut',
        data: {
            datasets: [{
                data : [0, 0, 0],
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

