import React from 'react';
import {
  Table,
  Button,
} from 'antd';
const objectAssign = require('object-assign');
var Tab2 = React.createClass({
  getInitialState() {
    return {
      loading: false,
      data: [],
      pagination: {},
    };
  },
  rowKey(record) {
    return record.id;
  },

  componentWillReceiveProps(nextProps){
    if(nextProps.visible){
      this.fetch();
    }
  },

  componentDidMount(){
    this.fetch();
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

    handleTableChange(pagination, filters, sorter) {
    const pager = this.state.pagination;
    pager.current = pagination.current;
    pager.userId = this.props.record.userId,
    this.setState({
      pagination: pager,
    });
    this.fetch(pager);
  },

  fetch(params = {}) {
    this.setState({
      loading: true
    });
    if (!params.pageSize) {
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
          pagination
        });
      }
    });
  },

  render() {
    var columns = [{
      title: '姓名',
      dataIndex: "name",
    }, {
      title: '手机号码',
      dataIndex: "phone",
    }];
    return (<div className="block-panel">
              <div style={{height:40 }}>
                 姓名: &nbsp;&nbsp;<input style={{height:28 }} name="name" onKeyPress={(e)=> {this.handleEnterPress(e)}} onChange={(e) => {this.inputName(e)}}/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 电话号码: &nbsp;&nbsp;<input style={{height:28 }} name="phone" onKeyPress={(e)=> {this.handleEnterPress(e)}} onChange={(e) => {this.inputNum(e)}}/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 <Button type="primary" onClick={this.handlePhone}> 查询</Button>&nbsp;&nbsp;&nbsp;
              </div>
              <Table columns={columns} rowKey={this.rowKey}  
              dataSource={this.state.data}
              pagination={this.state.pagination}
              loading={this.state.loading}
              onChange={this.handleTableChange}  />
          </div>
    );
  }
});
export default Tab2;