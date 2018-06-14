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
      startValue: null,
        count: 60,
        liked: true,
        flag: false
    };
  },
  handleCancel() {
    this.state.value = 1;
    this.state.startValue = null;
    this.state.count=0;
    this.state.liked = true;
    this.state.flag=false;
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
    var re = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    var re1 = /^(\d|[1-9]\d+)(\.\d+)?$/;
    var re2 = /^\d+$/;
    this.props.form.validateFields((errors, values) => {
      if (!!errors) {
        //console.log('Errors in form!!!');
        return;
      }
      if (values.amount > this.props.record.repayAmount) {
        Modal.error({
          title: "还款金额不能大于应还金额",
        });
        return;
      }
      if (values.amount && !re1.test(values.amount)) {
        Modal.error({
          title: "还款金额必须大于等于零",
        });
        return;
      }
      if (values.deratereAmount && !re1.test(values.deratereAmount)) {
        Modal.error({
          title: "减免金额必须大于零",
        });
        return;
      }
      if (values.deratereAmount<=0) {
          Modal.error({
            title: "减免金额必须不等于零",
          });
          return;
        }
      if (values.deratereAmount > this.props.record.repayAmount) {
        Modal.error({
          title: "减免金额不能大于应还金额",
        });
        return;
      }
      if (values.derateDay && !re2.test(values.derateDay)) {
          Modal.error({
            title: "减免天数必须大于等于零",
          });
          return;
        }
      if (parseInt(values.derateDay) > parseInt(this.props.record.penaltyDay)) {
          Modal.error({
            title: "减免天数不能大于逾期天数",
          });
          return;
        }
      if (re.test(values.repayAccount) && values.repayWay != '30') {
        Modal.error({
          title: "账号与方式不符,请核对",
        });
        return;
      }
      if (values.derateDay && !re2.test()) {
		
	}
      var tips = "您是否确定提交";
      confirm({
        title: tips,
        onOk: function () {
          Utils.ajaxData({
            url: "/modules/manage/borrow/repay/confirmRepay.htm",
            data: {
              id: values.id,
              repayTime: DateFormat.formatDate(values.repayTimes),
              repayAccount: values.repayAccount,
              repayWay: values.repayWay,
              serialNumber: values.serialNumber,
              amount: me.props.record.repayAmount,
              penaltyAmout: me.props.record.penaltyAmout ,
              deratereAmount:values.deratereAmount,
              derateRemark:values.derateRemark,
              derateDay:values.derateDay,
              state: me.state.value == 1 ? 10 : 20,
              vcode:values.vcode,
              phone:values.mobile
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
  onChange1(e) {
    this.setState({
      value: e.target.value,
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

      return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="500" footer={modalBtns} maskClosable={false} >
        <Form horizontal form={this.props.form}>
          <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden" />
          <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="还款选择:">
                <RadioGroup onChange={this.onChange1} value={this.state.value}>
                  <Radio key="a" value={1}>对公还款</Radio>
                  <Radio key="b" value={2}>对公减免还款</Radio>
                </RadioGroup>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="还款账号:">
                <Input type="text" placeholder="请输入银行卡号或者支付宝账号" {...getFieldProps('repayAccount', { rules: [{ required: true, message: "输入过多或者未输", max: 20 }] }) } />
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="还款方式:">
                <Select placeholder="请选择" {...getFieldProps('repayWay', { rules: [{ required: true, message: '必填', type: 'string' }] }) } >
                  <Option value="20">银行卡转账</Option>
                  <Option value="30">支付宝转账</Option>
                </Select>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="流水号:">
                <Input type="text" placeholder="请输入流水号" {...getFieldProps('serialNumber', { rules: [{ required: true, message: "输入过多或者未输", max: 50 }] }) } />
              </FormItem>
            </Col>
          </Row>
          
          <Row>
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
	      </Row>
	        <div>
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
	        </div>
	        <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="还款时间:">
                <DatePicker format="yyyy-MM-dd HH:mm:ss" disabledDate={this.disabledStartDate} value={this.state.startValue} {...getFieldProps('repayTimes', { rules: [{ required: true, message: '必填', type: 'date' }], onChange: (value) => { this.setState({ startValue: value }) } }) } />
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="手机号:">
                <Select placeholder="请选择管理员手机号" style={{ width: 180 }} {...getFieldProps('mobile', { rules: [{ required: true, message: '必填', type: 'string' }] }) } >
                  <Option value= {'13277049222'} >13277049222</Option>
                  <Option value= {'18617035085'} >18617035085</Option>
                  <Option value= {'17671775610'} >17671775610</Option>
                </Select>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <button key="button" className="ant-btn ant-btn-primary" disabled={flag} onClick={this.handleGetCode}>{text}</button>
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span="24">
              <FormItem {...formItemLayout} label="验证码:">
                <Input type="text"  style={{ width: '180' }} placeholder="请输入验证码" {...getFieldProps('vcode', { rules: [{ required: true, message: "输入过多或者未输", max: 4 }] }) } />
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