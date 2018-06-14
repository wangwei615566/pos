import React from 'react'
import {
  Table,
  Modal,
  Form,
  Row,
  Col,
  Input,
  Popover,
  Select,
  Icon,
  Button,
} from 'antd';
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
  hideCancel() {
    this.setState({
      visible: false,
      loading: false,
    });
    this.props.hideModal();
  },
  componentWillReceiveProps(nextProps, nextState) {
	    this.setState({
	    	dataRecord: nextProps.dataRecord,
	    	visible:nextProps.visiblesw
	    })
  },
  handleContent: function(e) {
      var value = e.target.value;
      var error = '';
      if(value == null || value == "") {
        error = '请输入敏感词';
      }
      this.setState({
        name: value,
        emailError: error
      });
    },
  handleOk(){
    	var param = {};
    	param = {
    		content:this.state.name,
    	    type:1	
    	}
    	
    		if(this.state.name == ""){
    			Modal.warning({
                    title: "不能输入空值",
                  });
    		} else if(this.state.name == " ") {
    			Modal.warning({
                    title: "不能输入空格",
                  });
    		}  else if(this.state.name.length > 10){
    			Modal.warning({
                    title: "输入字符长度不能超过10",
                  });
    		} else {
    		Utils.ajaxData({
    			url: "/modules/manage/sensitive/save.htm",
    			data:param,
    			method: 'post',
    			callback: (result) => {  
    				let resType = result.code == 200 ? 'success' : 'warning';
    				Modal.success({
    					title: "添加成功",
    				});
    				this.setState({
    					name:"",
    				})
    				this.handleRefresh();
    			}
    		});		
    	}
  },  
  handleAdd(ids){
	  var param = {};
	  	param = {
	  	    id:ids,
	  	    content:this.state.content,
	  	}
		Utils.ajaxData({
	        url: "/modules/manage/sensitive/update.htm",
	        data:param,
	        method: 'post',
	        callback: (result) => {  
	          let resType = result.code == 200 ? 'success' : 'warning';
	          Modal.success({
	              title: "修改成功",
	            });
	        }
	      });
  },
  handleDelete(ids){
	var param = {};
  	param = {
  	    id:ids,
  	}
  	Utils.ajaxData({
        url: "/modules/manage/sensitive/delete.htm",
        data:param,
        method: 'get',
        callback: (result) => {  
          let resType = result.code == 200 ? 'success' : 'warning';
          Modal.success({
              title: "删除成功",
            });
          this.handleRefresh();
        }
      });
  },
  handleRefresh(){ 
	  		var param = {};
	  		  param = {
	  			        type: 1,
	  			      }
	  		     Utils.ajaxData({
	  		        url: '/modules/manage/sensitive/list.htm',
	  		        method: 'get',
	  		        data:param,
	  		        callback: (result) => {
	  		          this.setState({
	  		            dataRecord:result.data, 		            
	  		          });
	  		        }
	  		   });
	  },
  render() {
	  var modalBtnstwo = [ <button key="back" className="ant-btn" onClick={this.hideCancel}>关闭</button>,];
	  var optionItem = [];
	    if(typeof this.state.dataRecord != "undefined"){
	    var dataLength = this.state.dataRecord.length;
	      for(let i = 0; i < dataLength; i++){
	    	 let sensiveId = this.state.dataRecord[i].id;
	        optionItem.push(
	        	 <div className="sensitive-style" >       
	      	     <p className="sensitive-name">{this.state.dataRecord[i].content}</p>
	      	     <p className="sensitive-x"  onClick={(e) => {this.handleDelete(sensiveId)}}>X</p>   
	      	     </div> );       
	      }
	    }
    return (
      <Modal title="敏感词" visible={this.state.visible} onCancel={this.hideCancel}  width="800"  footer={[modalBtnstwo]}>  
      {optionItem} 
      <Row>
      	<div className="sensitive-input">
      	<input type="text" value={this.state.name} onChange={this.handleContent} />
      	</div>
      	<div className="sensitive-input">  
        <a href="#" onClick={this.handleOk}>添加</a>   
        </div>
      	</Row>
     </Modal>
    );
  }
});
export default AdjustCreditDetial;