import React from 'react';
import {Button, Form, Input, Select,Message,DatePicker } from 'antd';
const createForm = Form.create;
const RangePicker = DatePicker.RangePicker;
const FormItem = Form.Item;
const Option = Select.Option;

let SeachForm = React.createClass({
    getInitialState() {
        return {
            roleList: [],
            initDate:true
        }
    },
    handleQuery() {
        var params = this.props.form.getFieldsValue();
        params = this.paseParams(params);
        this.props.passParams({
            search: JSON.stringify(params),
            pageSize: 10,
            current: 1,
        });
    },
    handleReset() {
        this.props.form.resetFields();
        this.setState({
            initDate:false
        });
        this.props.passParams({
            pageSize: 10,
            current: 1,
        });
    },
    handleOut() {
        var params = this.props.form.getFieldsValue();
        params = this.paseParams(params);
        var json = JSON.stringify(params);
        window.open("/modules/manage/payLog/export.htm?searchParams="+encodeURIComponent(json));

    },
    paseParams(params){
    	var json = {startTime:'',endTime:'',state:params.state,scenes:params.scenes,realName:params.realName,phone:params.phone};
        if(params.time[0]){
            var d = new Date(params.time[0]);
            json.startTime=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() +' 00:00:00';
            var d1 = new Date(params.time[1]);
            json.endTime=d1.getFullYear() + '-' + (d1.getMonth() + 1) + '-' + d1.getDate() +' 23:59:59';
        }
		return json;
    },
    disabledDate(startValue) {
        var today = new Date();
        return startValue.getTime() > today.getTime();
    },
    render() {

        const {getFieldProps} = this.props.form;
        var year, month, day;
        var year1, month1, day1;
        var date = new Date();
        if(date.getDate() < 7 ){
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1;
            day1 = date.getDate();
            switch(month1){
                case 1:
                    year = year1-1;
                    month = 12;
                    day = day1+25;
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                    year = year1;
                    month = month1<10 ? '0'+(month1-1) : (month1-1);
                    day = day1+25;
                    break;
                case 3:
                    year = year1;
                    month = '0'+(month1-1);
                    day = year%4==0&&year%100!=0||year%400==0 ? day1+23 : day1+22;
                    break;
                case 5:
                case 7:
                case 10:
                case 12:
                    year = year1;
                    month = month1<=10 ? '0'+(month1-1) : (month1-1);
                    day = day1+24;
                    break;
            }

            month1 = month1<10?'0'+month1 : month1;
        }else{
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1<10 ? '0'+(date.getMonth()+1) : date.getMonth()+1;
            day1 = date.getDate();
            year = year1;
            month = month1;
            day = day1 < 16 ? '0'+(day1-6) : day1-6;

        };
        return (
            <Form inline>
                <FormItem label="打款时间:">
                    <RangePicker disabledDate={this.disabledDate} {...getFieldProps('time', { initialValue: this.state.initDate ? [year+'-'+month+'-'+day+' 00:00:00',year1+'-'+month1+'-'+day1+' 23:59:59'] : ''}) } />
                </FormItem>
                <FormItem label="收款人姓名:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('realName',{initialValue: ''})} />
                </FormItem>
                <FormItem label="手机号码:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('phone',{initialValue: ''})} />
                </FormItem>
                <FormItem label="状态:">
                    <Select style={{ width: 100 }} {...getFieldProps('state',{initialValue: ''})} placeholder='请选择...'>
                        <Option value="">全部</Option>
                        <Option value="10">待支付</Option>
                        <Option value="40">支付成功</Option>
                        <Option value="50">支付失败</Option>
                        <Option value="20">审核通过</Option>
                        <Option value="30">审核不通过</Option>
                        <Option value="15">待审核</Option>
                    </Select>
                </FormItem>
                <FormItem label="业务:">
                    <Select style={{ width: 100 }} {...getFieldProps('scenes',{initialValue: ''})} placeholder='请选择...'>
                        <Option value="">全部</Option>
                        <Option value="10">放款</Option>
                        <Option value="11">奖励</Option>
                        <Option value="12">退还</Option>
                        <Option value="20">还款</Option>
                        <Option value="21">补扣</Option>
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