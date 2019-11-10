/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var BucketsHeatmap = function(id){
    this._map        = $("#" + id);
    this.initialized = false;
};

BucketsHeatmap.prototype.init = function(buckets) {
    var that = this;
    this.initialized = true;
    
    buckets.forEach( function(b) {
        var bucketClass = that.getBucketStyle(b);
        that._map.append("<div data-id='" + b.id + "' class='" + bucketClass + "' id='bucket-" + b.id + "' >" + b.id +  "</div>");
    });
};

BucketsHeatmap.prototype.onBucketSelection = function(fun) {
    this._map.on("click", "div", fun);
};

BucketsHeatmap.prototype.getBucketStyle = function(bucket) {
    var percentage = bucket.percentage;
    var bucket_style = ['bucket-l-25', 
                        'bucket-m-25',
                        'bucket-m-50',
                        'bucket-m-75'];

    if(percentage === 100) return "completed-bucket";
    else if (bucket.available) return "available-bucket";
    return bucket_style[Math.floor(percentage/25)];
};

BucketsHeatmap.prototype.updateBucket = function(bucket) {
    $("#bucket-"+bucket.id).attr('class', this.getBucketStyle(bucket));
};