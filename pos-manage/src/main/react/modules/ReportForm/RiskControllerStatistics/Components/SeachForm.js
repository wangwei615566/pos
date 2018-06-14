import React from 'react';
import {Button, Form, Input, Select,Message,DatePicker } from 'antd';
const createForm = Form.create;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const Option = Select.Option;

let SeachForm = React.createClass({
    getInitialState() {
        return {
            roleList: []
        }
    },
     handleQuery() {
        var params = this.props.form.getFieldsValue();
         var json = {startTime:'',endTime:'',channelId:params.channelId};
     //console.log(params);
     if(params.time){
         var d = new Date(params.time[0]);  
         json.startTime=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate();
         var d1 = new Date(params.time[1]);  
         json.endTime=d1.getFullYear() + '-' + (d1.getMonth() + 1) + '-' + d1.getDate();
         //console.log(d);
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
    disabledDate(startValue) {
        var today = new Date();
        return startValue.getTime() > today.getTime();
    },
    handleOut() {
        var params = this.props.form.getFieldsValue();
        var json = {startTime:'',endTime:'',channelId:params.channelId};
        //console.log(params);
        if(params.time){
            var d = new Date(params.time[0]);  
            json.startTime=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate();
            var d1 = new Date(params.time[1]);  
            json.endTime=d1.getFullYear() + '-' + (d1.getMonth() + 1) + '-' + d1.getDate();
            //console.log(d);
          }
        window.open("/modules/manage/rcDataStatistics/export.htm?searchParams="+encodeURIComponent(JSON.stringify(json)));
    },
    render() {

    	 const {
    	      getFieldProps
    	    } = this.props.form;
        var channelList = [];
        if(this.state.data){
          channelList.push(<Option key={'全部'} value= {'0'} >全部</Option>);
          this.state.data.map(item => {
            channelList.push(<Option key={item.name} value= {item.id} >{item.name}</Option>)
          })
        }
        return (
	            <Form inline >
	             <FormItem label="日期:">
	             	<RangePicker {...getFieldProps('time', { initialValue: '' }) } />
	             </FormItem>
	             <FormItem label="注册渠道：">
	             <Select style={{ width: 170 }} {...getFieldProps('channelId',{initialValue: ''})}>
	               {channelList}
	             </Select>
	           </FormItem>
	             
	             <FormItem><Button type="primary" onClick={this.handleQuery}>查询</Button></FormItem>
	             <FormItem><Button type="reset" onClick={this.handleReset}>重置</Button></FormItem>
	             <FormItem><Button onClick={this.handleOut}>导出</Button></FormItem>
	            </Form>
        );
    }
});

SeachForm = createForm()(SeachForm);
export default SeachForm;