import React from 'react'
import {Table, Modal, Icon} from 'antd';
import Lookdetails from "./Lookdetails";
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
            rowRecord:[],
            record:"",
            visibleAdd:false,
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
                search:JSON.stringify({startTime:year+'-'+month+'-'+day+' 00:00:00',endTime:year1+'-'+month1+'-'+day1+' 23:59:59'}),
            }
        }
        Utils.ajaxData({
            url: '/modules/manage/pay/log/page.htm',
            method: "post",
            data: params,
            callback: (result) => {
                const pagination = this.state.pagination;
                pagination.current = params.current;
                pagination.pageSize = params.pageSize;
                pagination.total = result.page.total;
                if (!pagination.current) {
                    pagination.current = 1
                };
                this.setState({
                    loading: false,
                    data: result.data,
                    pagination
                });
            }
        });
    },

    //查看弹窗
    showModal(title,record, canEdit) {
        var me = this;
        Utils.ajaxData({
            url: '/pay/testPayNotify.htm ',
            method: "post",
            data: {
                orderNo:record.orderNo
            },
            callback: (result) => {
                if (result.code == 200) {
                    Modal.success({
                        title: result.msg,
                        onOk: () => {
                            me.hideModal();
                        }
                    });
                } else {
                    Modal.error({
                        title: result.msg
                    });
                }
            }
        });

    },
    //新增
    addModal(title, record, canEdit){
        this.setState({
            visibleAdd:true,
            title:title,
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
            pageSize: pagination.pageSize,
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
                search: JSON.stringify({startTime:year+'-'+month+'-'+day+' 00:00:00',endTime:year1+'-'+month1+'-'+day1+' 23:59:59'}),
            }
        }else {
            var params = objectAssign({}, this.props.params, {
                current: pagination.current,
                pageSize: pagination.pageSize,
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
            title: '收款人姓名',
            dataIndex: 'realName'
        }, {
            title: '手机号码',
            dataIndex: 'phone'
        }, {
            title: '金额',
            dataIndex: 'amount'
        }, {
            title: '收款银行卡',
            dataIndex: 'cardNo'
        }, {
            title: '借款时间',
            dataIndex: "loanTime"
        }, {
            title: '打款时间',
            render(text, record) {
                if (record.updateTime == null) {
                    return (
                        <div title={record.updateTime} style={{ textAlign: "left" }}>
                            -
                        </div>
                    )
                } else {
                    return (
                        <div title={record.updateTime} style={{ textAlign: "left" }}>
                            {record.updateTime}
                        </div>
                    )
                }
            }
        }, {
            title: '业务',
            dataIndex: "scenesStr",
        },{
            title: '渠道',
            dataIndex: "channelName",
        }, {
            title: '状态',
            dataIndex: "stateStr",
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
                <Lookdetails ref="Lookdetails" visible={state.visible} title={state.title} hideModal={me.hideModal} record={state.record}
                             canEdit={state.canEdit} />
                <AddWin ref="AddWin"  visible={state.visibleAdd} hideModal={me.hideModal} title={state.title}/>
            </div>
        );
    }
})
