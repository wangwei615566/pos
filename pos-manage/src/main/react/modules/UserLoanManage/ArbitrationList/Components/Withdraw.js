import React from 'react';
import {
  Button,
  Form,
  Input,
  InputNumber,
  Modal,
  Row,
  Col,
  Select,
  Checkbox,
  Radio,
  message,
  DatePicker,
  
} from 'antd';

const CheckboxGroup = Checkbox.Group
const createForm = Form.create;
const FormItem = Form.Item;
const Option = Select.Option;
const objectAssign = require('object-assign');
var Withdraw = React.createClass({
  getInitialState() {
    return {
       checked: true,
       disabled: false,
       data:"",
       timeString:"",
       record:""
    };
  },
  handleCancel() {
    this.props.form.resetFields();
    this.props.hideModal();
  },
  handleOk(){
	  var me = this;
      var props = me.props;
	  var record = props.record;
	  this.props.form.validateFields((errors, values) => {
      if (!!errors) {
        //console.log('Errors in form!!!');
        return;
      }
      //console.log("00000000000",values)
       Utils.ajaxData({
        url: "/modules/manage/borrow/revokerArbitrate.htm",
        data:{
        	loanBillNos:record.orderNo,
        	type:values.type,
        	reason:values.reason
        },
        callback: (result) => {
        	if(result.code==200){
        		Modal.success({
        			title: result.msg,
        		});
        	}else{
        		Modal.error({
        			title: result.msg,
        		});
        	}
            me.handleCancel();
        }
      });
      
      })
  },
  componentWillReceiveProps(nextProps) {
       this.setState({
         record:nextProps.record
       })
  }, 
  

  
  render() {
    const {
      getFieldProps
    } = this.props.form;
    var props = this.props;
    var state = this.state;
    
   
    const formItemLayout = {
      labelCol: {
        span: 9
      },
      wrapperCol: {
        span: 15
      },
    };
    const formItemLayoutone = {
      labelCol: {
        span: 9
      },
      wrapperCol: {
        span:15
      },
    };
    const formItemLayouttwo = {
      labelCol: {
        span: 6
      },
      wrapperCol: {
        span: 18
      },
    };
     var modalBtns = [
            <Button key="back" className="ant-btn" onClick={this.handleCancel}>返 回</Button>,
            <Button key="button" className="ant-btn ant-btn-primary" onClick={this.handleOk}>
                提 交
            </Button>
        ];
    return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="500" footer={modalBtns} maskClosable={false} >
         <Form horizontal  form={this.props.form}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden"/>
          <Row>
	          <Col span="20">
	              <FormItem  {...formItemLayout} label="撤回原因:">
	                  <Select {...getFieldProps('type', {initialValue:"0", rules: [{ required: true, message: '必填'}] }) } >
	                      <Option value={"0"}>不投诉</Option>
	                      <Option value={"1"}>已调解</Option>
	                      <Option value={"2"}>已还款</Option>
	                      <Option value={"3"}>其他</Option>
	                  </Select>             
	              </FormItem>
	          </Col>
	      </Row>
          <Row>
           <Col span="20">
               <FormItem {...formItemLayout} label="其他原因:">
                <Input  type="text" {...getFieldProps('reason', { initialValue: '',rules: [{required:true,message:'必填'}] }) }/>
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
});
Withdraw = createForm()(Withdraw);
export default Withdraw;