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
var Tab5 = React.createClass({
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
    for(var item in dataProd){
    	var model;
    	if(item == "prodAcctExcep"){
    		htmlArray.push(<div><br/><h2>账户异常</h2></div>);
    		model ="prodAcctExcep";
    	}else if(item == "prodAddrExcep"){
    		htmlArray.push(<div><br/><h2>位置异常</h2></div>);
    		model ="prodAddrExcep";
    	}
    	else if(item == "prodApply"){
    		htmlArray.push(<div><br/><h2>多头借贷</h2></div>);
    		model ="prodApply";
    	}
    	else if(item == "prodBankQuery"){
    		htmlArray.push(<div><br/><h2>查询次数统计</h2></div>);
    		model ="prodBankQuery";
    	}
    	else if(item == "prodBankTrans"){
    		htmlArray.push(<div><br/><h2>银行流水</h2></div>);
    		model ="prodBankTrans";
    	}
    	else if(item == "prodBlack"){
    		htmlArray.push(<div><br/><h2>特殊名单</h2></div>);
    		model ="prodBlack";
    	}
    	else if(item == "prodCreditScore"){
    		htmlArray.push(<div><br/><h2>信用分数</h2></div>);
    		model ="prodCreditScore";
    	}
    	else if(item == "prodEcTrans"){
    		htmlArray.push(<div><br/><h2>电商流水</h2></div>);
    		model ="prodEcTrans";
    	}
    	else if(item == "prodFixedAssets"){
    		htmlArray.push(<div><br/><h2>固定资产</h2></div>);
    		model ="prodFixedAssets";
    	}
    	else if(item == "prodOperator"){
    		htmlArray.push(<div><br/><h2>运营商数据</h2></div>);
    		model ="prodOperator";
    	}
    	else if(item == "prodOverdue"){
    		htmlArray.push(<div><br/><h2>逾期情况</h2></div>);
    		model ="prodOverdue";
    	}
    	else if(item == "prodRepayPerform"){
    		htmlArray.push(<div><br/><h2>信用还款表现</h2></div>);
    		model ="prodRepayPerform";
    	}
    	else if(item == "prodTravel"){
    		htmlArray.push(<div><br/><h2>航旅数据</h2></div>);
    		model ="prodTravel";
    	}
    	else if(item == "prodUnitExcep"){
    		htmlArray.push(<div><br/><h2>设备异常</h2></div>);
    		model ="prodUnitExcep";
    	}
    	else if(item == "prodVerifyInfo"){
    		htmlArray.push(<div><br/><h2>一致性性验证</h2></div>);
    		model ="prodVerifyInfo";
    	}
    	if(model){
    		var items =[];
    		var modelArray = dataProd[model];
    		for(var m in modelArray){
    			var keyitem = m;
    			var keyval = modelArray[m];
    			if(m == "noBus" || m == "id"){
    				continue;
    			}
    			if(model == "prodOperator"){
    				items.push(
    					<Col span="8">
    						<div style={{margin:"20px"}}>
			              	<p style={{background:"#fafafa",margin:"20px",padding:"10px 0"}}>{keyitem}：</p>
			                <p style={{background:"#fff",margin:"20px",padding:"10px 0",wordBreak:"break-all"}}>{keyval}</p>
			                <div style={{clear:"both"}}></div>
			                </div>
			            </Col>)
    			}else{
    				items.push(
    					<Col span="8">
    						<div style={{margin:"20px"}}>
			              	<span style={{background:"#fafafa",display:"inlie-block",margin:"20px",padding:"10px 0"}}>{keyitem}：</span>
			                <span style={{background:"#fff",display:"inlie-block",margin:"20px",padding:"10px 0"}}>{keyval}</span>
			                <div style={{clear:"both"}}></div>
			                </div>
			            </Col>)
    			}
        	};
        	htmlArray.push(<Row>{items}</Row>)
    	}
    }
    	
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
        <h2>申请信息</h2>
        <Row>
          <Col span="8">
              <span>姓名：</span>
              <span>{props.record.applyInfoDO.nameCustc}</span>
          </Col>
          <Col span="8">
        	<span>证件号码：</span>
        	<span>{props.record.applyInfoDO.idCard}</span>
          </Col>
          
        </Row>
        <Row>
        <Col span="8">
	        <span>手机号：</span>
	        <span>{props.record.applyInfoDO.mobile}</span>
	      </Col>
        <Col span="8">
          	<span>现居地址：</span>
            <span>{props.record.applyInfoDO.abodeAdd}</span>
        </Col>
      </Row>
          {htmlArray}
        </div>
    );
  }
});
Tab5 = createForm()(Tab5);
export default Tab5;