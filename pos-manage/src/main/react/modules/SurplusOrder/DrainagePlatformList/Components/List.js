//主表
import React from 'react'
import {
    Table,
    Modal
} from 'antd';
import AddWin from './AddWin'
var confirm = Modal.confirm;
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
            canEdit: true,
            visible: false,
            condition: []
        };
    },
    componentWillReceiveProps(nextProps, nextState) {
        this.fetch(nextProps.params);
    },
    hideModal() {
        this.setState({
            visible: false,
        });
        this.refreshList();
        var pagination = this.state.pagination;

    },
    //新增跟编辑弹窗
    showModal(title, record, canEdit) {
        var record = record;
        if (title == '编辑') {
            this.refs.AddWin.setFieldsValue(record);
        } else if (title == '新增') {
            record = null
        }
        this.setState({
            canEdit: canEdit,
            visible: true,
            title: title,
            record: record
          });
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
        //console.log(pagination)
        const pager = this.state.pagination;
        pager.current = pagination.current;
        this.setState({
            pagination: pager,
        });
        this.refreshList();
    },
    componentDidMount() {
        this.fetch(this.props.params);
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
            url: '/modules/manage/drainage/platform/data/list.htm',
            data: params,
            method: 'post',
            callback: (result) => {
                const pagination = this.state.pagination;
                pagination.current = params.current;
                pagination.total = result.page.total;
                if (!pagination.current) {
                    pagination.current = 1
                };
                this.setState({
                    loading: false,
                    data: result.data,
                    pagination,
                });
                this.clearList();
            }
        });
    },
    clearList() {
        this.setState({
            selectedRowKeys: [],
        });
    },

    refreshList() {
        var pagination = this.state.pagination;
        var params = objectAssign({}, this.props.params, {
            current: pagination.current,
            pageSize: pagination.pageSize
        });
        //console.log("params",params)
        this.fetch(params);
    },
    changeStatus(title, record) {
        //console.log(record)
        var me = this;
        var url;
        var data = {};
        if (title == "禁用") {
            data = {
                id: record.id,
            }
            url = "/modules/manage/drainage/platform/disable.htm";
        } else {
            data = {
                id: record.id,
            }
            url = "/modules/manage/drainage/platform/enable.htm";
        }
        confirm({
            title: '是否确认',
            onOk: function () {
                Utils.ajaxData({
                    url: url,
                    data: data,
                    method: 'post',
                    callback: (result) => {
                        Modal.success({
                            title: result.msg,
                        });
                        me.refreshList();
                    }
                });
            },
            onCancel: function () { }
        });
    },
    onRowClick(record) {
        this.setState({
            selectedRowKeys: [record.id]
        });
    },

    render(text,record) {
        var record = record;
        var me = this;
        const {
            loading,
            selectedRowKeys
        } = this.state;
        const rowSelection = {
            // type: 'radio',
            selectedRowKeys,
            //onChange: this.onSelectChange,
        };
        const hasSelected = selectedRowKeys.length > 0;
        var columns = [{
            title: '排名',
            dataIndex: 'ranking'
        },{
            title: '平台名称',
            dataIndex: "name",
        }, {
            title: '平台标志码',
            dataIndex: 'platform'
        }, {
            title: '推广链接',
            dataIndex: "spreadUrl"
        }, {
            title: '注册量',
            dataIndex: 'registerCount'
        }, {
            title: '借款量',
            dataIndex: "loanCount"
        }, {
            title: '放款成功订单数',
            dataIndex: "loanOrderCount"
        }, {
            title: '放款成功金额',
            dataIndex: "loanOrderAmount"
        }, {
            title: '状态',
            dataIndex: "state"
        }, {
            title: '操作',
            dataIndex: "",
            render(text, record) {
                return (
                    <div style={{ textAlign: "left" }}>
                        {record.state == "启用" ? <a href="#" onClick={me.changeStatus.bind(me, '禁用', record)}>禁用</a> : <a href="#" onClick={me.changeStatus.bind(me, '启用', record)}>启用</a>}
                      &nbsp;&nbsp;<a href="#" onClick={me.showModal.bind(me,'编辑',record,true)}>编辑 </a>
                    </div>
                )
            }
        }];
        var state = this.state;
        return (
            <div className="block-panel">
              <div className="actionBtns" style={{ marginBottom: 16 }}>
                <button className="ant-btn" onClick={this.showModal.bind(this, '新增', null, true)}>
                  新增
                </button>
              </div>
              <Table columns={columns} rowKey={this.rowKey} size="middle" params={this.props.params}
                     dataSource={this.state.data} onRowClick={this.onRowClick}
                     pagination={this.state.pagination}
                     loading={this.state.loading}
                     onChange={this.handleTableChange} />
              <AddWin ref="AddWin" visible={state.visible} condition={state.condition} title={state.title} hideModal={me.hideModal} record={state.record}
                      canEdit={state.canEdit} />
            </div>
        );
    }
})
