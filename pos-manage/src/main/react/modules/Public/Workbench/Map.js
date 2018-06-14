import React from 'react';
var echarts = require('echarts');
require('./china');
require("echarts/theme/macarons.js");
export default React.createClass({
    getInitialState() {
        return {
            menuData: [],
            assetsData: {},
            investmentData: {},
            loading: false,
            monthBorrowAmt: [],
            monthBorrowCount: [],
            monthRegister: [],
            monthBorrowRepay: []
        }
    },
    fetch() {
        var me = this;
        this.setState({
            loading: true
        });
        Utils.ajaxData({
            url: '/modules/manage/count/userMonthLoanByArea.htm',
            method: "get",
            callback: (result) => {
                var json1 = [];
                var json2 = [];
                var json3 = [];
                var json4 = [];
                for (let item1 in result.data.monthBorrowAmt) {
                    json1.push({ 'name': item1, 'value': result.data.monthBorrowAmt[item1] });
                }
                for (let item2 in result.data.monthBorrowCount) {
                    json2.push({ 'name': item2, 'value': result.data.monthBorrowCount[item2] });
                }
                for (let item3 in result.data.monthRegister) {
                    json3.push({ 'name': item3, 'value': result.data.monthRegister[item3] });
                }
                for (let item4 in result.data.monthBorrowRepay) {
                    json4.push({ 'name': item4, 'value': result.data.monthBorrowRepay[item4] });
                }
                me.setState({
                    loading: false,
                    monthBorrowAmt: json1,
                    monthBorrowCount: json2,
                    monthRegister: json3,
                    monthBorrowRepay: json4
                });
                me.drawMap();

            }
        });
    },
    drawMap() {
        var me = this;
        var map = echarts.init(document.getElementById('map'), 'macarons');

        var mapOption = {
            title: {
                text: '用户地域分布',
                subtext: '',
                left: 'center',
                y: 10,
                textStyle: {
                    color: '#666',
                    fontWeight: 'normal'
                }
            },
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    var province = params.name;
                    if (!province) {
                        province = '南沙群岛';
                    }
                    var res = province + '</br>';
                    var series = mapOption.series;
                    var monad;
                    for (var j = 0; j < series.length; j++) {
                        for (var i = 0; i < series[j].data.length; i++) {
                            if (series[j].data[i].name == province) {
                                switch (series[j].name) {
                                    case '当月融资金额': monad = '元'; break;
                                    case '当月借款笔数': monad = '笔'; break;
                                    case '当月新增用户': monad = '个'; break;
                                    case '当月还款金额': monad = '元'; break;
                                    default: monad = ''; break;
                                }
                                res += series[j].name + ': ' + (series[j].data[i] && series[j].data[i].value ? series[j].data[i].value : 0) + monad + '<br/>';
                            }
                        }
                    }
                    return res;
                },
                textStyle: {
                    align: 'left',  
                }
            },
            legend: {
                orient: 'vertical',
                left: '20',
                top: '20',
                data: ['当月融资金额', '当月借款笔数', '当月新增用户', '当月还款金额']
            },
            toolbox: {
                show: true,
                orient: 'horizontal',
                x: '85%',
                y: 'buttom',
                itemGap: 20,   
                itemSize: 20, 
                feature: {
                    restore: {},
                    saveAsImage: {}
                }
            },
			 textStyle:{
             	fontSize:'11',
             },
            series: [
                {
                    name: '当月融资金额',
                    type: 'map',
                    mapType: 'china',
                    mapLocation: {
                        x : 'center',
                        y : 'center',
                        width:'670',
                        height:'420'
                    },
                    roam: false,
                    label: {
                        normal: {
                            show: true,                      
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    data: me.state.monthBorrowAmt
                },
                {
                    name: '当月借款笔数',
                    type: 'map',
                    mapType: 'china',
                    label: {
                        normal: {
                            show: true
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    data: me.state.monthBorrowCount
                },
                {
                    name: '当月新增用户',
                    type: 'map',
                    mapType: 'china',
                    label: {
                        normal: {
                            show: true
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    data: me.state.monthRegister
                },
                {
                    name: '当月还款金额',
                    type: 'map',
                    mapType: 'china',
                    label: {
                        normal: {
                            show: true
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    data: me.state.monthBorrowRepay
                }
            ]
        };
        map.setOption(mapOption);
    },
    componentDidMount() {
        this.fetch();
    },
    render() {
        return <div id="map" style={{ height: '480px', width: '750px', margin: '0 auto'}}></div>
    }
});