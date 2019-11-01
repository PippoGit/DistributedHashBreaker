/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var BucketsHeatmap = function(id){
    this._map    = $("#" + id);
    this.buckets = [];
};

BucketsHeatmap.prototype.init = function(buckets) {
    this.buckets = buckets;
    var that = this;
    
    this.buckets.forEach( function(b) {
        var bucketClass = that.getBucketStyle(b.id);
        that._map.append("<div data-id='" + b.id + "' class='" + bucketClass + "' id='bucket-" + b.id + "' >" + b.id +  "</div>");
    });
};

BucketsHeatmap.prototype.onBucketSelection = function(fun) {
    this._map.on("click", "div", fun);
};

BucketsHeatmap.prototype.getBucketStyle = function(id) {
    var percentage = this.buckets[id].percentage;
    var bucket_style = ['bucket-l-25', 
                        'bucket-m-25',
                        'bucket-m-50',
                        'bucket-m-75'];

    if(percentage === 100) return "completed-bucket";
    else if (this.buckets[id].available) return "available-bucket";
    return bucket_style[Math.floor(percentage/25)];
};