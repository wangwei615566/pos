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
            item:[],
            value:[],
        }
    },
	fetch() {
        var me = this;
        this.setState({
            loading: true
        });
        Utils.ajaxData({
            url: '/modules/manage/count/repaySource.htm',
            method: "get",
            callback: (result) => {
				console.log(result.data);
				var rs = {};
                for (var o in result.data.repaySource) {
                	console.log("test :" + result.data.repaySource[o]);
                	for (var k in result.data.repaySource[o]) {
                		console.log("key: " +  k + "val :" + result.data.repaySource[o][k]);
                		rs[k] = result.data.repaySource[o][k];
                	}
                }
                //console.log(rs);
                //console.log(result.data.repaySource[0]['自动代扣']);
                //console.log(result.data.repaySource[1]['银行卡转账']);
                //console.log(result.data.repaySource[2]['支付宝转账']);
                //console.log(result.data.repaySource[3]['其它']);
				me.setState({
                    	loading: false,
                    	value1:rs['自动代扣'],
                        value2:rs['银行卡对公转账'],
                        value3:rs['支付宝对公转账'],
                        value4:rs['快捷支付'],
                        value5:rs['聚合支付'],
//                        value1:result.data.repaySource[0]['自动代扣'],
//                        value2:result.data.repaySource[1]['银行卡对公转账'],
//                        value3:result.data.repaySource[2]['支付宝对公转账'],
//                        value4:result.data.repaySource[3]['快捷支付'],
                });
				me.drawPie();
               
            }
        });
    },
    drawPie() {
        var me = this
        var pie = echarts.init(document.getElementById('pie2'),'macarons');
        var option = {
            title: {
                text: '还款方式',
                x: 400,
				y:20,
                textStyle: {
                    color: '#666',
                }
            },
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c}笔 ({d}%)"
            },
            legend: {
                orient: 'vertical',
                x: 400,
				top:'30%', 
                data: ['自动代扣','银行卡对公转账','支付宝对公转账','快捷支付','聚合支付']
            },
            series: [{
                name: '还款方式',
                type: 'pie',
                center:['40%','50%'],
                radius: ['35%', '65%'],
                itemStyle: {
                    normal: {
                        label: {
                            position: 'outer',
                            formatter: function (params,data) {
                                return (params.percent - 0).toFixed(0) + '%'
                                // return params.value1
                            }
                        },
                        labelLine: {
                            show: true
                        }
                    },
                },
                data: [{
                    value: me.state.value1,
                    name: '自动代扣'
                }, {
                    value: me.state.value2,
                    name: '银行卡对公转账'
                }, {
                    value: me.state.value3,
                    name: '支付宝对公转账'
                },{
                    value:me.state.value4,
                    name:'快捷支付'
                },{
                    value:me.state.value5,
                    name:'聚合支付'
                }]
            }]
        };
        pie.setOption(option);
    },
    componentDidMount() {
        this.fetch();
    },
    render() {
        return <div id="pie2" style={{ height: '280px', width: '600px', margin: '0 auto'  }}></div>
    }
});