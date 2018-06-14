import React from 'react'
import {Table, Modal, Icon} from 'antd';
import Lookdetails from "./Lookdetails";
import DerateRepay from "./DerateRepay";
const objectAssign = require('object-assign');
export default React.createClass({
    getInitialState() {
        return {
            selectedRowKeys: [], // 这里配置默认勾选列
            loading: false,
            data: [],
            pagination: {
                pageSize: 10,
                current: 1
            },
            visible: false,
            visible1: false,
            record:"",
            canEdit: true,
        };
    },

    componentWillReceiveProps(nextProps, nextState) {
        this.fetch(nextProps.params);
    },
    componentDidMount() {
        this.fetch();
    },

    fetch(params = {}) {
        this.setState({
            loading: true
        });
        if (!params.pageSize) {
            var params = {};
            params = {
                pageSize: 10,
                current: 1
            }
        }
        Utils.ajaxData({
            url: '/modules/manage/collection/list.htm',
            method: "post",
            data: params,
            callback: (result) => {
                const pagination = this.state.pagination;
                pagination.current = params.current;
                pagination.pageSize = params.pageSize;
                pagination.total = result.page.total;
                if (!pagination.current) {
                    pagination.current = 1
                }
                ;
                this.setState({
                    loading: false,
                    data: result.data,
                    role:result.roleYes,
                    pagination
                });
            }
        });
    },
    //查看弹窗
    showModal(title,record,canEdit) {
    	var record = record;
        var me = this;
        if (title == '确认还款') {
	        me.setState({
	            visible: true,
	            canEdit: canEdit,
	            record: record,
	            title: title,
	        },()=>{
	            this.refs.Lookdetails.setFieldsValue(record);
	        })
        }else if(title == '减免还款'){
        	me.setState({
	            visible1: true,
	            canEdit: canEdit,
	            record: record,
	            title: title,
	        },()=>{
	            this.refs.DerateRepay.setFieldsValue(record);
	        })
        }
    },
    //隐藏弹窗
    hideModal() {
        this.setState({
            visible: false,
            visible1: false,
            visible2: false,
        });
        this.refreshList();
    },
    //分页
    handleTableChange(pagination, filters, sorter) {
        const pager = this.state.pagination;
        pager.current = pagination.current;
        this.setState({
            pagination: pager,
        });
        this.refreshList();
    },

    refreshList() {
        var pagination = this.state.pagination;
        var params = objectAssign({}, this.props.params, {
            current: pagination.current,
            pageSize: pagination.pageSize
        });
        this.fetch(params);
    },
    render() {
        var role = this.state.role;
        let me=this;
        let state=me.state;
        var columns = [{
            title: '真实姓名',
            dataIndex: 'realName'
        }, {
            title: '手机号码',
            dataIndex: "phone",
        }, {
            title: '订单号',
            dataIndex: 'orderNo'
        }, {
            title: '借款金额(元)',
            dataIndex: 'borrowAmount'
        }, {
            title: '逾期罚金(元)',
            dataIndex: 'penaltyAmout'
        },{
            title: '逾期天数',
            dataIndex: "penaltyDay"
        },{
            title: '逾期管理费(元)',
            dataIndex: "penaltyManageAmt"
        }, {
            title: '应还款金额(元)',
            dataIndex: 'repayAmount'
        }, {
            title: '对公还款金额(元)',
            dataIndex: 'money'
        }, {
            title: '应还款日期',
            dataIndex: 'repayTime'
        }, {
            title: '还款状态',
            dataIndex: "repayState",
            render: (text, record)=>{
                if(record.repayState==10){
                    return "已还款"
                }else if(record.repayState==20){
                    return "未还款"
                }else{
                    return "-"
                }
            }
        },{
            title: '还款类型',
            dataIndex: "repayType",
            render: (text, record)=>{
                if(text == '41'){
                    return "部分逾期还款"
                }else if(text == '42'){
                    return "全额逾期还款"
                }else if(text == '43'){
                    return "减免逾期还款"
                }else{
                    return "-"
                }
            }
        },{
            title: '催收员',
            dataIndex: 'applyUserName'
        },{
            title: '催收员提交日期',
            dataIndex: 'applyTime'
        },{
            title: '审批人',
            dataIndex: 'userName'
        },{
            title: '审批日期',
            dataIndex: 'createTime'
        },{
            title: '操作',
            dataIndex: "",
            render: (text,record) => {
            	if (role=="1") {
            		if(record.state == '1'){
                        return "-"
                    }else{
                        return(
                        <div style={{ textAlign: "left" }}>
                        	<a href="#" onClick={me.showModal.bind(me, '确认还款',record, false)}>确认还款</a>                           
	                        &nbsp;&nbsp;
	                        <a href="#" onClick={me.showModal.bind(me, '减免还款',record, false)}>代扣减免还款</a>
                        </div>
                        )
                    }
				}
                
            } 
        }]
        return (
            <div className="block-panel">             
                <Table columns={columns} rowKey={this.rowKey} ref="table" 
                       onRowClick={this.onRowClick}
                       dataSource={this.state.data}
                       pagination={this.state.pagination}
                       onChange={this.handleTableChange}
                />
                <Lookdetails ref="Lookdetails" visible={state.visible} title={state.title} hideModal={me.hideModal} record={state.record}
                canEdit={state.canEdit} />
                <DerateRepay ref="DerateRepay" visible={state.visible1} title={state.title} hideModal={me.hideModal} record={state.record}
                canEdit={state.canEdit} />              
             </div>
        );
    }
})
