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
    var index = this._chart.data.datasets[0].data.push(value - this.lastInspected);
    this._chart.data.labels[index-1] = new Date().toLocaleTimeString();
    
    if(this._chart.data.datasets[0].data.length == 20) // ??? idk
    {
        // should remove first entry and stuff...
        this._chart.data.datasets[0].data.shift(15);
        this._chart.data.labels.shift(15);
    }
    this.lastInspected = value;
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
                        labelString: 'plaintext/s'
                    }
                }]
            }
        }
    };
    this._chart = new Chart(this._ctx, config);
};

