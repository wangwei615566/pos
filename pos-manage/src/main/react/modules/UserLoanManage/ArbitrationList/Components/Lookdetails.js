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
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="600" footer={[<button key="back" className="ant-btn" onClick={this.handleCancel}>关闭</button>]} maskClosable={false} >
        <Form horizontal form={this.props.form}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden" />
          <Row>
            <Col span="12">
              <FormItem  {...formItemLayout} label="订单号:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('orderNo', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
	          <FormItem {...formItemLayout} label="姓名:">
	            <Input disabled={!props.canEdit} type="text" {...getFieldProps('realName', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
	          </FormItem>
	        </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="身份证号:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('idNo', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
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
              <FormItem {...formItemLayout} label="案件金额:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('caseAmount', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款本金(元):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('amount', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem  {...formItemLayout} label="借款天数(天):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('timeLimit', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
	        <Col span="12">
	          <FormItem {...formItemLayout} label="利息(元):">
	            <Input disabled={!props.canEdit} type="text" {...getFieldProps('interest', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
	          </FormItem>
	        </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="利息利率(%):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('interestRatio', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
	        <Col span="12">
	          <FormItem {...formItemLayout} label="服务费(元):">
	            <Input disabled={!props.canEdit} type="text" {...getFieldProps('serviceFee', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
	          </FormItem>
	        </Col>
	        <Col span="12">
	          <FormItem {...formItemLayout} label="服务费利率(%):">
	            <Input disabled={!props.canEdit} type="text" {...getFieldProps('serviceFeeRatio', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
	          </FormItem>
	        </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款开始日期:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('createTime', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="借款应还日期:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps("repayTime", { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem  {...formItemLayout} label="逾期天数(天):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('penaltyDay', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem  {...formItemLayout} label="逾期利率(%):">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('overduePenaltyRatio', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="案件进程:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('process', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="案件状态:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('processStatus', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          
          {state.record.processStatus=="案件已撤回"?<div><Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="撤回原因:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('cancelType', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
	        <Col span="12">
	          <FormItem {...formItemLayout} label="其它原因:">
	            <Input disabled={!this.props.canEdit} type="text" {...getFieldProps('cancelReason', { initialValue: '' }) } style={{border:'none', background:'#FFF', display:'block'}}/>
	          </FormItem>
	        </Col>
          </Row></div>:""}
        </Form>
      </Modal>
    );
  }
});
Lookdetails = createForm()(Lookdetails);
export default Lookdetails;