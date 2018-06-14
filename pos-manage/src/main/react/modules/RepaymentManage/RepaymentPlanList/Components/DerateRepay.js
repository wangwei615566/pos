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
  Switch,

} from 'antd';
const CheckboxGroup = Checkbox.Group;
const createForm = Form.create;
const FormItem = Form.Item;
var confirm = Modal.confirm;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const objectAssign = require('object-assign');
var Lookdetails = React.createClass({
  getInitialState() {
    return {
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
  handleOk(e) {
	e.preventDefault();
    var me = this;
    var record = me.props.record;
    var re = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    var re1 = /^(\d|[1-9]\d+)(\.\d+)?$/;
    this.props.form.validateFields((errors, values) => {
      if (!values.derateAmount || !re1.test(values.derateAmount)||values.derateAmount>record.borrowAmount ||values.derateAmount<0) {
        Modal.error({
          title: "减免金额必须是小于等于借款金额的数",
        });
        return;
      }
      if (!values.derateRemark) {
    	  Modal.error({
              title: "备注信息不能为空",
            });
            return;
      }
      var tips = "您是否确定提交";
      confirm({
        title: tips,
        onOk: function () {
          Utils.ajaxData({
            url: "/modules/manage/borrow/repay/doDerateRepay.htm",
            data: {
              repayId: record.id,
              derateAmount:values.derateAmount,
              derateRemark:values.derateRemark
            },
            callback: (result) => {
                if (result.code == 200) {
                  me.handleCancel();
                };
                let resType = result.code == 200 ? 'success' : 'warning';
                Modal[resType]({
                  title: result.msg,
                });
              }
          });
        },
        onCancel: function () { }
      })
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
        span: 4
      },
      wrapperCol: {
        span: 20
      },
    };
      var modalBtns = [
      <Button key="back" className="ant-btn" onClick={this.handleCancel}>返 回</Button>,
      <Button key="button" className="ant-btn ant-btn-primary" onClick={this.handleOk}>
        提 交
            </Button>
    ];

      return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="700" footer={modalBtns} maskClosable={false} >
        <Form horizontal form={this.props.form}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden" />
          <Row>
            <Col span="18">
              <FormItem {...formItemLayout}  label="借款金额:">
              	<Input disabled={!props.canEdit} style={{ width: '300' }} {...getFieldProps('borrowAmount', { rules: [{ required: true, message: '必填' }] }) } type="text"/>
              </FormItem>
            </Col>
         </Row>
         <Row>
            <Col span="18">
            <FormItem {...formItemLayout} label="逾期罚金:">
            	<Input disabled={!props.canEdit} style={{ width: '300' }} {...getFieldProps('penaltyAmout', { rules: [{ required: true, message: '必填' }] }) } type="text"/>
            </FormItem>
          </Col>
          </Row>
          <Row>
	          <Col span="18">
	            <FormItem {...formItemLayout} label="逾期天数:">
	            	<Input disabled={!props.canEdit} style={{ width: '300' }} {...getFieldProps('penaltyDay', { rules: [{ required: true, message: '必填' }] }) } type="text"/>
	            </FormItem>
	          </Col>
	      </Row>
          <Row>
	          <Col span="18">
	          <FormItem {...formItemLayout} label="逾期管理费:">
	          	<Input disabled={!props.canEdit} style={{ width: '300' }} {...getFieldProps('penaltyManageAmt', { rules: [{ required: true, message: '必填' }] }) } type="text"/>
	          </FormItem>
	        </Col>
	      </Row>
	      <Row>
	          <Col span="18">
	            <FormItem {...formItemLayout} label="应还款金额:">
	            	<Input disabled={!props.canEdit} style={{ width: '300' }} {...getFieldProps('repayAmount', { rules: [{ required: true, message: '必填' }] }) } type="text"/>
	            </FormItem>
	          </Col>
	      </Row>
          <Row>
            <Col span="18">
              <FormItem {...formItemLayout} label="减免金额:">
                <Input type="text"  style={{ width: '300' }} placeholder="请输入减免金额" {...getFieldProps('derateAmount', { rules: [{ required: true, message: "必填"}] }) } />
              </FormItem>
            </Col>
          </Row>
          <Row>
          <Col span="18">
            <FormItem {...formItemLayout} label="备注信息:">
              <Input type="text"  style={{ width: '300' }} placeholder="请输入备注信息" {...getFieldProps('derateRemark', { rules: [{ required: true, message: "必填"}] }) }/>
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