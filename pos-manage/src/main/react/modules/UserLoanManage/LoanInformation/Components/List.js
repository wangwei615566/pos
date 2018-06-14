import React from 'react'
import { Table, Modal, Icon, Button } from 'antd';
import ReviewWin from './ReviewWin';
var repaymentTypeText = { '10': '待审核', '20': '审核中', '30': '通过', '40': '已拒绝', '50': '还款中', '60': '还款完成', '70': '逾期' }
const objectAssign = require('object-assign');
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
            dataRecord: '',
            dataForm: '',
            record: "",
            visibleAdd: false,
            recordSoure: null,
            initTime:'0',
        };
    },

    componentWillReceiveProps(nextProps, nextState) {
        this.setState({
            initTime:'1',
        });
        this.fetch(nextProps.params);
    },

    componentDidMount() {
        this.fetch();
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
                searchParams: JSON.stringify({startDate:year+'-'+month+'-'+day+' 00:00:00',endDate:year1+'-'+month1+'-'+day1+' 23:59:59'}),
            }

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
                    pagination,
                });
            }
        });
    },

    //查看弹窗
    showModal(title, record, canEdit) {
        console.log(record);
        var that = this;
        that.setState({
            visible: true,
            canEdit: canEdit,
            record: record,
            title: title + "【渠道来源："+record.channelName+"】",
        }, () => {
            Utils.ajaxData({
                url: '/modules/manage/cl/cluser/detail.htm',
                data: {
                    userId: record.userId
                },
                callback: (result) => {
                    if (result.code == 200) {
                        var dataForm = {};
                        dataForm.realName = result.data.userbase.realName;
                        dataForm.sex = result.data.userbase.sex;
                        dataForm.idNo = result.data.userbase.idNo;
                        dataForm.age = result.data.userbase.age;
                        dataForm.cardNo = result.data.userbase.cardNo;
                        dataForm.bank = result.data.userbase.bank;
                        dataForm.bankPhone = result.data.userbase.bankPhone;
                        dataForm.phone = result.data.userbase.phone;
                        dataForm.liveAddr = result.data.userbase.liveAddr;
                        dataForm.registTime = result.data.userbase.registTime;
                        dataForm.registerAddr = result.data.userbase.registerAddr;
                        dataForm.registerIp = result.data.userbase.registerIp;
                        dataForm.channelName = result.data.userbase.channelName;
                        dataForm.registerClient = result.data.userbase.registerClient;
                        dataForm.score = result.data.userbase.score;
                        dataForm.education = result.data.userbase.education;
                        dataForm.education = result.data.userbase.education;
                        dataForm.companyName = result.data.userbase.companyName;
                        dataForm.companyPhone = result.data.userbase.companyPhone;
                        dataForm.salary = result.data.userbase.salary;
                        dataForm.workingYears = result.data.userbase.workingYears;
                        dataForm.companyAddr = result.data.userbase.companyAddr;
                        dataForm.taobao = result.data.userOtherInfo != null ? result.data.userOtherInfo.taobao : '';
                        dataForm.email = result.data.userOtherInfo != null ? result.data.userOtherInfo.email : '';
                        dataForm.qq = result.data.userOtherInfo != null ? result.data.userOtherInfo.qq : '';
                        dataForm.wechat = result.data.userOtherInfo != null ? result.data.userOtherInfo.wechat : '';
                        dataForm.registerCoordinate = result.data.userbase.registerCoordinate;
                        if (result.data.userContactInfo.length > 0) {
                            if (result.data.userContactInfo[0].type == "10") {
                                dataForm.urgentName = result.data.userContactInfo[0].name;
                                dataForm.urgentPhone = result.data.userContactInfo[0].phone;
                                dataForm.urgentRelation = result.data.userContactInfo[0].relation;
                            } else {
                                dataForm.otherName = result.data.userContactInfo[0].name;
                                dataForm.otherPhone = result.data.userContactInfo[0].phone;
                                dataForm.otherRelation = result.data.userContactInfo[0].relation;
                            }
                            if (result.data.userContactInfo[1].type == "20") {
                                dataForm.otherName = result.data.userContactInfo[1].name;
                                dataForm.otherPhone = result.data.userContactInfo[1].phone;
                                dataForm.otherRelation = result.data.userContactInfo[1].relation;
                            } else {
                                dataForm.urgentName = result.data.userContactInfo[1].name;
                                dataForm.urgentPhone = result.data.userContactInfo[1].phone;
                                dataForm.urgentRelation = result.data.userContactInfo[1].relation;
                            }
                        }
                        if (result.data.userAuth) {
                            if (result.data.userAuth.bankCardState == 10) {
                                dataForm.bankCardState = "未认证"
                            } else if (result.data.userAuth.bankCardState == 20) {
                                dataForm.bankCardState = "认证中"
                            } else if (result.data.userAuth.bankCardState == 30) {
                                dataForm.bankCardState = "已认证"
                            }
                            if (result.data.userAuth.idState == 10) {
                                dataForm.idState = "未认证"
                            } else if (result.data.userAuth.idState == 20) {
                                dataForm.idState = "认证中"
                            } else if (result.data.userAuth.idState == 30) {
                                dataForm.idState = "已认证"
                            }
                            if (result.data.userAuth.phoneState == 10) {
                                dataForm.phoneState = "未认证"
                            } else if (result.data.userAuth.phoneState == 20) {
                                dataForm.phoneState = "认证中"
                            } else if (result.data.userAuth.phoneState == 30) {
                                dataForm.phoneState = "已认证"
                            }
                            if (result.data.userAuth.zhimaState == 10) {
                                dataForm.zhimaState = "未认证"
                            } else if (result.data.userAuth.zhimaState == 20) {
                                dataForm.zhimaState = "认证中"
                            } else if (result.data.userAuth.zhimaState == 30) {
                                dataForm.zhimaState = "已认证"
                            }
                        }
                        that.setState({
                            recordSoure: result.data,
                            dataForm:dataForm
                        })
                        that.refs.ReviewWin.refs.Tab2.setFieldsValue(dataForm);
                    }
                }
            });
            Utils.ajaxData({
                url: '/modules/manage/borrow/borrowRepayContent.htm',
                data: {
                    borrowId: record.id
                },
                method: "post",
                callback: (result) => {
                    result.data.remark = (result.data.state == "19" || result.data.state == "21" || result.data.state == "27") ? result.data.remark : "审核通过";
                    that.refs.ReviewWin.refs.Lookdetails.setFieldsValue(result.data);
                    that.setState({
                        dataRecord: result.data,
                    });
                }
            });
            Utils.ajaxData({
                url: '/modules/manage/review/riskReport.htm',
                data:{
                    borrowId:record.id
                },
                method: "post",
                callback: (result) => {
                    //console.log(result.data);
                    //that.refs.ReviewWin.refs.Tab4.setFieldsValue(result.data);
                    that.setState({
                        riskRecord: result.data,
                    });

                }
            });
        })
    },
    //新增
    addModal(title) {
        Utils.ajaxData({
            url: '/modules/manage/user/list.htm',
            method: "post",
            callback: (result) => {
                this.setState({
                    dataRecord: result.data.list,
                    visibleAdd: true,
                    title: title,
                });
            }
        });

    },
    //隐藏弹窗
    hideModal() {
        this.setState({
            visible: false,
            visible1: false,
            visible2: false,
            selectedIndex: '',
            selectedRowKeys: [],
            visibleAdd: false,
            dataForm: '',
            dataRecord: ''
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
                searchParams: JSON.stringify({startDate:year+'-'+month+'-'+day+' 00:00:00',endDate:year1+'-'+month1+'-'+day1+' 23:59:59'}),
            }
        } else {
            var params = objectAssign({}, this.props.params, {
                current: pagination.current,
                pageSize: pagination.pageSize
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

    render() {
        //console.log(this.state.riskRecord);
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
            title: '订单号',
            dataIndex: 'orderNo'
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
        },{
            title: '应还时间',
            render(text, record) {
                if (record.sRepay == null) {
                    return (
                        <div title={record.sRepay} style={{ textAlign: "left" }}>
                            -
                        </div>
                    )
                } else {
                    return (
                        <div title={record.sRepay} style={{ textAlign: "left" }}>
                            {record.sRepay}
                        </div>
                    )
                }
            }
        },{
            title: '应还金额(元)',
            render(text, record) {
                if (record.penaltyAmoutCount == null) {
                    return (
                        <div title={record.penaltyAmoutCount} style={{ textAlign: "left" }}>
                            -
                        </div>
                    )
                } else {
                    return (
                        <div title={record.penaltyAmoutCount} style={{ textAlign: "left" }}>
                            {record.penaltyAmoutCount}
                        </div>
                    )
                }
            }
        }, {
            title: '实际到账金额(元)',
            dataIndex: 'realAmount'
        }, {
            title: '实际还款金额(元)',
            render(text, record) {
                if (record.repayAmount == null) {
                    return (
                        <div title={record.repayAmount} style={{ textAlign: "left" }}>
                            -
                        </div>
                    )
                } else {
                    return (
                        <div title={record.repayAmount} style={{ textAlign: "left" }}>
                            {record.repayAmount}
                        </div>
                    )
                }
            }
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
                        <div style={{ textAlign: "left" }}>
                            <a href="#" onClick={me.showModal.bind(me, '查看详情', record, false)}>查看详情</a>
                        </div>
                    )
                }
            }];

        var state = this.state;
        var hStyle = {color: '#1C86EE'};
        return (
            <div className="block-panel">
                {/*<div className="actionBtns" style={{ marginBottom: 16 }}>
                 <Button onClick={me.addModal.bind(me, '新增')}>
                 新增(测试)
                 </Button>
                 </div>*/}
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
                <ReviewWin ref="ReviewWin" visible={state.visible} title={state.title} hideModal={me.hideModal} record={state.record}
                           canEdit={state.canEdit} riskRecord={state.riskRecord} dataForm={state.dataForm} recordSoure={state.recordSoure}/>
                <AddWin ref="AddWin" dataRecord={state.dataRecord} visible={state.visibleAdd} hideModal={me.hideModal} title={state.title}/>
            </div>
        );
    }
})
