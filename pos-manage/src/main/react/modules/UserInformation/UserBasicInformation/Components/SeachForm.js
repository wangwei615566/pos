import React from 'react';
import {
  Button,
  Form,
  Input,
  Select,
  DatePicker
} from 'antd';
const createForm = Form.create;
const FormItem = Form.Item;
const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;

let SeachForm = React.createClass({
  getInitialState() {
        return {
            
        }
    },
  handleQuery() {
    var params = this.props.form.getFieldsValue();
    var json = {endTime:'',startTime:'',realName:params.realName,loginName:params.loginName,idNo:params.idNo,province:params.province,city:params.city,country:params.country,channelId: params.channelId,registerClient: params.registerClient};
    if(params.registTime){
      json.startTime = (DateFormat.formatDate(params.registTime[0])).substring(0,10);
      json.endTime = (DateFormat.formatDate(params.registTime[1])).substring(0,10);
    }
    this.props.passParams({
      searchParams : JSON.stringify(json),
      pageSize: 10,
      current: 1,
    });
  },
  handleReset() {
    this.props.form.resetFields();
    this.props.passParams({
      pageSize: 10,
      current: 1,
    });
  },
  componentDidMount() {
    this.fetch();
  },
  fetch(){
    var params = {};
    params = {
      pageSize: 10,
      current: 1,
    }
    Utils.ajaxData({
      url: '/modules/manage/promotion/channel/page.htm',
      data: params,
      callback: (result) => {
        this.setState({
          data: result.data,
        });
      }
    });
  },
  render() {
    const {
      getFieldProps
    } = this.props.form;
    var channelList = [];
    if(this.state.data){
      channelList.push(<Option key={'全部'} value= {''} >全部</Option>);
      this.state.data.map(item => {
        channelList.push(<Option key={item.name} value= {item.id} >{item.name}</Option>)
      })
    }
    return (
      <Form inline >
        <FormItem label="真实姓名：">
          <Input onPressEnter={this.handleQuery} {...getFieldProps('realName') } />
        </FormItem>
        <FormItem label="手机号码：">
          <Input onPressEnter={this.handleQuery} {...getFieldProps('loginName') } />
        </FormItem>
        <FormItem label="证件号码：">
          <Input onPressEnter={this.handleQuery} {...getFieldProps('idNo') } />
        </FormItem>
        <FormItem label="注册客户端：">
          <Select style={{ width: 170 }} {...getFieldProps('registerClient',{initialValue: ''})}>
            <Option value= {''} >全部</Option>
            <Option value= {'wx'} >wx</Option>
            <Option value= {'ios'} >ios</Option>
            <Option value= {'android'} >android</Option>
            <Option value= {'h5'} >h5</Option>
            <Option value= {'knapp'} >knapp</Option>
          </Select>
        </FormItem>
        <FormItem label="注册渠道：">
          <Select style={{ width: 170 }} {...getFieldProps('channelId',{initialValue: ''})}>
            {channelList}
          </Select>
        </FormItem>
        <FormItem label="省名称：">
        <Input onPressEnter={this.handleQuery} {...getFieldProps('province') } />
        </FormItem>
        <FormItem label="城市名称：">
        <Input onPressEnter={this.handleQuery} {...getFieldProps('city') } />
        </FormItem>
        <FormItem label="区名称：">
        <Input onPressEnter={this.handleQuery} {...getFieldProps('country') } />
        </FormItem>
        <FormItem label="注册时间：">
            <RangePicker style={{width:"310"}} {...getFieldProps('registTime', { initialValue: '' }) } />
        </FormItem>
        <FormItem><Button type="primary" onClick={this.handleQuery}>查询</Button></FormItem>
        <FormItem><Button type="reset" onClick={this.handleReset}>重置</Button></FormItem>
      </Form>
    );
  }
});

SeachForm = createForm()(SeachForm);
export default SeachForm;