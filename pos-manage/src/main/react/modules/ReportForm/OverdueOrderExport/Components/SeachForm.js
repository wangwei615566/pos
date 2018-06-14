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
         var json = {startTime:'',endTime:''};
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
    disabledDate(startValue) {
        var today = new Date();
        return startValue.getTime() > today.getTime();
    },
    handleOut() {
        var params = this.props.form.getFieldsValue();
        var json = {startTime:'',endTime:''};
        //console.log(params);
        if(params.time){
            var d = new Date(params.time[0]);  
            json.startTime=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate();
            var d1 = new Date(params.time[1]);  
            json.endTime=d1.getFullYear() + '-' + (d1.getMonth() + 1) + '-' + d1.getDate();
            //console.log(d);
          }
        window.open("/modules/manage/borrow/repay/overdue/orderList/export.htm?searchParams="+encodeURIComponent(JSON.stringify(json)));
    },
    render() {

        const {getFieldProps} = this.props.form;
        return (
            <Form inline>
             <FormItem label="逾期天数:">
		         <Select style={{ width: 100 }} {...getFieldProps('overDay', { initialValue: '' }) }>
		             <Option value="">15天以上</Option>
		         </Select>
		     </FormItem>
             <FormItem><Button type="primary" onClick={this.handleQuery}>查询</Button></FormItem>
             <FormItem><Button onClick={this.handleOut}>导出</Button></FormItem>
            </Form>
        );
    }
});

SeachForm = createForm()(SeachForm);
export default SeachForm;