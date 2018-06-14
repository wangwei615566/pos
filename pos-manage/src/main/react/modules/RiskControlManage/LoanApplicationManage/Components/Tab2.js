import React from 'react';
import {
  Table,
  Checkbox,
  Button,
} from 'antd';
import AdjustCreditDetial from './AdjustCreditDetial';
var Tab2 = React.createClass({
  getInitialState() {

    return {
      loading: false,
      data: [],
      visible:true,
      pagination: {},
      check: true,
      canEdit:false,
      sensitiveData: [],
      visiblesw:false,
        dataForm: '',
        inputPhone:''
    };
  },

  rowKey(record) {
    return record.id;
  },
  showModal(e) {
//	  e.nativeEvent.stopImmediatePropagation();
	  var params = {};
	  params = {
		        type: 1,
		      }
	     Utils.ajaxData({
	        url: '/modules/manage/sensitive/list.htm',
	        method: 'get',
	        data:params,
	        callback: (result) => {
	          this.setState({
	        	 visiblesw: true,
	            dataRecord:result.data,
	            canEdit:true,
	          });
	        }
	      });
	  },
  hideModal() {
	  if(this.state.check){
		  this.sensitiveList();
	  } else {
		  this.fetch();
	  }
	  console.log("++++++++++++");
	    this.setState({
	    visiblesw:false,
	      visible: false,
	    });
	  },
  componentWillReceiveProps(nextProps) {
    if (nextProps.visible) {
    	this.sensitiveList();
    }
  },

  componentDidMount() {
	  if(this.state.check){
		  this.sensitiveList();	
	  } else {
		  this.fetch();
	  }
	  if(!this.state.visible){
		  this.setState({	  
			  visiblesw:false
	  	    });
	  }

  },
    inputName(e){
        const pager = this.state.pagination;
        // console.log(e.target);
        var inputName = e.target.value;
        pager.name = inputName;
        this.setState({
            inputName:pager.name
        });
    },

    inputNum(e){
        const pager = this.state.pagination;
        // console.log(e.target);
        var phonenum = e.target.value;
        pager.phone = phonenum;
        this.setState({
            inputPhone:pager.phone,
            inputName:pager.name
        });
    },

    handlePhone(){
        var name = this.state.inputName;
        var phone = this.state.inputPhone;
        this.fetch();
    },

    handleEnterPress(e) {
        if(e.key=='Enter'){
            this.handlePhone();
        }
    },

  onClickEvent(e) {
//	  e.stopPropagation();
	const pager = this.state.pagination;
    if (this.state.check) {
      this.setState({
        check: false,
        visiblesw:false,
      });
      if (!pager.current) {
        pager.current = 1;
      }
      this.fetch(pager);
    } else {
      this.setState({ check: true,
    	  visiblesw:false,});
      if (!pager.current) {
        pager.current = 1;
      }
      pager.userId = this.props.record.userId,
        this.setState({
          pagination: pager,
        });
      this.sensitiveList(pager);   
    }
  },

  sensitiveList(params = {}) {
    this.setState({
      loading: true
    });
    if (!params.pageSize || !params.current) {
      var params = {};
      params = {
        pageSize: 5,
        current: 1,
        userId: this.props.record.userId,
      }
    }
    Utils.ajaxData({
      url: '/modules/manage/msg/sensitive/listContacts.htm',
      data: params,
      callback: (result) => {
        const pagination = this.state.pagination;
        pagination.current = params.current;
        pagination.pageSize = params.pageSize;
        pagination.total =result.page.total;
        if (!pagination.current) {
          pagination.current = 1
        };
        this.setState({
          loading: false,
          data: result.data.list,
          pagination,
        });
      }
    });
  },

  handleTableChange(pagination, filters, sorter) {
    const pager = this.state.pagination;
    pager.current = pagination.current;
    pager.userId = this.props.record.userId;
    pager.phone  =  this.state.inputPhone;

      this.setState({
        pagination: pager,
        visiblesw:false,
      });
    if(this.state.check){
    	this.sensitiveList(pager);
    } else {
    	this.fetch(pager);
    }
  },

  fetch(params = {}) {
	  this.setState({
      loading: true
    });
    if (!params.pageSize || params.current==1) {
      var params = {};
      params = {
        pageSize: 5,
        current: 1,
        userId: this.props.record.userId,
        phone: this.state.inputPhone,
        name:this.state.inputName,
      }
    }
    Utils.ajaxData({
      url: '/modules/manage/msg/listContacts.htm',
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
          data: result.data.list,
          pagination,
        });

      }
    });
  },

  render() {
    var columns = [{
      title: '姓名',
      dataIndex: "name",
      key: 'name',
      render: (text, record) => (<span dangerouslySetInnerHTML={{__html:record.name}}></span>)
    }, {
      title: '手机号码',
      dataIndex: "phone",
      key: 'phone'
    }];
    return (<div className="block-panel">
      <div style={{height:40 }}>
        姓名: &nbsp;&nbsp;<input style={{height:28 }} name="name" onKeyPress={(e)=> {this.handleEnterPress(e)}} onChange={(e) => {this.inputName(e)}}/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        电话号码: &nbsp;&nbsp;<input style={{height:28 }} name="phone" onKeyPress={(e)=> {this.handleEnterPress(e)}} onChange={(e) => {this.inputNum(e)}}/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <Button type="primary" onClick={this.handlePhone}> 查询</Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <div className="sensitive-name">
          <Checkbox checked={this.state.check} onChange={(e) => {this.onClickEvent(e)}}>敏感词联系人</Checkbox>
          &nbsp;&nbsp;<Button onClick={(e) => {this.showModal(e)}}> 编辑</Button>
        </div >
      </div>

    <Table columns={columns} rowKey={this.rowKey}
      dataSource={this.state.data}
      pagination={this.state.pagination}
      loading={this.state.loading}
      onChange={this.handleTableChange}/>

    <AdjustCreditDetial ref="AdjustCreditDetial"  visiblesw={this.state.visiblesw}  dataRecord={this.state.dataRecord}  hideModal={this.hideModal}  canEdit={this.state.canEdit} /> 
  </div>
    );
  }
});

export default Tab2;