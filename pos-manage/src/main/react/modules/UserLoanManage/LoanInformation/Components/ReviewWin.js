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
import Tab3 from './Tab3';
import Tab4 from './Tab4';
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
      <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="1200" footer={modalBtns} maskClosable={false} >
                <Tabs defaultActiveKey="1" onTabClick={this.handleTabClick} activeKey={this.state.activekey}>
                    <TabPane tab="借款信息" key="1">
                        <Lookdetails record={props.record} ref="Lookdetails" canEdit={props.canEdit} visible={props.visible} dataRecord={props.dataRecord} />
                    </TabPane>
                    <TabPane tab="基本信息" key="2">
                        <Tab2 record={props.record} ref="Tab2" canEdit={props.canEdit} visible={props.visible} recordSoure={props.recordSoure} dataForm={props.dataForm}/>
                    </TabPane>
                    <TabPane tab="通讯录" key="3">
                        <Tab3 record={props.record} ref="Tab3" canEdit={props.canEdit} visible={props.visible} />
                    </TabPane>
                    <TabPane tab="风控报告" key="4">
                        <Tab4 record={props.record} ref="Tab4" canEdit={props.canEdit} visible={props.visible} riskRecord={props.riskRecord} />
                    </TabPane>
                </Tabs>
            </Modal>
    );
  }
});
export default ReviewWin;