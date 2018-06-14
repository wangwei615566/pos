import React from 'react'
import {
  Table,
  Modal
} from 'antd';
var confirm = Modal.confirm;
const objectAssign = require('object-assign');
var testData = require('../../../../TestData/Json1');
import AdjustCreditDetial from './AdjustCreditDetial';
import UrgeMemberModifyDetial from './UrgeMemberModifyDetial';
export default React.createClass({
  getInitialState() {
    return {
    	 selectedRows: [],
         selectedRowKeys: [], // 这里配置默认勾选列
         loading: false,
         data: [],
         ren:[],
         pagination: {},
         canEdit: true,
         visible: false,
         visibleAc: false,
         visibleAc1: false,
         dataRecord: [],
         testData: testData.data4,
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
	      button:false,
          visibleAc1: false,
	    });
	    this.state.selectedRowKeys.remove(this.state.record);
	    this.refreshList();
	  },
  //新增跟编辑弹窗
  showModal(title, record, canEdit) {
    var record = record;
    this.setState({
      canEdit: canEdit,
      visible: true,
      title: title,
      record: record
    },()=>{
      this.refs.CustomerWin.setFieldsValue(record);
    });
  },
  //新增
  addModal(title, record, canEdit){
      this.setState({
        visibleAdd:true,
        title:title,  
      })

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
    this.clearSelectedList();
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
      url: '/modules/manage/borrow/repay/urge/list.htm',
      data: params,
      method: "get",
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
    });
    this.fetch(params);
  },
  changeStatus(title,record) {
    var me = this;
    var selectedRowKeys =me.state.selectedRowKeys;
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
        onOk: function() {
          Utils.ajaxData({
            url: trueurl,
            data: {     
              id: id, 
              state:status
            },
            method: 'post',
            callback: (result) => {
              if(result.code==200){
                Modal.success({
                 title: result.msg,
                });     
              }else{
                Modal.error({
                  title:  result.msg,
                });
              }
              me.refreshList();
            }
          });
        },
        onCancel: function() {}
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
	    	if(record.state == "10"){
	    		selectedRowKeys.push(id);
	    		selectedRows.push(record); 		
	    	}
	    } else {
	      selectedRowKeys.remove(id);
	      selectedRows.remove(record);
	    }
	    //console.log(selectedRowKeys);
	    if (selectedRowKeys[0] && selectedRowKeys.length>0) {
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
	    var selectedRowKeys = this.state.selectedRowKeys;
	    if (selected) {
	    	if(selectedRowKeys.length > 0){
	    		 selectedRowKeys = [];
	    	} else {
	    		for (var i = 0; i < selectedRows.length; i++) {   
	    			if(selectedRows[i].state == "10"){
	    				selectedRowKeys.push(selectedRows[i].id);    	
	    			} 
	    		}		
	    	}
	    } else {
	      selectedRowKeys = [];
	    }
	    if(selected && selectedRowKeys.length > 0){
	    	selected = true;
	    } else {
	    	selected = false;
	    }
	    this.setState({	
	      selectedRows: selectedRows,
	      selectedRowKeys: selectedRowKeys,
	      button: selected,
	    })
	  },
	  rowKey(record) {
		    return record.id;
		  },

  showModalAc(title, record, canEdit) {
    if(title=='分配催收员'){
     Utils.ajaxData({
        url: '/modules/manage/borrow/repay/urge/sysUserlist.htm',
        method: 'get',
        callback: (result) => {
          this.refs.AdjustCreditDetial.setFieldsValue(result.data);
         
          this.setState({
            canEdit: false,
            visibleAc: true,
            title: title,
            record: record.id,
            dataRecord:result.data,
          });

        }
      });
    }  
  },
  
  showModalAll(title, ids, canEdit) {
	    if(title=='分配催收员'){
	     Utils.ajaxData({
	        url: '/modules/manage/borrow/repay/urge/sysUserlist.htm',
	        method: 'get',
	        callback: (result) => {
	          this.refs.AdjustCreditDetial.setFieldsValue(result.data);
	          
	          this.setState({
	            canEdit: true,
	            visibleAc: true,
	            title: title,
	            record: ids,
	            dataRecord:result.data,
	          });
	        }
	      });
	    }  
	  },

    showModalAllotRecord(title, record, canEdit) {
        var record = record.borrowId;
        var searchParams = {"dueId":record};
        if(title=='查看分配记录'){
            Utils.ajaxData({
                url: '/modules/manage/borrow/repay/urge/member/modify/list.htm',
                data: {
                    pageSize: 2,
                    current: 1,
                    searchParams: JSON.stringify(searchParams),
                },
                method: 'get',
                callback: (result) => {
                    //console.log(result);
                    this.setState({
                        canEdit: canEdit,
                        visibleAc1: true,
                        title: title,
                        record: result.id,
                        dataRecord: result.data
                    });
                }
            });
        }
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
      title: '真实姓名',
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
    }, {
      title: '催收人',
      render(text, record) {
          if (record.userName == null) {
          	return (
          		  <div title={record.userName} style={{ textAlign: "left" }}>
                  	-                      	
                  </div>
               ) 
          } else {
          	return (
                    <div title={record.userName} style={{ textAlign: "left" }}>
                    	{record.userName}                        	
                    </div>
               ) 
          }
      }
    }, {
        title: '分配时间',
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
      title: '订单状态',
      dataIndex: 'state',
      render:(text,record) =>  {
        switch(text){
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
      render(text, record) {
        if(record.state == "10"){
          return <div ><a href="#" onClick={me.showModalAc.bind(me,"分配催收员",record)}>分配催收员</a></div>
        }else{
          return  <div >< a href="#" onClick={me.showModalAllotRecord.bind(me,"查看分配记录",record)}>查看分配记录</ a></div>
        }
        
      }
    }];
    var state = this.state;
    return (
      <div className="block-panel">
           <div className="actionBtns" style={{ marginBottom: 16 }}>
              <button disabled={!state.button} onClick={me.showModalAll.bind(me, '分配催收员',me.state.selectedRowKeys)} className="ant-btn">
              批量分配催收员
                </button>
            </div>
            <Table columns={columns} rowKey={this.rowKey}
            rowSelection={rowSelection}
            onRowClick={this.onRowClick}
            dataSource={this.state.data}
            pagination={this.state.pagination}
            loading={this.state.loading}
            onChange={this.handleTableChange} />
             <AdjustCreditDetial ref="AdjustCreditDetial"  visible={state.visibleAc}    title={state.title} hideModal={me.hideModal}
             record={state.record} dataRecord={state.dataRecord}  canEdit={state.canEdit} />
             <UrgeMemberModifyDetial ref="UrgeMemberModifyDetial"  visible={state.visibleAc1}    title={state.title} hideModal={me.hideModal}
                record={state.selectedRowKeys} dataRecord={state.dataRecord}  canEdit={state.canEdit} />
      </div>
    );
  }
})