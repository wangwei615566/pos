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

var confirm = Modal.confirm;
const CheckboxGroup = Checkbox.Group
const createForm = Form.create;
const FormItem = Form.Item;
const Option = Select.Option;
const objectAssign = require('object-assign');
var AddWin = React.createClass({
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
  componentWillReceiveProps(nextProps) {
    this.setState({
      record: nextProps.record
    })
  },
  
  handleOk(){
	let me =this;
    let params = this.props.form.getFieldsValue();
    let record = this.state.record;
      this.props.form.validateFields((errors, values) => {
      if (!!errors) {
        //console.log('Errors in form!!!');
        return;
      }
      var msg = '提交成功';
      var tips = '您是否确认提交?'
      confirm({
        title: tips,
        onOk:function(){
              Utils.ajaxData({
                url: "/modules/manage/borrow/verifyBorrowLoan.htm",
                data:{ borrowId: record.id, state: params.state1, remark: params.state1 },
                method: 'post',
                callback: (result) => {
                  if (result.code == 200) {
                      me.handleCancel();
                      Modal.success({
                        title: msg,
                    });
                  }else{
                     Modal.error({
                            title:  result.msg,
                        });
                  }
                  
                }
            });
          },
        onCancel:function(){}
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
            <Button key="button" className="ant-btn ant-btn-primary" loading={state.loading}  onClick={this.handleOk}>
                提 交
            </Button>
        ];
    return (
      <Modal style={{ left: '-100px'}} title={props.title} visible={props.visible} onCancel={this.handleCancel} width="900" footer={modalBtns} maskClosable={false} >
         <Form horizontal  form={this.props.form}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden"   />
        	  <Row>
          <Col span="12">
            <FormItem  {...formItemLayout} label="订单号:">
              <Input disabled={true} type="text" {...getFieldProps('orderNo', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem {...formItemLayout} label="借款人姓名:">
              <Input disabled={true} type="text" {...getFieldProps('realName', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem  {...formItemLayout} label="手机号码:">
              <Input disabled={true} type="text" {...getFieldProps('phone', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem {...formItemLayout} label="借款期限(天):">
              <Input disabled={true} type="text" {...getFieldProps('timeLimit', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem {...formItemLayout} label="借款所在地:">
              <Input style={{ width: '600px'}} disabled={true} type="text" {...getFieldProps('address', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem {...formItemLayout} label="借款所在经纬度:">
              <Input disabled={true} type="text" {...getFieldProps('coordinate', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem  {...formItemLayout} label="借款金额(元):">
              <Input disabled={true} type="text" {...getFieldProps('amount', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem {...formItemLayout} label="综合费用(元):">
              <Input disabled={true} type="text" {...getFieldProps('fee', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem {...formItemLayout} label="服务费(元):">
              <Input disabled={true} type="text" {...getFieldProps('serviceFee', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem {...formItemLayout} label="信息认证费(元):">
              <Input disabled={true} type="text" {...getFieldProps('infoAuthFee', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem {...formItemLayout} label="借款利息(元):">
              <Input disabled={true} type="text" {...getFieldProps('interest', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem {...formItemLayout} label="实际到账金额(元):">
              <Input disabled={true} type="text" {...getFieldProps('realAmount', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem {...formItemLayout} label="借款时间:">
              <Input disabled={true} type="text" {...getFieldProps('createTime', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem  {...formItemLayout} label="放款时间:">
              <Input disabled={true} type="text" {...getFieldProps('loanTime', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem  {...formItemLayout} label="逾期天数(天):">
              <Input disabled={true} type="text" {...getFieldProps('penaltyDay', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem {...formItemLayout} label="逾期金额(元):">
              <Input disabled={true} type="text" {...getFieldProps('penaltyAmout', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem {...formItemLayout} label="实际还款时间:">
              <Input disabled={true} type="text" {...getFieldProps('repayTime', { initialValue: '' }) } />
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span="12">
            <FormItem {...formItemLayout} label="实际还款金额(元):">
              <Input disabled={true} type="text" {...getFieldProps('repayAmount', { initialValue: '' }) } />
            </FormItem>
          </Col>
          <Col span="12">
	          <FormItem {...formItemLayout} label="审核:">
	                   <Select type="text" {...getFieldProps('state1', {initialValue: '27'}) } >
	                         {/*<Option value="26">审核通过</Option>*/}
	                         <Option value="27">审核不通过</Option>
	                   </Select>
	         </FormItem>
	       </Col>
         </Row>
        </Form>
      </Modal>
    );
  }
});
AddWin = createForm()(AddWin);
export default AddWin;