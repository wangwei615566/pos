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

let SeachForm = React.createClass({
  getInitialState() {
        return {
            
        }
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
  handleQuery() {
    var params = this.props.form.getFieldsValue();
    this.props.passParams({
      searchParams : JSON.stringify(params),
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
        <FormItem label="姓名：">
          <Input onPressEnter={this.handleQuery} {...getFieldProps('realName') } />
        </FormItem>
        <FormItem label="手机号码：">
          <Input onPressEnter={this.handleQuery} {...getFieldProps('phone') } />
        </FormItem>
        <FormItem label="身份证号码：">
          <Input onPressEnter={this.handleQuery} {...getFieldProps('idNo') } />
        </FormItem>
        <FormItem label="逾期等级：">
       <Select style={{ width: 170 }} {...getFieldProps('level',{initialValue: ''})}>
        <Option value="">全部</Option>
        <Option value="M1">M1</Option>
        <Option value="M2">M2</Option>
        <Option value="M3">M3</Option>
        </Select>
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
        <FormItem label="列入原因：">
        <Select style={{ width: 170 }} {...getFieldProps('blackReason',{initialValue: ''})}>
        <Option value="还款态度消极">还款态度消极</Option>
        <Option value="多头借贷">多头借贷</Option>
        <Option value="逾期14天及以上">逾期14天及以上</Option>
        <Option value="伪造信息/虚假信息">伪造信息/虚假信息</Option>
        <Option value="暴力威胁">暴力威胁</Option>
        <Option value="拖欠/恶意欠款">拖欠/恶意欠款</Option>
        <Option value="其它">其它</Option>
        </Select>
        </FormItem>
        <FormItem><Button type="primary" onClick={this.handleQuery}>查询</Button></FormItem>
        <FormItem><Button type="reset" onClick={this.handleReset}>重置</Button></FormItem>
      </Form>
    );
  }
});

SeachForm = createForm()(SeachForm);
export default SeachForm;