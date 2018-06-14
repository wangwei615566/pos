import React from 'react';
import {
  Modal,
  Form,
  Input,
  Row,
  Col,
} from 'antd';
const createForm = Form.create;
const FormItem = Form.Item;
const objectAssign = require('object-assign');
const userbaseTit = {
  color: '#2db7f5',
  textAlign: 'center',
  fontSize: '14px',
  marginBottom: '10px',
  display: 'block',
  width: '150px',
}
var Tab6 = React.createClass({
  getInitialState() {
    return {
    };
  },
  componentWillReceiveProps(nextProps) {
  },
  componentDidMount() {
  },
  render() {
    var props = this.props;
    var state = this.state;
    var htmlArray = [];
    var dataProd = props.record.applyInfoDO.dataProd;
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
    const formItemLayout2 = {
      labelCol: {
        span: 5
      },
      wrapperCol: {
        span: 19
      },
    };
    return (
        <div className="navLine-wrap-left">
        <h2>决策原因</h2>
	        <Col span="8">
				<div style={{margin:"20px"}}>
		          	<span style={{background:"#fafafa",display:"inlie-block",margin:"20px",padding:"10px 0"}}>失败原因：</span>
		            <span style={{background:"#fff",display:"inlie-block",margin:"20px",padding:"10px 0"}}>{props.record.applyInfoDO.dataProd.reason}</span>
	            <div style={{clear:"both"}}></div>
	            </div>
		    </Col>
	    </div>
    );
  }
});
Tab6 = createForm()(Tab6);
export default Tab6;