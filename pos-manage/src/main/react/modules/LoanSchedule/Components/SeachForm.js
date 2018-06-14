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
        var json = {startDate:'',endDate:'',realName:params.realName,phone:params.phone,orderNo:params.orderNo,state:params.state};
        //console.log(params);
        if(params.time[0]){
            var d = new Date(params.time[0]);
            json.startDate=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()+' 00:00:00';
            var d1 = new Date(params.time[1]);
            json.endDate=d1.getFullYear() + '-' + (d1.getMonth() + 1) + '-' + d1.getDate()+' 23:59:59';
            //console.log(d);
        }
        this.props.passParams({
            searchParams: JSON.stringify(json),
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
    disabledDate(startValue) {
        var today = new Date();
        return startValue.getTime() > today.getTime();
    },
    render() {
        const {getFieldProps} = this.props.form;
        var year, month, day;
        var year1, month1, day1;
        var date = new Date();
        if(date.getDate() < 3 ){
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1;
            day1 = date.getDate();
            switch(month1){
                case 1:
                    year = year1-1;
                    month = 12;
                    day = day1+29;
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                    year = year1;
                    month = month1<10 ? '0'+(month1-1) : (month1-1);
                    day = day1+29;
                    break;
                case 3:
                    year = year1;
                    month = '0'+(month1-1);
                    day = year%4==0&&year%100!=0||year%400==0 ? day1+27 : day1+26;
                    break;
                case 5:
                case 7:
                case 10:
                case 12:
                    year = year1;
                    month = month1<=10 ? '0'+(month1-1) : (month1-1);
                    day = day1+28;
                    break;
            }
            month1 = month1<10?'0'+month1 : month1;

        }else{
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1<10 ? '0'+(date.getMonth()+1) : date.getMonth()+1;
            day1 = date.getDate();
            year = year1;
            month = month1;
            day = day1 < 12 ? '0'+(day1-2) : day1-2;
        };
        return (
            <Form inline>
                <FormItem label="业务处理时间:">
                    <RangePicker disabledDate={this.disabledDate} {...getFieldProps('time', { initialValue: this.state.initDate ? [year+'-'+month+'-'+day+' 00:00:00',year1+'-'+month1+'-'+day1+' 23:59:59'] : ''}) } />
                </FormItem>
                <FormItem label="真实姓名:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('realName')} />
                </FormItem>
                <FormItem label="手机号码:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('phone')} />
                </FormItem>
                <FormItem label="订单号:">
                    <Input onPressEnter={this.handleQuery} {...getFieldProps('orderNo')} />
                </FormItem>
                <FormItem label="状态:">
                    <Select style={{ width: 170 }} {...getFieldProps('state',{initialValue: ''})} placeholder='请选择...'>
                        <Option value="10">申请成功待审核</Option>
                        <Option value="18">待人工审核</Option>
                        <Option value="19">人工审核不通过</Option>
                        <Option value="20">自动审核通过</Option>
                        <Option value="21">自动审核不通过</Option>
                        <Option value="22">人工复审通过</Option>
                        <Option value="26">放款审核通过</Option>
                        <Option value="27">放款审核不通过</Option>
                        <Option value="30">待还款</Option>
                        <Option value="31">放款失败</Option>
                        <Option value="40">还款成功</Option>
                        <Option value="41">还款成功-金额减免</Option>
                        <Option value="50">逾期</Option>
                        <Option value="90">坏账</Option>
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