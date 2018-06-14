import React from 'react'
import {
  Table,
  Modal,
  Form,
  Select,
  Input,
  Row,
  Col,
  Button,
} from 'antd';
const createForm = Form.create;
const FormItem = Form.Item;
const Option = Select.Option;
const objectAssign = require('object-assign');
var AdjustCreditDetial = React.createClass({
  getInitialState() {
    return {
      loading: false,
      data: [],
      canEdit: true,
      visible: false,
      name:"",
      refresh:false,
    };
  },
  hideModal() {
    this.setState({
      visible: false,
      loading: false,
    });
    this.props.hideModal();
  },
  componentWillReceiveProps(nextProps, nextState) {
	    this.setState({
	    	record: nextProps.record,
	    	visible:nextProps.blackVisible,
	    })	
  },
  handleOk(){					  		
        Utils.ajaxData({
          url: "/modules/manage/user/updateState.htm",
          data: {
            id: this.state.record.id,
            state: "10",
            blackReason:this.props.form.getFieldsValue().blackReason,
            blackDesc:this.props.form.getFieldsValue().blackDesc,
          },
          method: 'post',
          callback: (result) => {
            if (result.code == 200) {
              Modal.success({
                title: result.msg,
              });
            } else {
              Modal.error({
                title: result.msg,
              });
            }
          }
        }); 	
        this.props.form.resetFields();
        this.props.hideModal();
  },  
  render() {
	  var modalBtns = [
	  <button key="go" className="ant-btn" onClick={this.handleOk}>保存</button>,
	    <button key="back" className="ant-btn" onClick={this.hideModal}>取消</button>
	      ];
	 const {
	      getFieldProps
	    } = this.props.form;
	    const formItemLayout = {
	            labelCol: {
	                span: 9
	            },
	            wrapperCol: {
	                span: 14
	            },
	        };
    return (
      <Modal  visible={this.state.visible} onCancel={this.hideModal}  width="800"  footer={modalBtns} maskClosable={false} >  
     
      	<div  style={{marginLeft:"40%",fontSize:"20px"}}>
      	<p >添加黑名单</p>
      	</div>
      	 <div style={{ position: "relative" }}>
         <Form horizontal form={this.props.form} style={{ marginTop: "30px" }}>
      	<Col span="15">
        <FormItem  {...formItemLayout} style={{ marginLeft: "90px" }} label="列入黑名单原因:" > 
            <Select style={{ width: '100%' }}  {...getFieldProps('blackReason',{ initialValue: '还款态度消极',rules: [{ required: true, message: '必填' }] })}>
            <Option value="还款态度消极">还款态度消极</Option>
            <Option value="多头借贷">多头借贷</Option>
            <Option value="逾期14天及以上">逾期14天及以上</Option>
            <Option value="伪造信息/虚假信息">伪造信息/虚假信息</Option>
            <Option value="暴力威胁">暴力威胁</Option>
            <Option value="拖欠/恶意欠款">拖欠/恶意欠款</Option>
            <Option value="其它">其它</Option>
        </Select>
        </FormItem>
    </Col>
    <Col span="21">
        <FormItem  labelCol={{span: 6}} wrapperCol={{span:16}} label="备注:">
            <Input type="textarea" {...getFieldProps('blackDesc') } />
        </FormItem>
    </Col>  
    </Form>
    </div>
     </Modal>
    );
  }
});
AdjustCreditDetial = createForm()(AdjustCreditDetial);
export default AdjustCreditDetial;