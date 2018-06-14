import React from 'react';
import {
  Button,
  Form,
  Input,
  Modal,
  Row,
  Col,
  Select,
  Tabs,
  Checkbox
} from 'antd';

import RuleReport from './RuleReport';
import Tab1 from './Tab1';
import Tab2 from './Tab2';
import Tab3 from './Tab3';
import Tab4 from './Tab4';
import Tab5 from './Tab5';
import Tab6 from './Tab6';

const createForm = Form.create;
const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
var confirm = Modal.confirm;
var Lookdetails = React.createClass({
  getInitialState() {
    return {
      checked: true,
      disabled: true,
      data: "",
      timeString: "",
      record: "",
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
  handleOk() {

    let me = this;
    let params = this.props.form.getFieldsValue();
    let record = this.state.record;
    this.props.form.validateFields((errors, values) => {
      if (!!errors) {
        //console.log('Errors in form!!!');
        return;
      }
      var tips = '是否确定提交';
      confirm({
        title: tips,
        onOk: function () {
          Utils.ajaxData({
//            url: '/modules/manage/borrow/verifyBorrow.htm', 旧
            url: '/modules/manage/borrow/verifyBorrowNew.htm', 
            data: { borrowId: record.id, state: params.state1, remark: params.remark,isEditPhoto:params.isEditPhoto },
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
  onchange(event){
	  this.props.form.setFieldsValue({'state1':event});
	  if(event==19){
		  this.setState({
			  disabled:false
		  })
	  }else{
		  this.setState({
			  disabled:true
		  })
		  
	  }
  },
  handleChange(event){
	  this.props.form.setFieldsValue({'remark':event});
  },
  render() {
    const {
      getFieldProps
    } = this.props.form;
    var props = this.props;
    var state = this.state;
    var modalBtns = [
      <button key="back" className="ant-btn" onClick={this.handleCancel}>取消</button>,
      <button key="sure" className="ant-btn ant-btn-primary" onClick={this.handleOk}>确定</button>
    ];
    var modalBtnstwo = [
      <button key="back" className="ant-btn" onClick={this.handleCancel}>关闭</button>,
    ];
    const remarks = [<Option key="照片不清晰">照片不清晰</Option>];
    const formItemLayout = {
      labelCol: {
        span: 8
      },
      wrapperCol: {
        span: 12
      },
    };
    const formTailLayout = {
		  labelCol: { span: 8 },
		  wrapperCol: { span: 12, offset: 8 },
	};
    const formItemLayouttwo = {
      labelCol: {
        span: 4
      },
      wrapperCol: {
        span: 20
      },
    };

    return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="1200" footer={props.title == "查看" ? [modalBtnstwo] : [modalBtns]} maskClosable={false} >

        <Tabs defaultActiveKey="1"  >
	      <TabPane tab="规则报告" key='1'>
	        <RuleReport record={this.props.record} visible={props.visible} />
	      </TabPane>
          <TabPane tab="基本信息" key="2">
            <Tab1 record={props.record} dataForm={props.dataForm} ref="Tab1" canEdit={props.canEdit} visible={props.visible} recordSoure={props.recordSoure} />
          </TabPane>
          <TabPane tab="通讯录" key="3">
            <Tab2 record={props.record} ref="Tab2" canEdit={props.canEdit} visible={props.visible} />
          </TabPane>
          <TabPane tab="通话记录" key="4">
            <Tab3 record={props.record} ref="Tab3" canEdit={props.canEdit} visible={props.visible} />
          </TabPane>
          <TabPane tab="短信记录" key="5">
            <Tab4 record={props.record} ref="Tab4" canEdit={props.canEdit} visible={props.visible} />
          </TabPane>
          <TabPane tab="借款记录" key="6">
            <Tab6 record={props.record} ref="Tab6" canEdit={props.canEdit } visible={props.visible} />
          </TabPane>
          <TabPane tab="风控报告" key="7">
            <Tab5 record={props.riskRecord} ref="Tab5" canEdit={props.canEdit} visible={props.visible} />
          </TabPane>
        </Tabs>
        <Form horizontal form={this.props.form}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden" />
          {props.dataRecord ? <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label={props.dataRecord.respCode == 200 ? '审核请求成功，审核结果:' : '审核请求失败，错误信息:'}>
                <span>{props.dataRecord.rsDesc}</span>
              </FormItem>
            </Col>
          </Row> : ''}
          <Row>
          <Col span="24">
          	<FormItem {...formTailLayout}>
          		<Checkbox {...getFieldProps('isEditPhoto', {initialValue: "" })} disabled={this.state.disabled} >是否因照片不符合拒绝</Checkbox>
          	</FormItem>
          </Col>
          </Row>
          <Row>
            <Col span="24">
              <FormItem  {...formItemLayout} label="审批意见:">
                {props.title != "查看" ? (
                  <Select  {...getFieldProps('state1', { initialValue: "19" }) } disabled={!props.canEdit} onChange={this.onchange}>
                    {/*<Option value="22">人工复审通过</Option>*/}
                    <Option value="19">人工复审拒绝</Option>
                  </Select>) : (<Input type="text" disabled={!props.canEdit} {...getFieldProps('stateStr') } />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="24">
              <FormItem  {...formItemLayout} label="备注说明:">
              <Select combobox {...getFieldProps('remark', { initialValue: "" }) } disabled={!props.canEdit}>
              <Option value="手持身份证照片不合格">手持身份证照片不合格</Option>
              <Option value="身份证正面照不合格">身份证正面照不合格</Option>
              <Option value="身份证反面照不合格">身份证反面照不合格</Option>
              <Option value="照片与本人相差较大">照片与本人相差较大</Option>
              <Option value="照片非本人">照片非本人</Option>
            </Select>
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