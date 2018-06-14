import React from 'react'
import { Table, Modal, Icon } from 'antd';
import ReviewWin from './ReviewWin';
var repaymentTypeText = { '10': '待审核', '20': '审核中', '30': '通过', '40': '已拒绝', '50': '还款中', '60': '还款完成', '70': '逾期' }
const objectAssign = require('object-assign');
const confirm = Modal.confirm;
import AddWin from "./AddWin";
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
            canEdit: true,
            visible: false,
            visible1: false,
            visible2: false,
            pictureData: [],
            creditReportData: [],
            rowRecord: [],
            record: "",
            visibleAdd: false,
            initTime:'0',
        };
    },

    componentWillReceiveProps(nextProps, nextState) {
        this.fetch(nextProps.params);
        this.setState({
            initTime:'1',
        });
    },

    componentDidMount() {
        this.fetch();
        []
    },

    fetch(params = {}) {
        this.setState({
            loading: true
        });

        var year, month, day;
        var year1, month1, day1;
        var date = new Date();
        if(date.getDate() < 7 ){
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1;
            day1 = date.getDate();
            switch(month1){
                case 1:
                    year = year1-1;
                    month = 12;
                    day = day1+25;
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                    year = year1;
                    month = month1<10 ? '0'+(month1-1) : (month1-1);
                    day = day1+25;
                    break;
                case 3:
                    year = year1;
                    month = '0'+(month1-1);
                    day = year%4==0&&year%100!=0||year%400==0 ? day1+23 : day1+22;
                    break;
                case 5:
                case 7:
                case 10:
                case 12:
                    year = year1;
                    month = month1<=10 ? '0'+(month1-1) : (month1-1);
                    day = day1+24;
                    break;
            }

            month1 = month1<10?'0'+month1 : month1;
        }else{
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1<10 ? '0'+(date.getMonth()+1) : date.getMonth()+1;
            day1 = date.getDate();
            year = year1;
            month = month1;
            day = day1 < 16 ? '0'+(day1-6) : day1-6;

        };

        if (!params.pageSize) {
            var params = {};
            params = {
                pageSize: 10,
                current: 1,
                searchParams: JSON.stringify({startDate:year+'-'+month+'-'+day+' 00:00:00',endDate:year1+'-'+month1+'-'+day1+' 23:59:59',type:'repay'}),
            }
        }
        if(!params.searchParams){
            params.searchParams= JSON.stringify({type:"repay"});
        }
        Utils.ajaxData({
            url: '/modules/manage/borrow/borrowRepayList.htm',
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
                    pagination
                });
            }
        });
    },

    //查看弹窗
    showModal(title, record, canEdit) {
        var this2 = this;
        this.setState({
            visible: true,
            canEdit: canEdit,
            record: record,
            title: title + "【渠道来源："+record.channelName+"】",
        }, () => {
            Utils.ajaxData({
                url: '/modules/manage/borrow/borrowRepayContent.htm',
                data: {
                    borrowId: record.id
                },
                method: "post",
                callback: (result) => {
                    this.refs.ReviewWin.refs.Lookdetails.setFieldsValue(result.data);
                    this.refs.ReviewWin.state.dataRecord = result.data;
                    this2.setState({
                        dataRecord: result.data,
                    },function(){
//                    	console.log(this2.state.dataRecord);
                    });

                }
            });

        })
    },
    //新增
    addModal(title, record, canEdit) {
        this.setState({
            visibleAdd: true,
            title: title,
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
            visibleAdd: false
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

        var year, month, day;
        var year1, month1, day1;
        var date = new Date();
        if(date.getDate() < 7 ){
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1;
            day1 = date.getDate();
            switch(month1){
                case 1:
                    year = year1-1;
                    month = 12;
                    day = day1+25;
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                    year = year1;
                    month = month1<10 ? '0'+(month1-1) : (month1-1);
                    day = day1+25;
                    break;
                case 3:
                    year = year1;
                    month = '0'+(month1-1);
                    day = year%4==0&&year%100!=0||year%400==0 ? day1+23 : day1+22;
                    break;
                case 5:
                case 7:
                case 10:
                case 12:
                    year = year1;
                    month = month1<=10 ? '0'+(month1-1) : (month1-1);
                    day = day1+24;
                    break;
            }

            month1 = month1<10?'0'+month1 : month1;
        }else{
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1<10 ? '0'+(date.getMonth()+1) : date.getMonth()+1;
            day1 = date.getDate();
            year = year1;
            month = month1;
            day = day1 < 16 ? '0'+(day1-6) : day1-6;

        };

        if(this.state.initTime == '0'){
            var params = {
                pageSize: pagination.pageSize,
                current: pagination.current,
                searchParams: JSON.stringify({startDate:year+'-'+month+'-'+day+' 00:00:00',endDate:year1+'-'+month1+'-'+day1+' 23:59:59',type:'repay'}),
            }
        }else {
            var params = objectAssign({}, this.props.params, {
                current: pagination.current,
                pageSize: pagination.pageSize,
                // searchParams: JSON.stringify({ type: "repay" }),
            });
        }
        this.fetch(params);
    },

    //行点击事件
    onRowClick(record) {
        //console.log(record)
        this.setState({
            selectedRowKeys: [record.id],
            selectedRow: record,
            rowRecord: record
        }, () => {
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
    again(title, record) {
        var record = record;
        confirm({
            title: '你是否确定',
            onOk: function () {

                Utils.ajaxData({
                    url: '/modules/manage/borrow/payAgain.htm',
                    data: {
                        borrowId: record.id
                    },
                    method: "post",
                    callback: (result) => {
                        if(result.code == 200){
                            Modal.success({
                                title: result.msg
                            })
                        }else{
                            Modal.error({
                                title: result.msg
                            })
                        }


                    }
                });
            },
            onCancel: function(){}
        })
    },
    render() {
        const {
            loading,
            selectedRowKeys
        } = this.state;
        const rowSelection = {
            type: 'checkbox',
            selectedRowKeys,
            onSelectAll: this.onSelectAll,
        };
        let me = this;
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
            dataIndex: 'phone'
        }, {
            title: '证件号码',
            dataIndex: 'idNo'
        }, {
            title: '订单号',
            dataIndex: 'orderNo'
        }, {
            title: '交易流水号',
            dataIndex: 'tradeNo'
        }, {
            title: '借款金额(元)',
            dataIndex: 'amount'
        }, {
            title: '借款期限(天)',
            dataIndex: "timeLimit",
        }, {
            title: '订单生成时间',
            dataIndex: 'createTime'
        }, {
            title: '综合费用(元)',
            dataIndex: "fee"
        }, {
            title: '实际到账金额(元)',
            dataIndex: 'realAmount'
        }, {
            title: '实际还款金额(元)',
            dataIndex: 'repayAmount'
        },{
            title: '注册客户端',
            dataIndex: "registerClient",
        },
            {
                title: '渠道',
                dataIndex: "channelName",
            }, {
                title: '订单状态',
                dataIndex: "stateStr",
            }, {
                title: '操作',
                dataIndex: "",
                render: (value, record) => {
                    return (
                        <div>
                            {record.stateStr == '放款失败' ?
                                <div style={{ textAlign: "left" }}>
                                    <a href="#" onClick={me.showModal.bind(me, '查看详情', record, false)}>查看详情</a>
                                    <span className='ant-divider'></span>
                                    <a href="#" onClick={me.again.bind(me, '再次支付', record)}>再次支付</a>
                                </div>
                                : <div style={{ textAlign: "left" }}>
                                    <a href="#" onClick={me.showModal.bind(me, '查看详情', record, false)}>查看详情</a>
                                </div>}
                        </div>
                    )
                }
            }];

        var state = this.state;
        var hStyle = {color: '#1C86EE'};
        return (
            <div className="block-panel">
                <div className="actionBtns" style={{ marginBottom: 16 }}>
                    {/*<button onClick={me.addModal.bind(me,'新增')} className="ant-btn">
                     新增(测试)
                     </button>*/}
                </div>
                <Table columns={columns} rowKey={this.rowKey} ref="table"
                       onRowClick={this.onRowClick}
                       dataSource={this.state.data}
                       rowClassName={this.rowClassName}
                       pagination={this.state.pagination}
                       onChange={this.handleTableChange}
                />
                <div>
                    <h3 style={hStyle}>共{this.state.pagination.total}条记录</h3>
                </div>
                <ReviewWin ref="ReviewWin" visible={state.visible} title={state.title} hideModal={me.hideModal} dataRecord={state.dataRecord} record={state.record}
                           canEdit={state.canEdit} />
                <AddWin ref="AddWin" visible={state.visibleAdd} hideModal={me.hideModal} title={state.title} />
            </div>
        );
    }
})