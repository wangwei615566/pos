import React from 'react'
import {Table, Modal, Icon} from 'antd';
import Lookdetails from "./Lookdetails";
const objectAssign = require('object-assign');
import Withdraw from "./Withdraw";
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
            rowRecord:[],
            record:"",
            visibleWith:false,

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
                current: 1,
            }
        }
        Utils.ajaxData({
            url: '/modules/manage/borrow/repay/arbitra/list.htm',
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
        Utils.ajaxData({
            url: '/modules/manage/borrow/repay/arbitra/queryBalance.htm',
            method: "post",
            data: params,
            callback: (result) => {
                this.setState({
                    loading: false,
                    data1: result.data
                });
            }
        });
    },

    //查看弹窗
    showModal(title,record, canEdit) {
        this.setState({
            visible: true,
            canEdit: canEdit,
            record: record,
            title: "仲裁"+title ,
        },()=>{ 
            Utils.ajaxData({
                url: '/modules/manage/borrow/lookArbitrateDetail.htm',
                data:{
                	orderNo: record.orderNo
                },
                method: "post",
                callback: (result) => {
                    this.refs.Lookdetails.setFieldsValue(result.data);
                    this.setState({
                        dataRecord: result.data,
                    });
                    
                }
            });
        })
    },
    //撤回
	withdrawModal(title, record) {
		this.setState({
		  visibleWith: true,
		  title: title,
		  record: record
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
            visibleWith:false
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
            pageSize: pagination.pageSize,
            // searchParams: JSON.stringify({state:"50"}),
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
            type: 'checkbox',
            selectedRowKeys,
            onSelectAll: this.onSelectAll,
        };
        let me=this;
        const hasSelected = selectedRowKeys.length > 0;
        let openEdit = true;
        if (hasSelected && selectedRowKeys.indexOf("0") === -1) {
            openEdit = false;
        }
        var columns = [{
            title: '订单编号',
            dataIndex: 'orderNo'
        }, {
            title: '姓名',
            dataIndex: 'realName'
        }, {
            title: '身份证号',
            dataIndex: 'idNo'
        }, {
            title: '手机号',
            dataIndex: 'phone'
        }, {
            title: '案件金额',
            dataIndex: "amount",
        }, {
            title: '案件进程',
            dataIndex: 'process'
        }, {
            title: '状态',
            dataIndex: 'processStatus'
        }, {
            title: '操作',
            dataIndex: "",
            render: (value, record) => {
                return (
                    <div style={{ textAlign: "left" }}>
                        <a href="#" onClick={me.showModal.bind(me, '查看详情', record, false)}>查看详情</a>
                        <span className="ant-divider"></span>
                        {record.state=='0'||record.state=='1'||record.state=='5'||record.state=='6'||record.state=='7'||record.state=='8'?<a href="#" onClick={me.withdrawModal.bind(me, '撤回', record, false)}>撤回</a>:""}
                    </div>
                )
            }
        }];

        var state = this.state;
        var hStyle = {color: '#1C86EE'};
        var blances = this.state.data1==undefined?"0":this.state.data1.blances;
        var tickets = this.state.data1==undefined?"0":this.state.data1.tickets;
        return (
            <div className="block-panel">
            <div style={{color: '#1C86EE',textAlign: 'right'}}><span>仲裁账户余额：{blances}元； </span>
            <span>仲券数量：{tickets}张 </span></div>
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
                <Lookdetails ref="Lookdetails" visible={state.visible} title={state.title} hideModal={me.hideModal} record={state.record}
                canEdit={state.canEdit} />
                <Withdraw ref="Withdraw"  visible={state.visibleWith} hideModal={me.hideModal} title={state.title} record={state.record}/>
            </div>
        );
    }
})
