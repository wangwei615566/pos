import React from 'react';
import {
  Modal,
  Form,
  Input,
  Row,
  Col,
  Tabs,
} from 'antd';
import Lookdetails from './Lookdetails';
import Tab2 from './Tab2';
const createForm = Form.create;
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const objectAssign = require('object-assign');
var ReviewWin = React.createClass({
  getInitialState() {
    return {
      activekey: "1",
    };
  },
  componentWillReceiveProps(nextProps) {
	    this.setState({
	      record: nextProps.record,
	      dataRecord: nextProps.dataRecord
	    },function(){
//	    	console.log(this.state.dataRecord);
//	    	console.log(this.refs);
	    	if (this.refs.Tab2) {
	    		this.refs.Tab2.setFieldsValue(nextProps.dataRecord);
			}
	    });	    
	  },
  handleCancel() {
    this.refs.Lookdetails.resetFields();
    this.props.hideModal();
    this.changeTabState()
  },

  changeTabState() {
      this.setState({
          activekey: 1,
      })
  },
  handleTabClick(key) {
      this.setState({
          activekey: key
      })
  },

  render() {
    var props = this.props;
    var modalBtns  = [
            <button key="back" className="ant-btn" onClick={this.handleCancel}>关 闭</button>
            ];
    return (
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="1000" footer={modalBtns} maskClosable={false} >
                <Tabs defaultActiveKey="1" onTabClick={this.handleTabClick} activeKey={this.state.activekey}>
                    <TabPane tab="借款信息" key="1">
                        <Lookdetails record={props.record} ref="Lookdetails" canEdit={props.canEdit} visible={props.visible} dataRecord={props.dataRecord} />
                    </TabPane>
                    <TabPane tab="支付凭证" key="2">
                        <Tab2 record={props.record} ref="Tab2" canEdit={props.canEdit} visible={props.visible} dataRecord={this.state.dataRecord}/>
                    </TabPane>                    
                </Tabs>
            </Modal>
    );
  }
});
export default ReviewWin;