$(window).load(function() {
	renderChart1();
	renderChart2();
	
	//$('#portal').portal({
		//fit:true
	//});
	$(".loader").fadeOut("slow");
});

function renderChart1() {
	var dateFormat = 'MMMM DD YYYY';	
	$.ajax({
		type: "GET",
        url: '/search/logs/search_summary.json',
        success: function(inData) {
        	data = [inData.length];
        	labels = [inData.length];
            for(i=0; i < inData.length; i++) {
            	data[i] = inData[i].count;
            	labels[i] = inData[i].timestamp;
            }
            var ctx = document.getElementById("chart1").getContext("2d");
        	//ctx.canvas.width = 400;
        	//ctx.canvas.height = 500;
        	var cfg = {
        		type: 'bar',
        		data: {
        			labels: labels,
        			datasets: [{
        				label: "Total Searches",
        				data: data,
        				type: 'bar',
        				pointRadius: 0,
        				fill: false,
        				lineTension: 0,
        				borderWidth: 2
        			}]
        		},
        		options: {        			
        			scales: {
        				xAxes: [{        					
        					distribution: 'series',
        					ticks: {
        						source: 'labels'
        					}
        				}],
        				yAxes: [{
        					scaleLabel: {
        						display: true,
        						labelString: 'Daily Total Searches Performed'
        					},
        					ticks: {
        				        beginAtZero: true,
        				        callback: function(value) {return value;}
        				    }
        				}]
        			}
        		}
        	};
        	var chart = new Chart(ctx, cfg);
        }
    });	
}
function renderChart2() {
	var dateFormat = 'MMMM DD YYYY';	
	$.ajax({
		type: "GET",
        url: '/search/logs/view_summary.json',
        success: function(inData) {
        	data = [inData.length];
        	labels = [inData.length];
            for(i=0; i < inData.length; i++) {
            	data[i] = inData[i].count;
            	labels[i] = inData[i].timestamp;
            }
            var ctx = document.getElementById("chart2").getContext("2d");
        	//ctx.canvas.width = 400;
        	//ctx.canvas.height = 500;
        	var cfg = {
        		type: 'bar',
        		data: {
        			labels: labels,
        			datasets: [{
        				label: "Total Page Views",
        				data: data,
        				type: 'bar',
        				pointRadius: 0,
        				fill: false,
        				lineTension: 0,
        				borderWidth: 2
        			}]
        		},
        		options: {        			
        			scales: {
        				xAxes: [{        					
        					distribution: 'series',
        					ticks: {
        						source: 'labels'
        					}
        				}],
        				yAxes: [{
        					scaleLabel: {
        						display: true,
        						labelString: 'Daily Total Page Views'
        					},
        					ticks: {
        				        beginAtZero: true,
        				        callback: function(value) {return value;}
        				    }
        				}]
        			}
        		}
        	};
        	var chart = new Chart(ctx, cfg);
        }
    });	
}