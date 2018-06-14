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
var Lookdetails = React.createClass({
  getInitialState() {
    return {
      checked: true,
      disabled: false,
      data: "",
      timeString: "",
      record: ""
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

  onChange(time, timeString) {

    //console.log(time, timeString);
    this.setState({
      timeString: timeString,
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
        span: 15
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
    return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="900" footer={[<button key="back" className="ant-btn" onClick={this.handleCancel}>关闭</button>]} maskClosable={false} >
        <Form horizontal form={this.props.form}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden" />
          <Row>
            <Col span="12">
              <FormItem  {...formItemLayout} label="订单号:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('orderNo', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款人姓名:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('realName', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem  {...formItemLayout} label="手机号码:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('phone', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款期限(天):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('timeLimit', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款所在地:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('address', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block', width: '600px'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款所在经纬度:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('coordinate', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem  {...formItemLayout} label="借款金额(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('amount', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="综合费用(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('fee', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="服务费(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('serviceFee', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="信息认证费(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('infoAuthFee', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款利息(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('interest', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="实际到账金额(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('realAmount', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款时间:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('createTime', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem  {...formItemLayout} label="放款时间:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('loanTime', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem  {...formItemLayout} label="逾期天数(天):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('penaltyDay', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="逾期金额(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('penaltyAmout', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="实际还款时间:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('repayTime', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="实际还款金额(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('repayAmount', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
	          <FormItem {...formItemLayout} label="逾期管理费(元):">
	            <Input disabled={!this.props.canEdit} type="text" {...getFieldProps('penaltyManageAmt', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
	          </FormItem>
	        </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
});
Lookdetails = createForm()(Lookdetails);
export default Lookdetails;