/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var AttackStatus = function() {
    this.idAttack;
    this.totalPercentage;
    this.numCollisions;
    this.etc;
    this.numAvailableBuckets;
    this.numWorkingBuckets;
    this.numCompletedBuckets;
    this.buckets;
};

AttackStatus.prototype.updateStatusVariable = function(variable, data) {
    this[variable] = (data === undefined)?this.variable:data;
};
