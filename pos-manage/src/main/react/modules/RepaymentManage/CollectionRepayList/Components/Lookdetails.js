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
      value: 1,
      status:0,
      startValue: null,
        count: 60,
        liked: true,
        flag: false
    };
  },
  handleCancel() {
    this.setState({
    	value:1,
    	status:0,
    	startValue: null,
    	count:0,
    	liked :true,
    	flag:false,
    })
    this.props.form.resetFields();
    this.props.hideModal();
  },
  componentWillReceiveProps(nextProps) {
    this.setState({
      record: nextProps.record
    })
  },
  disabledStartDate(startValue) {
    var today = new Date();
    var loanDay = new Date(this.props.record.borrowTime);
    return startValue.getTime() > today.getTime() || startValue.getTime() < loanDay.getTime();
  },

  handleGetCode() {
    var params = this.props.form.getFieldsValue();
      Utils.ajaxData({
          url: '/modules/manage/borrow/repay/getCode.htm',
          data: {
            phone:params.mobile
          },
          callback: (result) => {
              if (result.code == 200) {
                  if(this.state.liked){//获取验证码60s倒计时
                      this.timer = setInterval(function () {
                          var count = this.state.count;
                          this.state.liked = false;
                          this.state.flag=true;
                          count -= 1;
                          if (count < 1) {
                              this.setState({
                                  liked: true,
                                  flag:false
                              });
                              count = 60;
                              clearInterval(this.timer);
                          }
                          this.setState({
                              count: count
                          });
                      }.bind(this), 1000);
                  }
              };
              this.setState({
                  data: result.data
              });
              let resType = result.code == 200 ? 'success' : 'warning';
              Modal[resType]({
                  title: result.msg,
              });
          }
      });
  },
  handleOk() {
    var me = this;
    this.props.form.validateFields((errors, values) => {
	  	  if (me.state.status != '1' && me.state.status != '2') {
	  	        Modal.error({
	  	          title: "请审核该订单",
	  	        });
	  	        return;
	  	  }
	  	  if(values.refuseReason == "" || values.refuseReason == null){
	  		  	Modal.error({
	  	          title: "请填写审核备注",
	  	        });
	  	        return;
	  	  }
		  	if(me.state.status == 1){
		  		 var re = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		 	    var re1 = /^(\d|[1-9]\d+)(\.\d+)?$/;
		 	    var re2 = /^\d+$/;
			      if (re.test(values.repayAccount) && values.repayWay != '30') {
			        Modal.error({
			          title: "账号与方式不符,请核对",
			        });
			        return;
			      }
		  	 }  
	    if(me.state.status == 1){ 
		      Utils.ajaxData({
		        url: "/modules/manage/borrow/repay/confirmRepay.htm",
		        data: {
		          id: values.repayId,
		          repayTime: DateFormat.formatDate(values.repayTimes),
		          repayAccount: values.repayAccount,
		          repayWay: values.repayWay,
		          serialNumber: values.serialNumber,
		          amount: me.props.record.repayAmount,
		          penaltyAmout: me.props.record.penaltyAmout ,
		          deratereAmount:0,
		          derateDay:0,
		          state: 10,
		          vcode:values.vcode,
		          phone:values.mobile
		        },
		        callback: (result) => {
		          if (result.code == 200) {
		            me.handleCancel();
		            Utils.ajaxData({
		                url: "/modules/manage/collection/check.htm",
		                data: {
		                  dueId: me.props.record.dueId,     
		                  state: me.state.status,
		                  reason:values.refuseReason
		                },
		                callback: (result) => {
		                  if (result.code == 200) {
		                    me.handleCancel();
		                  }; 
		                }
		              });
		          }
		          let resType = result.code == 200 ? 'success' : 'warning';
		          Modal[resType]({
		            title: result.msg,
		          });
		        }
		      }); 
	    } else {
	    	var tips = "您是否确定提交";
	        confirm({
	          title: tips,
	          onOk: function () {
	            Utils.ajaxData({
	              url: "/modules/manage/collection/check.htm",
	              data: {
	                dueId: me.props.record.dueId,        
	                state: me.state.status,
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
	    } 
    })
  },
  onChange1(e) {
    this.setState({
      value: e.target.value,
    });
  },
  onChange2(e) {
	  this.setState({
		  status: e.target.value,
	  });
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
      var text = this.state.liked ? '获取验证码' : this.state.count + '秒后重发';
      var flag = this.state.flag;
      var fileUrlList = [];
      if(props.record.fileUrl != null && props.record.fileUrl != 'undefined' && props.record.fileUrl != ''){
    	  let urlList = props.record.fileUrl.split(",");
    	  for(let i=0; i<urlList.length; i++){
    		  fileUrlList.push(
	      <a style={{margin:'20px',color:"blue"}} target="_blank" href={urlList[i]}>申请文件[1]</a>)
    	  }
      }
      return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="800" footer={modalBtns} maskClosable={false} >
        <Form horizontal form={this.props.form}>
          <Input  {...getFieldProps('repayId', { initialValue: '' }) } type="hidden" />
        {/*  <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="还款选择:">
                <RadioGroup onChange={this.onChange1} value={this.state.value}>
                  <Radio key="a" value={1}>对公还款</Radio>
                  <Radio key="b" value={2}>对公减免还款</Radio>
                </RadioGroup>
              </FormItem>
            </Col>
          </Row>*/}
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
	        <FormItem   {...formItemLayout}  label="对公还款金额(元):">
	        <Input  style={{width:"100"}} {...getFieldProps('money') } type="text" disabled={true}/>
	        	{fileUrlList}
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
              <FormItem {...formItemLayout} label="审核:">
                <RadioGroup onChange={this.onChange2} value={this.state.status}>
                  <Radio key="a" value={1}>同意</Radio>
                  <Radio key="b" value={2}>拒绝</Radio>
                </RadioGroup>
              </FormItem>
            </Col>
          </Row>
	           <div>
		          {state.status == 1 ? 
		        	<Row>
		            <Col span="24">
		              <FormItem {...formItemLayout} label="还款账号:">
		                <Input type="text" placeholder="请输入银行卡号或者支付宝账号" {...getFieldProps('repayAccount', { rules: [{ required: true, message: "输入过多或者未输", max: 20 }] }) } />
		              </FormItem>
		            </Col>
		            <Col span="24">
		              <FormItem {...formItemLayout} label="还款方式:">
		                <Select placeholder="请选择" {...getFieldProps('repayWay', { rules: [{ required: true, message: '必填', type: 'string' }] }) } >
		                  <Option value="20">银行卡转账</Option>
		                  <Option value="30">支付宝转账</Option>
		                </Select>
		              </FormItem>
		            </Col>
		            <Col span="24">
		              <FormItem {...formItemLayout} label="流水号:">
		                <Input type="text" placeholder="请输入流水号" {...getFieldProps('serialNumber', { rules: [{ required: true, message: "输入过多或者未输", max: 50 }] }) } />
		              </FormItem>
		            </Col>
		         {/* <Row>
			          <Col span="24">
			            <FormItem {...formItemLayout} label="应还金额:">
			              <Input type="text" disabled={!props.canEdit} {...getFieldProps('repayAmount') } />
			            </FormItem>
			          </Col>
			      </Row>
			      <Row>
				      <Col span="24">
				          <FormItem {...formItemLayout} label="逾期罚金:">
				            <Input type="text" disabled={!props.canEdit}  {...getFieldProps('penaltyAmout') } />
				          </FormItem>
				      </Col>
		          </Row>
		          <Row>
			          <Col span="24">
				          <FormItem {...formItemLayout} label="逾期天数:">
				            <Input type="text" disabled={!props.canEdit}  {...getFieldProps('penaltyDay') } />
				          </FormItem>
				      </Col>
			      </Row>*/}
			       {/* <div>
			          {state.value == 2 ? <Row>
			            <Col span="24">
			                <FormItem  {...formItemLayout} label="减免金额:">
			                    <Input  placeholder="请输入减免金额，减免金额不能大于逾期罚金" {...getFieldProps('deratereAmount', { rules: [{ required: true, message: '必填' }] }) } />
			                </FormItem>
			            </Col>
			            <Col span="24">
			                <FormItem  {...formItemLayout} label="减免备注:">
			                    <Input  placeholder="请输入减免备注" {...getFieldProps('derateRemark', { rules: [{ required: true, message: '必填' }] }) } type="text" />
			                </FormItem>
			            </Col>
			          </Row> : <Row>
			           <Col span="24">
			                <FormItem  {...formItemLayout} label="减免天数:">
			                    <Input type="number" placeholder="请输入减免天数，必须比逾期天数小" {...getFieldProps('derateDay', { rules: [{ required: true, message: '必填' }] }) } />
			                </FormItem>
			            </Col>
			          </Row> }
			        </div>*/}
		            <Col span="24">
		              <FormItem {...formItemLayout} label="还款时间:">
		                <DatePicker format="yyyy-MM-dd HH:mm:ss" disabledDate={this.disabledStartDate} value={this.state.startValue} {...getFieldProps('repayTimes', { rules: [{ required: true, message: '必填', type: 'date' }], onChange: (value) => { this.setState({ startValue: value }) } }) } />
		              </FormItem>
		            </Col>
		            <Col span="24">
		              <FormItem {...formItemLayout} label="手机号:">
		                <Select placeholder="请选择管理员手机号" style={{ width: 180 }} {...getFieldProps('mobile', { rules: [{ required: true, message: '必填', type: 'string' }] }) } >
		                  <Option value= {'13925164990'} >13925164990</Option>
		                  <Option value= {'18903340418'} >18903340418</Option>
		                  <Option value= {'18617035085'} >18617035085</Option>
		                </Select>
		                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		                <button key="button" className="ant-btn ant-btn-primary" disabled={flag} onClick={this.handleGetCode}>{text}</button>
		              </FormItem>
		            </Col>
		            <Col span="24">
		              <FormItem {...formItemLayout} label="验证码:">
		                <Input type="text"  style={{ width: '180' }} placeholder="请输入验证码" {...getFieldProps('vcode', { rules: [{ required: true, message: "输入过多或者未输", max: 4 }] }) } />
		              </FormItem>
		            </Col>
		          </Row> : null }
		        </div>
		        <Row>
		          <Col span="24">
		          <FormItem  {...formItemLayout} label="备注:">
		              <Input  placeholder="请输入备注信息" {...getFieldProps('refuseReason', { rules: [{ required: true, message: '必填' }] }) } type="text" />
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