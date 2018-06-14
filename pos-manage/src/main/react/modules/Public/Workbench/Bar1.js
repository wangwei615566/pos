import React from 'react';
var echarts = require('echarts');
require("echarts/theme/macarons.js");
export default React.createClass({
	getInitialState() {
        return {
            menuData: [],
            assetsData: {},
            investmentData: {},
            loading: false,
            data: [],
			item: [],
        }
    },
	fetch() {
        var me = this;
        this.setState({
            loading: true
        });
        Utils.ajaxData({
            url: '/modules/manage/count/fifteenDaysLoan.htm',
            method: "get",
            callback: (result) => {
				var data = [];
				var item1 = [];
				var item2 = []
				for(let item in result.data.fifteenDaysLoan){
					item1.push(item.substring(5));
					item2.push(item);
				}
				for(var i = 0; i < item1.length-1; i++){
					for(var j = i+1; j < item1.length; j++){
						if(item1[i] > item1[j]){
							var z = item1[i];
							item1[i] = item1[j];
							item1[j] = z;
							var q = item2[i];
                            item2[i] = item2[j];
							item2[j] = q;
						}
					}
				}
				for(var i = 0; i < item1.length; i++){
					data.push(result.data.fifteenDaysLoan[item2[i]]);
				}
				me.setState({
                    	loading: false,
                    	data: data,
						item: item1
                });
				this.drawBar();
               
            }
        });
    },
	drawBar() {
		var me = this;
		var bar = echarts.init(document.getElementById('bar'),'macarons');
		var option = {
			title: {
				text: '每天放款量',
				x: 'center',
				y: 10,
				textStyle: {
					color: '#666',
					fontWeight: 'normal'
				}
			},
			tooltip: {
				trigger: 'axis',
				formatter: '{a} <br/>{b}: {c}笔',
				axisPointer: {            // 坐标轴指示器，坐标轴触发有效
					type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			barWidth: '12',
			legend: {
				orient: 'horizontal',
				x: 'right',
				y: 20,
				itemGap: 20,
				data: ['每天放款量']
			},
			grid: {
				left: '3%',
				right: '4%',
				bottom: '3%',
				containLabel: true
			},
			xAxis: [
				{
					type: 'category',
					data: me.state.item
				}
			],
			yAxis: {
				type: 'value',
				splitLine: { show: false },
			},
			series: [
				{
					name: '每天放款量',
					type: 'bar',
					data: me.state.data
				},
			]
		};
		bar.setOption(option);
	},
	componentDidMount() {
		this.fetch();
	},
	render() {
		return <div id="bar" style={{ height: '340px', width: '610px', margin: '0 auto'  }}></div>
	}
});