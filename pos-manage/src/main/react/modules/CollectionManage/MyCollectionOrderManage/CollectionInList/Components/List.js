import React from 'react'
import {
  Table,
  Modal
} from 'antd';
var confirm = Modal.confirm;
import AddWin from './AddWin';
import ReviewWin from './ReviewWin';
import AdjustCreditDetial from './AdjustCreditDetial';
const objectAssign = require('object-assign');
var testData = require('../../../../TestData/Json1');
export default React.createClass({
  getInitialState() {
    return {
      selectedRows: [],
      selectedRowKeys: [], // 这里配置默认勾选列
      loading: false,
      data: [],
      pagination: {},
      canEdit: true,
      visible: false,
      visible1:false,
      visibleAc: false,
      dataRecord: [],
      testData: testData.data7,
      button: false,
    };
  },
  componentWillReceiveProps(nextProps, nextState) {
    this.clearSelectedList();
    this.fetch(nextProps.params);
  },
  hideModal() {
    this.setState({
      visible: false,
      visibleAc: false,
      visible1: false,
    });
    this.refreshList();
  },
  //新增跟编辑弹窗
  showModal(title, record, canEdit) {
    var record = record;
    var me = this;
    if (title == '新增催收反馈') {
      me.setState({
        canEdit: canEdit,
        visible: true,
        title: title,
        record: record
      }, () => {
        switch (record.state) {
          case "10":
            record.state = "未分配";
            break;
          case "11":
            record.state = "待催收";
            break;
          case "20":
            record.state = "催收中";
            break;
          case "30":
            record.state = "承诺还款";
            break;
          case "40":
            record.state = "催收成功";
            break;
          case "50":
            record.state = "坏账";
            break;
        }
        me.refs.AddWin.setFieldsValue(record);
      }
      );
    } else if(title=="查看催收反馈"){
      Utils.ajaxData({
        url: '/modules/manage/borrow/repay/urge/listDetail.htm',
        data: {
          id: record.id
        },
        method: 'get',
        callback: (result) => {
          //console.log(result);
          if(result.data.logs && result.data.logs.length >= 2){
            for(var i = 0; i < result.data.logs.length-1; i++){
              for(var j = i+1; j < result.data.logs.length; j++){
                if(result.data.logs[i].createTime < result.data.logs[j].createTime){
                    var z = result.data.logs[j];
                    result.data.logs[j] = result.data.logs[i];
                    result.data.logs[i] = z;
                }
              }
            }
          }
          me.setState({
            canEdit: canEdit,
            visibleAc: true,
            title: title,
            record: result.data.logs,
            dataRecord: result.data.logs
          });
        }
      });
    } else {
    	me.setState({
	      canEdit: canEdit,
	      visible1: true,
	      title: title,
	      record: record,
	    }, () => {
	      Utils.ajaxData({
	        url: '/modules/manage/cl/cluser/detail.htm',
	        data: {
	        	userId:record.id,
	        	borrowId: record.borrowId
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
	            dataForm.registerClient = result.data.userbase.registerClient;
	            dataForm.channelName = result.data.userbase.channelName;
	            dataForm.score = result.data.userbase.score;
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
	            //console.log(result.data);
	            this.refs.ReviewWin.refs.Tab1.setFieldsValue(dataForm);
	            this.setState({
	              recordSoure: result.data
	            })
	          }
	        }
	      });
	    });
	}
  },
  //发送
  send(title, record, canEdit) {
    var me = this;
    var ids = me.state.selectedRowKeys.toString();
    Utils.ajaxData({
      url: '/api/smsBatch.htm',
      data: {
        ids: JSON.stringify(ids)
      },
      method: 'post',
      callback: (result) => {
        if (result.code == 200) {
          Modal.success({
            title: result.msg,
          });
        } else {
          Modal.error({
            title: result.msg,
          });
        }
        me.refreshList();
      }
    });

  },
  rowKey(record) {
    return record.id;
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
  fetch(params = {}) {
    this.setState({
      loading: true
    });
    if (!params.pageSize) {
      var params = {};
      params = {
        pageSize: 10,
        current: 1,
        searchParams: JSON.stringify({ state: "20" }),
      }
    }
    if(!params.searchParams){
            params.searchParams= JSON.stringify({state: '20'})
        }
    Utils.ajaxData({
      url: '/modules/manage/borrow/repay/urge/collection/list.htm',
      data: params,
      method: 'post',
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
  clearSelectedList() {
    this.setState({
      selectedRowKeys: [],
    });
  },
  refreshList() {
    var pagination = this.state.pagination;
    var params = objectAssign({}, this.props.params, {
      current: pagination.current,
      pageSize: pagination.pageSize,
      // searchParams: JSON.stringify({ state: "20" }),
    });
    this.fetch(params);
  },
  changeStatus(title, record) {
    var me = this;
    var selectedRowKeys = me.state.selectedRowKeys;
    var id = record.id;
    var status;
    var msg = "";
    var tips = "";
    var trueurl = "";
    if (title == "加入黑名单") {
      msg = '加入黑名单';
      status = '20';
      tips = '您是否确定加入黑名单';
      trueurl = "/modules/manage/user/updateState.htm"
    } else if (title == "解除黑名单") {
      msg = '解除黑名单成功';
      status = '10';
      tips = '您是否确定解除黑名单';
      trueurl = "/modules/manage/user/updateState.htm"
    }
    confirm({
      title: tips,
      onOk: function () {
        Utils.ajaxData({
          url: trueurl,
          data: {
            id: id,
            state: status
          },
          method: 'post',
          callback: (result) => {
            if (result.code == 200) {
              Modal.success({
                title: result.msg,
              });
            } else {
              Modal.error({
                title: result.msg,
              });
            }
            me.refreshList();
          }
        });
      },
      onCancel: function () { }
    });
  },
  componentDidMount() {
    this.fetch();
  },
  onRowClick(record) {
    //console.log(record);
    var button = this.state.button;
    var id = record.id;
    var selectedRows = this.state.selectedRows;
    var selectedRowKeys = this.state.selectedRowKeys;
    if (selectedRowKeys.indexOf(id) < 0) {
      selectedRowKeys.push(id);
      selectedRows.push(record);
    } else {
      selectedRowKeys.remove(id);
      selectedRows.remove(record);
    }
    //console.log(selectedRowKeys);
    if (selectedRowKeys[0]) {
      button = true;
    } else {
      //console.log(11111111);
      button = false;
    }
    this.setState({
      selectedRows: selectedRows,
      selectedRowKeys: selectedRowKeys,
      button: button
    });
  },
  onSelectAll(selected, selectedRows, changeRows) {
    //console.log('1111111');
    //console.log(selectedRows);
    var selectedRowKeys = this.state.selectedRowKeys;
    if (selected) {
      for (var i = 0; i < selectedRows.length; i++) {
        selectedRowKeys.push(selectedRows[i].id);
      }
    } else {
      selectedRowKeys = [];
    }
    //console.log(selectedRowKeys);
    this.setState({
      selectedRows: selectedRows,
      selectedRowKeys: selectedRowKeys,
      button: selected,
    })
  },

  render() {
    var me = this;
    const {
      loading,
      selectedRowKeys
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onSelectAll: this.onSelectAll
    };
    const hasSelected = selectedRowKeys.length > 0;
    var columns = [{
      title: '借款人姓名',
      dataIndex: 'borrowName',
    }, {
      title: '手机号码',
      dataIndex: "phone",
    }, {
      title: '金额',
      dataIndex: 'amount'
    }, {
      title: '借款时间',
      dataIndex: 'borrowTime',
    }, {
      title: '预计还款时间',
      dataIndex: 'repayTime',
    }, {
      title: '逾期天数',
      dataIndex: "penaltyDay",
    }, {
      title: '逾期等级',
      dataIndex: 'level'
    }, {
      title: '罚息',
      dataIndex: "penaltyAmout",
    },{
	    title: '应还款总额',
	    dataIndex: "repayAmount",
    }, {
      title: '催收人',
      dataIndex: 'userName',
    }, {
        title: '分配时间',
        dataIndex: 'updateTime',
    }, {
      title: '订单状态',
      dataIndex: 'state',
      render: (text, record) => {
        switch (text) {
          case "10":
            return "未分配";
            break;
          case "11":
            return "待催收";
            break;
          case "20":
            return "催收中";
            break;
          case "30":
            return "承诺还款";
            break;
          case "40":
            return "催收成功";
            break;
          case "50":
            return "坏账";
            break;
        }
      }
    }, {
      title: '操作',
      render: (text, record) => {
        return <div>
          <a href="javascript:;" onClick={me.showModal.bind(me, '查看详情', record, true)}>查看</a>
          <span className="ant-divider"></span>
          <a href="#" onClick={me.showModal.bind(me, '新增催收反馈', record, false)}>新增催收反馈</a>
          <span className="ant-divider"></span>
          <a href="#" onClick={me.showModal.bind(me, '查看催收反馈', record, true)}>查看催收反馈</a>
        </div>
      }
    }];
    var state = this.state;
    return (
      <div className="block-panel">
        <div hidden={true}  className="actionBtns" style={{ marginBottom: 16 }}>
          <button disabled={!state.button} onClick={me.send.bind(me, '发送')} className="ant-btn">
            批量发送催收短信
              </button>
        </div>
        <Table columns={columns} rowKey={this.rowKey}
          rowSelection={rowSelection}
          onRowClick={this.onRowClick}
          dataSource={this.state.data}
          pagination={this.state.pagination}
          loading={this.state.loading}
          onChange={this.handleTableChange} />
        <AdjustCreditDetial ref="AdjustCreditDetial" visible={state.visibleAc} title={state.title} hideModal={me.hideModal}
          record={state.selectedrecord} dataRecord={state.dataRecord} pagination={state.pagination} canEdit={state.canEdit} />
        <ReviewWin ref="ReviewWin" visible={state.visible1} recordSoure={state.recordSoure} title={state.title} hideModal={me.hideModal} record={state.record}
        canEdit={state.canEdit} />
        <AddWin ref="AddWin" record={state.record} rowKeys={state.selectedRowKeys} visible={state.visible} title={state.title} canEdit={state.canEdit} hideModal={me.hideModal} />
      </div>
    );
  }
})