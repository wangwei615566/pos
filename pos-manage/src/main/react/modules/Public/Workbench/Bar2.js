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
            data1: [],
            item: [],
            data2: [],
            data3: [],
            max: 1,
        }
    },
    fetch() {
        var me = this;
        this.setState({
            loading: true
        });
        Utils.ajaxData({
            url: '/modules/manage/count/fifteenDaysNeedAndRealRepay.htm',
            method: "get",
            callback: (result) => {
                var data = [];
                var data1 = [];
                var data2 = [];
                var data3 = [];
                var item1 = [];
                var item2 = [];
                var max = 0;
                for (let item in result.data.fifteenDaysRealRepay) {
                    item1.push(item.substring(5));
                    item2.push(item);
                }
                for (var i = 0; i < item1.length - 1; i++) {
                    for (var j = i + 1; j < item1.length; j++) {
                        if (item1[i] > item1[j]) {
                            var z = item1[i];
                            item1[i] = item1[j];
                            item1[j] = z;
                            var q = item2[i];
                            item2[i] = item2[j];
                            item2[j] = q;
                        }
                    }
                    data.push(result.data.fifteenDaysNeedRepay[item2[i]]);
                    data1.push(result.data.fifteenDaysTotalRepay[item2[i]]);
                    data2.push((parseFloat(result.data.fifteenDaysOverdueApr[item2[i]]) * 100).toFixed(2));
                    data3.push(result.data.fifteenDaysRealRepay[item2[i]]);
                }
                data.push(result.data.fifteenDaysNeedRepay[item2[item1.length - 1]]);
                data1.push(result.data.fifteenDaysTotalRepay[item2[item1.length - 1]]);
                data2.push((parseFloat(result.data.fifteenDaysOverdueApr[item2[item1.length - 1]])*100).toFixed(2));
                data3.push(result.data.fifteenDaysRealRepay[item2[item1.length - 1]]);
                //console.log(data);
                //console.log(data1);
                //console.log(data2);
                for (var i = 0; i < item1.length; i++) {
                    if (max < data[i]) {
                        max = data[i];
                    }
                    if (max < data1[i]) {
                        max = data1[i];
                    }
                    if (max < data3[i]) {
                    	max = data3[i];
                    }
                }
                //console.log(max);
                //console.log(item1);
                //console.log(data);
                //console.log(data1);
                me.setState({
                    loading: false,
                    data: data,
                    data1: data1,
                    data2: data2,
                    data3:data3,
                    item: item1,
                    max: max
                });
                me.drawBar();

            }
        });
    },
    drawBar() {
        var me = this;
        var bar = echarts.init(document.getElementById('bar2'), 'macarons');
        var option = {
            title: {
                text: '还款量和逾期率',
                x: 'center',
                y: -4,
                textStyle: {
                    color: '#666',
                    fontWeight: 'normal',
                }
            },
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    var xAxis = option.xAxis;
                    var res = params[0].name + "<br/>"
                    var series = option.series;
                    for (var i = 0; i < xAxis[0].data.length; i++) {
                        if(xAxis[0].data[i] == params[0].name){
                            res += series[0].name + ': ' + series[0].data[i] + '笔<br/>' + series[1].name + ': ' + series[1].data[i] + '笔<br/>' + series[3].name + ': ' + series[3].data[i] + '%<br/>' + series[2].name + ': ' + series[2].data[i] + '笔<br/>'
                        }
                    }
                    return res;
                },
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                },
                textStyle: {
                    align: 'left'
                }
            },
            barWidth: '12',
            legend: {
                orient: 'horizontal',
                x: 'center',
                y: 20,
                itemGap: 12,
                data: ['当日应还款量', '当日还款量','当日总还款量','逾期率']
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
            yAxis: [
                {
                    type: 'value',
                    splitLine: { show: false },
                    scale: true,
                    name: '还款量',
                    max: me.state.max % 2 == 0 ? me.state.max : me.state.max + 1,
                    min: 0,
                },
                {
                    type: 'value',
                    splitLine: { show: false },
                    scale: true,
                    name: '逾期率(%)',
                    max: 100,
                    min: 0,
                }
            ],
            series: [
                {
                    name: '当日应还款量',
                    type: 'bar',
                    data: me.state.data
                },
                {
                	name: '当日还款量',
                	type: 'bar',
                	data: me.state.data3
                },
                {
                    name: '当日总还款量',
                    type: 'bar',
                    stack: '广告',
                    data: me.state.data1
                },
                {
                    name: '逾期率',
                    type: 'line',
                    yAxisIndex: 1,
                    data: me.state.data2
                }
            ]
        };
        bar.setOption(option);
    },
    componentDidMount() {
        this.fetch();
    },
    render() {
        return <div id="bar2" style={{ height: '360px', width: '610px', margin: '0 auto'}}></div>
    }
});