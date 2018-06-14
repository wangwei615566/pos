import React from 'react';
import {
    Button,
    Form,
    Input,
    Modal,
    Select,
    Tree,
    TreeSelect,
    Row,
    Col
} from 'antd';
const createForm = Form.create;
const FormItem = Form.Item;
const Option = Select.Option;
const objectAssign = require('object-assign');
let treeData = [];

var AddWin = React.createClass({
    getInitialState() {
        return {
            status: {},
            formData: {}
        };
    },
    handleCancel() {
        this.props.form.resetFields();
        this.props.hideModal();
    },
    handleOk(e) {
        e.preventDefault();
        var re1 = /^\+?[1-9]\d*$/;
        var me = this;
        var props = me.props;
        var record = props.record;
        var title = props.title;
        //console.log(record)
        this.props.form.validateFields((errors, values) => {
            if (!!errors) {
                //console.log('Errors in form!!!');
                return;
            }
            if (!values.ranking || !re1.test(values.ranking)||values.ranking<0) {
                Modal.error({
                  title: "排名不能为空且必须是大于等于0的数",
                });
                return;
              }
            if (title == "新增") {
                var data = objectAssign({}, {
                    form: JSON.stringify(objectAssign({}, values, {
                        
                        }))
                    }, {
                        status: 'create'
                    });
                }
                else if (title == "编辑") {
                    var data = objectAssign({}, {
                    form: JSON.stringify(objectAssign({}, values, {
                        
                    }))
                }, {
                    status: 'update'
                });
                }
            Utils.ajaxData({
                url: "/modules/manage/drainage/platform/addAndUpdate.htm",
                method: 'post',
                status:data.status,
                data: {
                	name: values.name,
                    platform: values.platform,
                    ranking: values.ranking,
                    status:data.status,
                    id:values.id
                },
                callback: (result) => {
                	if(result.code==200){
                		Modal.success({
                			title: result.msg,
                		});
                	}else{
                		Modal.error({
                			title: result.msg,
                		});
                	}
                    me.handleCancel();
                }
            })

        })
    },
    render() {
        const {
            getFieldProps
        } = this.props.form;
        var props = this.props;
        var state = this.state;
        var modalBtns = [
            <button key="back" className="ant-btn" onClick={this.handleCancel}>返 回</button>,
            <button key="button" className="ant-btn ant-btn-primary" onClick={this.handleOk}>
                提 交
            </button>
        ];
        const formItemLayout = {
            labelCol: {
                span: 8
            },
            wrapperCol: {
                span: 15
            },
        };
   
        return (
            <Modal title={props.title} visible={props.visible} onCancel={this.handleCancel} width="700" footer={modalBtns} maskClosable={false} >
                <Form horizontal form={this.props.form}>
                    <Input  {...getFieldProps('id', { initialValue: '' }) } type="hidden" />
                    <Row>
                        <Col span="16">
                            <FormItem  {...formItemLayout} label="平台名称：">
                                <Input disabled={!props.canEdit}  {...getFieldProps('name', { rules: [{ required: true, message: '必填' }] }) } type="text" />
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="16">
                            <FormItem  {...formItemLayout} label="平台标志码：">
                                <Input disabled={!props.canEdit}  {...getFieldProps('platform', { rules: [{ required: true, message: '必填' }] }) } type="text"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
		                <Col span="16">
		                    <FormItem  {...formItemLayout} label="排名：">
		                        <Input disabled={!props.canEdit}  {...getFieldProps('ranking')} type="number"/>
		                    </FormItem>
		                </Col>
                    </Row>
                </Form>
            </Modal>
        );
    }
});
AddWin = createForm()(AddWin);
export default AddWin;
