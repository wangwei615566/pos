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
var Tab2 = React.createClass({
  getInitialState() {
    return {
      checked: true,
      disabled: false,
      data: "",
      timeString: "",
      record: ""
    };
  },
  componentWillReceiveProps(nextProps) {
    this.setState({
      record: nextProps.record,
      dataRecord:nextProps.dataRecord
    },function(){
//    	this.props.form.setFieldsValue(nextProps.dataRecord);
    });    
  },
  componentWillMount() {
	    // this.props.form.resetFields();
//	  console.log(this.props.dataRecord);
//	  console.log('++++++++++++++++++++++++++');
//	  console.log(this.props);
	  
	    this.props.form.setFieldsValue(this.props.dataRecord);
	  },
  onChange(time, timeString) {

    //console.log(time, timeString);
    this.setState({
      timeString: timeString,
    })
  },
  render() {
	  var props = this.props;
	  var state = this.state;
    const {
      getFieldProps
    } = this.props.form;


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
        <Form horizontal form={this.props.form} style={{background:"url('/dev/images/bg.png')",backgroundSize:'cover', display:'block',paddingTop:'100px',paddingBottom:'150px'}}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden" />
          <div height="200px" width="200"></div>
          <Row>
            <Col span="12">
              <FormItem  {...formItemLayout} label="交易号:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('payOrderNo', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}} />
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="申请时间:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('createTime', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem  {...formItemLayout} label="付款方全称:">
                <Input disabled={!props.canEdit} type="text" defaultValue="武汉成长无限网络科技有限公司" style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="收款方全称:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('realName', { initialValue: '' }) } style={{border:'none', background:'none', display:'block',width: '600px'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="收款方开户单位:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('bank', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem  {...formItemLayout} label="收款方账号:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('cardNo', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="大写金额:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('capitalAmount', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="小写金额:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('amountSt', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="交易类型:">
                <Input disabled={!props.canEdit} type="text" defaultValue="商户付款" style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="账户类型:">
                <Input disabled={!props.canEdit} type="text" defaultValue="人民币账户" style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="12">
              <FormItem {...formItemLayout} label="成功时间:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('payUpdateTime', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
            <Col span="12">
              <FormItem {...formItemLayout} label="电子回单编号:">
                <Input disabled={!props.canEdit} type="text" {...getFieldProps('payOrderNo', { initialValue: '' }) } style={{border:'none', background:'none', display:'block'}}/>
              </FormItem>
            </Col>
          </Row>
        </Form>
    );
  }
});
Tab2 = createForm()(Tab2);
export default Tab2;