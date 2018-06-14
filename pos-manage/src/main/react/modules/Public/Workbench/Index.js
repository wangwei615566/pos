import React from 'react';
import { Row, Col } from 'antd';
import './style.css';
import Map from './Map';
import Bar1 from './Bar1';
import Bar2 from './Bar2';
import Pie1 from './Pie1';
import Pie2 from './Pie2';
var Reflux = require('reflux');
var AppActions = require('../../frame/actions/AppActions');
var UserMessageStore = require('../../frame/stores/UserMessageStore');
export default React.createClass({
    mixins: [
        Reflux.connect(UserMessageStore, "userMessage")
    ],
    getInitialState() {
        return {
            menuData: [],
            assetsData: {},
            investmentData: {},
            loading: false,
            data: [],
            userMessage: {},
        }
    },
    fetch() {
        var me = this;
        this.setState({
            loading: true
        });
        Utils.ajaxData({
            url: '/modules/manage/count/homeInfo.htm',
            method: "get",
            callback: (result) => {
                //console.log(result.data);
                me.setState({
                    loading: false,
                    data: result.data,
                });
            }
        });
    },

    componentDidMount() {
        AppActions.initUserMessageStore();
        this.fetch();
    },
    render() {
        var { data } = this.state;
        var userMessage = this.state.userMessage;
        var roleList = userMessage.roleList;
        var flag = true;
        if(roleList && roleList.length){
        	console.log(roleList.length);
        	for(var item in roleList){
        		if(roleList[item].nid == "lctc"){
        			flag = false;
        			break;
        		}
        	}
        }
        if(flag){
        	return (
                    //如果需要响应时布局就要把minWidth: 1350去掉,加上marginBottom: 16
                    <div  style={{ minWidth: 1350,display: userMessage.name&&userMessage.name!='代理商' ? 'block' : 'none' }}>
                        <div className="block-panel">
                            <h2 className="navLine-title">今日数据</h2>
                            <div className='blk-top'>
                                <div className='blk-top-item'>
                                    <div className='blk-title'>当天注册数</div>
                                    <div className='blk-number'>{data.register}</div>
                                </div>
                                <div className='blk-top-item'>
                                    <div className='blk-title'>登录数</div>
                                    <div className='blk-number'>{data.login}</div>
                                </div>
                                <div className='blk-top-item'>
                                    <div className='blk-title'>借款申请数</div>
                                    <div className='blk-number'>{data.borrow}</div>
                                </div>
                                <div className='blk-top-item'>
                                    <div className='blk-title'>通过次数</div>
                                    <div className='blk-number'>{data.borrowPass}</div>
                                </div>
                                <div className='blk-top-item'>
                                    <div className='blk-title'>通过率</div>
                                    <div className='blk-number'>{data.passApr}<span style={{ fontSize: '12px' }}>% </span></div>
                                </div>
                                <div className='blk-top-item'>
                                    <div className='blk-title'>放款量</div>
                                    <div className='blk-number'>{data.borrowLoan}</div>
                                </div>
                                <div className='blk-top-item' >
                                    <div className='blk-title'>还款量</div>
                                    <div className='blk-number'>{data.borrowRepay}</div>
                                </div>
                                <div className='blk-top-item blk-top-item-last' >
        	                        <div className='blk-title'>逾期笔数</div>
        	                        <div className='blk-number'>{data.overdueRepayUser}</div>
        	                    </div>
                            </div>
                        </div>
                        <div>
                            <div className="data-panel">
                                <div className="block-panel">
                                    <h2 className="navLine-title">累计数据</h2>
                                    <Row>
        	                            <Col span='12'>
        		                            <div className='blk-bottom'>
        		                                <span className='workBench-icon icon1'> </span>
        		                                <span className='blk-title'>总注册量</span>
        		                                <span className='blk-number'>{data.countAllRegister}</span>人
        		                            </div>
        		                        </Col>
                                        <Col span='12'>
                                            <div className='blk-bottom'>
                                                <span className='workBench-icon icon2'> </span>
                                                <span className='blk-title'>历史放款总量</span>
                                                <span className='blk-number'>{data.borrowLoanHistory}</span>笔
                                            </div>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span='12'>
                                            <div className='blk-bottom'>
                                                <span className='workBench-icon icon3'> </span>
                                                <span className='blk-title'>历史还款总量</span>
                                                <span className='blk-number'>{data.borrowRepayHistory}</span>笔
                                            </div>
                                        </Col>
                                        <Col span='12'>
        	                                <div className='blk-bottom'>
        	                                    <span className='workBench-icon icon4'> </span>
        	                                    <span className='blk-title'>历史逾期总量</span>
        	                                    <span className='blk-number'>{data.sumBorrowOverdueRepayAllUser}</span>笔
        	                                </div>
        	                            </Col>
                                    </Row>
                                </div>
                            </div>
                            <div className="data-panel">
                                <div className="block-panel">
                                    <h2 className="navLine-title">实时数据</h2>
                                    <Row>
                                        <Col span='12'>
                                            <div className='blk-bottom'>
                                                <span className='workBench-icon icon5'> </span>
                                                <span className='blk-title'>待还款总余额</span>
                                                <span className='blk-number'>{data.needRepay}</span>元
                                            </div>
                                        </Col>
                                        <Col span='12'>
                                            <div className='blk-bottom'>
                                                <span className='workBench-icon icon6'> </span>
                                                <span className='blk-title'>逾期未还款总额</span>
                                                <span className='blk-number'>{data.overdueRepay}</span>元
                                            </div>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col span='12'>
        	                                <div className='blk-bottom'>
        	                                    <span className='workBench-icon icon7'> </span>
        	                                    <span className='blk-title'>已还款总额</span>
        	                                    <span className='blk-number'>{data.sumAllRepay}</span>元
        	                                </div>
        	                            </Col>
        	                            <Col span='12'>
        		                            <div className='blk-bottom'>
        		                                <span className='workBench-icon icon8'> </span>
        		                                <span className='blk-title'>已放款总额</span>
        		                                <span className='blk-number'>{data.sumAllLoan}</span>元
        		                            </div>
        		                        </Col>	
                                    </Row>
                                </div>
                            </div>
                        </div>
                        <div className="block-chart">
                            <div className='blk-top'>
                                <div className='blk-top-item'>
                                    <Bar1 />
                                </div>
                              
                                <div className='blk-top-item blk-top-item-last'>
                                    <Bar2 />
                                </div>
                            </div>
                        </div>
                    </div>
                )
        }	
        else{
        	return (<div  style={{ minWidth: 1350,display: userMessage.name&&userMessage.name!='代理商' ? 'block' : 'none' }}></div>)
        }
        
    }
});