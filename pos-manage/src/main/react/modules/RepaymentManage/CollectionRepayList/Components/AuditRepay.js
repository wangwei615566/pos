import React from 'react';
import {
  Button,
  Form,
  Input,
  Modal,
  Row,
  Col,
  Select,
  message,
} from 'antd';
const createForm = Form.create;
const FormItem = Form.Item;
var confirm = Modal.confirm;
const Option = Select.Option;
var AuditRepay = React.createClass({
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
  handleOk() {
    var me = this;
    this.props.form.validateFields((errors, values) => {
	  if (values.state != '1' && values.state != '2') {
	        Modal.error({
	          title: "请审核该订单",
	        });
	        return;
	  }
	  if(values.reason == "" || values.reason == null){
		  	Modal.error({
	          title: "请填写审核备注",
	        });
	        return;
	  }
      var tips = "您是否确定提交";
      confirm({
        title: tips,
        onOk: function () {
          Utils.ajaxData({
            url: "/modules/manage/collection/check.htm",
            data: {
              dueId: me.props.record.dueId,        
              state: values.state,
              reason:values.refuseReason
            },
            callback: (result) => {
              if (result.code == 200) {
                me.handleCancel();
              };
              let resType = result.code == 200 ? 'success' : 'warning';
              Modal[resType]({
                title: result.msg
              });
            }
          });
        },
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
      var fileUrlList = [];
      if(props.record.fileUrl != null && props.record.fileUrl != 'undefined' && props.record.fileUrl != ''){
    	  let urlList = props.record.fileUrl.split(",");
    	  for(let i=0; i<urlList.length; i++){
    		  fileUrlList.push(
    		<Row>
	        <Col span="24">
	        <FormItem  {...formItemLayout} label="申请文件:"><a target="_blank" href={urlList[i]}>申请文件[1]</a>
	        </FormItem>
	        </Col>
	        </Row>)
    	  }
      }
      return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="700" footer={modalBtns} maskClosable={false} >
        <Form horizontal form={this.props.form}>		
	        <Row>
	        <Col span="24">
	        <FormItem  {...formItemLayout} label="还款类型:">
	        <Select {...getFieldProps('repayType', { initialValue: '' })} disabled={true}>
	        <Option value="41">部分逾期还款</Option>
	        <Option value="42">全额逾期还款</Option>
	        <Option value="43">减免逾期还款</Option>
	        </Select>
	        </FormItem>
	        </Col>
	        </Row>
	        <Row>
	        <Col span="24">
	        <FormItem  {...formItemLayout} label="应还款金额(元):">
	        	<Input  {...getFieldProps('repayAmount') } type="text" disabled={true}/>
	        </FormItem>
	        </Col>
	        </Row>
	        <Row>
	        <Col span="24">
	        <FormItem  {...formItemLayout} label="对公还款金额(元):">
	        <Input  {...getFieldProps('money') } type="text" disabled={true}/>
	        </FormItem>
	        </Col>
	        </Row>
	        <Row>
	        <Col span="24">
	        <FormItem  {...formItemLayout} label="催收员备注:">
	        <Input  {...getFieldProps('applyRemark') } type="text" disabled={true}/>
	        </FormItem>
	        </Col>
	        </Row>
	        <Row>
	        <Col span="24">
	        <FormItem  {...formItemLayout} label="审批人备注:">
	        <Input  {...getFieldProps('remark') } type="text" disabled={true}/>
	        </FormItem>
	        </Col>
	        </Row>
	        <Row>
	        <Col span="24">
	        <FormItem  {...formItemLayout} label="审核:">
	        <Select {...getFieldProps('state', { rules: [{ required: true, message: '必填' }] })} >
		        <Option value="0">未确认</Option>
		        <Option value="1">同意</Option>
		        <Option value="2">拒绝</Option>
	        </Select>
	        </FormItem>
	        </Col>
	        </Row>
        	  <Row>
	          <Col span="24">
	          <FormItem  {...formItemLayout} label="备注:">
	              <Input  placeholder="请输入备注信息" {...getFieldProps('refuseReason', { rules: [{ required: true, message: '必填' }] }) } type="text" />
	          </FormItem>
	           </Col>
	           </Row>
	           {fileUrlList}
        </Form>
        
      </Modal>
    );
  }
});
AuditRepay = createForm()(AuditRepay);
export default AuditRepay;