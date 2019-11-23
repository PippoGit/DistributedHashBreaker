/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var BucketInspectedChart = function(ctx){
    this._ctx = document.getElementById(ctx).getContext('2d');
    this.lastInspected = 0;
    this._chart = {};
    this.init();
};

BucketInspectedChart.prototype.pushData = function(value) {
    var k_val = Math.round(value/1000);
    
    // Some initial check
    if(k_val == this.lastInspected) return; // this means there was no need to update the chart
    if(this.lastInspected == 0) this.lastInspected = k_val;

    // Add data and labels for the new entry
    var index = this._chart.data.datasets[0].data.push(k_val - this.lastInspected);
    this._chart.data.labels[index-1] = new Date().toLocaleTimeString();
    
    // Scroll the plot if too much data...
    if(this._chart.data.datasets[0].data.length == 10)
    {
        this._chart.data.datasets[0].data.shift(5);
        this._chart.data.labels.shift(5);
    }
    
    // finally update the chart
    this.lastInspected = k_val;
    this._chart.update();
};

BucketInspectedChart.prototype.init = function() {
        var config = {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                fill: false,
                backgroundColor: "#536de6",
                borderColor: "#536de6",
                data: []
            }]
        },
        options: {
            animation: false,
            legend: {
                display: false
            },
            aspectRatio: 2,
            responsive: true,
            title: {
                display: true,
                text: 'Inspected Plaintexts [KPlnTxt/TimeInterval]'
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
                    display: false,
                    scaleLabel: {
                        display: true,
                        labelString: 'Time'
                    }
                }],
                yAxes: [{
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: '# of Plaintext'
                    },
                    ticks: {
                        beginAtZero: true,
                        callback: function(value) {if (value % 1 === 0) {return value;}}
                    }
                }]
            }
        }
    };
    this._chart = new Chart(this._ctx, config);
};

