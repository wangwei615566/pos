import React from 'react';
import { Button, Form, Input, Select, Message,DatePicker } from 'antd';
const RangePicker = DatePicker.RangePicker;
const createForm = Form.create;
const FormItem = Form.Item;
const Option = Select.Option;

let SeachForm = React.createClass({
    getInitialState() {
        return {
            roleList: []
        }
    },
 handleQuery() {
    	var params = this.props.form.getFieldsValue();
    	var json = {startTime:'',endTime:'',realName:params.realName,phone:params.phone,orderNo:params.orderNo,repayState:params.repayState,timeSort:params.timeSort};
        if(params.time){
           var d = new Date(params.time[0]);
           json.startTime=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()+' '+'00:00:00';
           var d1 = new Date(params.time[1]);
           json.endTime=d1.getFullYear() + '-' + (d1.getMonth() + 1) + '-' + d1.getDate()+' '+'23:59:59';
         }
        this.props.passParams({
            searchParams: JSON.stringify(json),
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

        const { getFieldProps } = this.props.form;
        return (
            <Form inline>
	            <FormItem label="应还款时间:">
		            <RangePicker {...getFieldProps('time', { initialValue: '' }) } />
		        </FormItem>
                <FormItem label="真实姓名:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('realName') } />
                </FormItem>
                <FormItem label="手机号码:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('phone') } />
                </FormItem>
                <FormItem label="订单号:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('orderNo') } />
                </FormItem>
                <FormItem label="还款状态:">
                    <Select style={{ width: 100 }} {...getFieldProps('repayState', { initialValue: '' }) }>
                        <Option value="">全部</Option>
                        <Option value="10">已还款</Option>
                        <Option value="20">未还款</Option>
                    </Select>
                </FormItem>
                <FormItem label="日期排序:">
                <Select style={{ width: 100 }} {...getFieldProps('timeSort', { initialValue: '' }) }>
                	<Option value="">默认</Option>
                    <Option value="1">顺序</Option>
                    <Option value="2">倒序</Option>
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