import React from 'react'
import {Table, Modal, Icon} from 'antd';
import Lookdetails from "./Lookdetails";
import DerateRepay from "./DerateRepay";
const confirm = Modal.confirm;
var repaymentTypeText={'10':'待审核', '20': '审核中' ,'30': '通过','40' :'已拒绝' ,'50': '还款中', '60' :'还款完成', '70': '逾期'}
const objectAssign = require('object-assign');
import AddWin from "./AddWin";
export default React.createClass({
    getInitialState() {
        return {
            selectedRowKeys: [], // 这里配置默认勾选列
            loading: false,
            data: [],
            repayCount:[],
            pagination: {
                pageSize: 10,
                current: 1
            },
            canEdit: true,
            visible: false,
            visible1: false,
            visible2: false,
            pictureData: [],
            creditReportData: [],
            rowRecord:[],
            record:"",
            visibleAdd:false,

        };
    },

    componentWillReceiveProps(nextProps, nextState) {
        this.fetch(nextProps.params);
    },

    componentDidMount() {
        this.fetch();
        []
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
            url: '/modules/manage/borrow/repay/list.htm',
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
                    repayCount:result.repayCount,
                    pagination
                });
            }
        });
    },

    //查看弹窗
    showModal(title,record, canEdit) {
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
    //新增
    addModal(title, record, canEdit){
        this.setState({
            visibleAdd:true,
            title:title,  
        })
    },
    //代扣还款
    doRepay(title, record) {
        var record = record;
        confirm({
            title: "是否确定代扣还款",
            onOk: function () {
                Utils.ajaxData({
                    url: '/modules/manage/borrow/repay/doRepay.htm',
                    method: "post",
                    data: {
                        repayId:record.id
                    },
                    callback:(result) => {
                        if(result.code == 200){
                            Modal.success({
                                title:result.msg
                            })
                        }else{
                            Modal.error({
                                title:result.msg
                            })
                        }
                    }
                })
            },
            onCancel: function () { }
        })
    },
    //隐藏弹窗
    hideModal() {
        this.setState({
            visible: false,
            visible1: false,
            visible2: false,
            selectedIndex: '',
            selectedRowKeys: [],
            visibleAdd:false
        });
        this.refreshList();
    },
    rowKey(record) {
        return record.id;
    },

    //选择
    onSelectChange(selectedRowKeys) {
        this.setState({
            selectedRowKeys
        });
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

    //行点击事件
    onRowClick(record) {
        //console.log(record)
        this.setState({
            selectedRowKeys: [record.id],
            selectedRow: record,
            rowRecord:record
        },()=>{
            this
        });
    },

    // 列表添加选中行样式
    rowClassName(record) {
        let selected = this.state.selectedIndex;
        //console.log('selected', this.state.selectedIndex)
        return (record.id == selected && selected !== '') ? 'selectRow' : '';

    },

    //选择
    onSelectAll(selected, selectedRowKeys, selectedRows, changeRows) {
        if (selected) {
            this.setState({
                selectedRowKeys
            })
        } else {
            this.setState({
                selectedRowKeys: []
            })
        }
    },

    render() {
        const {
            loading,
            selectedRowKeys
            } = this.state;
        const rowSelection = {
            //type: 'checkbox',
            selectedRowKeys,
            //onSelectAll: this.onSelectAll,
        };
        var role = this.state.role;
        let me=this;
        const hasSelected = selectedRowKeys.length > 0;
        let openEdit = true;
        if (hasSelected && selectedRowKeys.indexOf("0") === -1) {
            openEdit = false;
        }
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
            title: '应还款日期',
            dataIndex: 'repayTime'
        }, {
            title: '还款状态',
            dataIndex: "state",
            render: (text, record)=>{
                if(record.state==10){
                    return "已还款"
                }else if(record.state==20){
                    return "未还款"
                }else{
                    return "-"
                }
            }
        }, {
            title: '经办人',
            dataIndex: 'operator'
        }, {
            title: '访问码',
            dataIndex: 'accessCode'
        },{
            title: '操作',
            dataIndex: "",
            render: (text,record) => {
            	if (role=="1") {
            		if(record.state == 10){
                        return "-"
                    }else{
                        return(
                        <div style={{ textAlign: "left" }}>
                                <a href="#" onClick={me.showModal.bind(me, '确认还款',record, false)}>确认还款</a>
                                &nbsp;&nbsp;
                                <a href="#" onClick={me.doRepay.bind(me, '代扣还款',record)}>代扣还款</a>
                                &nbsp;&nbsp;
                                <a href="#" onClick={me.showModal.bind(me, '减免还款',record, false)}>代扣减免还款</a>
                        </div>
                        
                        )
                    }
				}
                
            } 
        }]
        //console.log(this.state.repayCount);
        var state = this.state;
        var repayCountList=[];
        if (this.state.repayCount) {
        	this.state.repayCount.map(item=>{
        		if (item.state=='10') {
					item.state="已还款";
				}else if (item.state=='20') {
					item.state="未还款";
				}
        		repayCountList.push(<div style={{color:"blue"}} value={item.count} key={item.state}>{item.state}：{item.count}  </div>)
        	})
		}
        return (
            <div className="block-panel">
               {/* <div className="actionBtns" style={{ marginBottom: 16 }}>
                    <button onClick={me.addModal.bind(me,'批量')} className="ant-btn"> 
                        批量还款
                    </button>
                </div> */}
                <Table columns={columns} rowKey={this.rowKey} ref="table" 
                       onRowClick={this.onRowClick}
                       dataSource={this.state.data}
                       rowClassName={this.rowClassName}
                       pagination={this.state.pagination}
                       onChange={this.handleTableChange}
                />
                {repayCountList}
                <Lookdetails ref="Lookdetails" visible={state.visible} title={state.title} hideModal={me.hideModal} record={state.record}
                canEdit={state.canEdit} />
                <AddWin ref="AddWin"  visible={state.visibleAdd} hideModal={me.hideModal} title={state.title}/>
                <DerateRepay ref="DerateRepay" visible={state.visible1} title={state.title} hideModal={me.hideModal} record={state.record}
                canEdit={state.canEdit} />
             </div>
        );
    }
})
