import React from 'react';
import {Button, Form, Input, Select,Message,DatePicker,Upload, Icon, message, Modal} from 'antd';
import reqwest from 'reqwest';
const createForm = Form.create;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const Option = Select.Option;

let SeachForm = React.createClass({
    getInitialState() {
        return {
            roleList: [],
            initdate:true,
            fileList: [],
            uploading: false,

        }
    },

    handleUpload() {
        var that=this;
        const {fileList} = this.state;
        const formData = new FormData();
        fileList.forEach((file) => {
            formData.append("file", file);
        });

        this.setState({
            uploading: true,
        }),
        reqwest({
            url: '/modules/manage/borrow/submitBatchArbitrate.htm',
            method: 'post',
            processData: false,
            data: formData,
            success: function(result) {
                that.setState({
                    fileList: [],
                    uploading: false,
                });
                if(result.code == 200){
                    Modal.success({
                        title:result.msg,
                    })
                }else{
                    Modal.error({
                        title:result.msg,
                    })
                }

            },
            error :() => {
                that.setState({
                    uploading: false,
                });
                Modal.error({
                    title: "系统忙，请稍后重试"
                });
            },
        });
    },

    handleQuery() {
        var params = this.props.form.getFieldsValue();
        var json = {startDate:'',endDate:'',realName:params.realName,phone:params.phone,orderNo:params.orderNo,state:"50"};
        //console.log(params);
        if(params.time[0]){
            var d = new Date(params.time[0]);
            json.startDate=d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()+' 00:00:00';
            var d1 = new Date(params.time[1]);
            json.endDate=d1.getFullYear() + '-' + (d1.getMonth() + 1) + '-' + d1.getDate()+' 23:59:59';
            //console.log(d);
        }
        this.props.passParams({
            searchParams: JSON.stringify(json),
            pageSize: 10,
            current: 1,
        });
    },
    handleReset() {
        this.props.form.resetFields();
        this.setState({
            initdate:false
        });
        this.props.passParams({
            pageSize: 10,
            current: 1,
            searchParams: JSON.stringify({state:"50"}),
        });
    },
    handleOut() {
        var params = this.props.form.getFieldsValue();
        var json = JSON.stringify(params);
        window.open("/modules/manage/overdue/export.htm?searchParams="+encodeURIComponent(json));

    },
    disabledDate(startValue) {
        var today = new Date();
        return startValue.getTime() > today.getTime();
    },
    render() {

        const {getFieldProps} = this.props.form;
        var year, month, day;
        var year1, month1, day1;
        var date = new Date();
        if(date.getDate() < 7 ){
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1;
            day1 = date.getDate();
            switch(month1){
                case 1:
                    year = year1-1;
                    month = 12;
                    day = day1+25;
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                    year = year1;
                    month = month1<10 ? '0'+(month1-1) : (month1-1);
                    day = day1+25;
                    break;
                case 3:
                    year = year1;
                    month = '0'+(month1-1);
                    day = year%4==0&&year%100!=0||year%400==0 ? day1+23 : day1+22;
                    break;
                case 5:
                case 7:
                case 10:
                case 12:
                    year = year1;
                    month = month1<=10 ? '0'+(month1-1) : (month1-1);
                    day = day1+24;
                    break;
            }

            month1 = month1<10?'0'+month1 : month1;
        }else{
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1<10 ? '0'+(date.getMonth()+1) : date.getMonth()+1;
            day1 = date.getDate();
            year = year1;
            month = month1;
            day = day1 < 16 ? '0'+(day1-6) : day1-6;

        };

        const { uploading } = this.state;
        const props = {
            action: '#',
            onRemove: (file) => {
                this.setState(({ fileList }) => {
                    const index = fileList.indexOf(file);
                    const newFileList = fileList.slice();
                    newFileList.splice(index, 1);

                    return {
                        fileList: newFileList,
                    };
                });
            },
            beforeUpload: (file) => {
                const isTEXT = file.type === 'text/plain';
                if (!isTEXT) {
                    Modal.error({
                        title: "请上传text文本!"
                    })
                }else{
                    this.setState(({ fileList }) => ({
                        fileList: [...fileList, file],
                    }));
                }
                return false;
            },

            fileList: this.state.fileList,
        };

        return (
            <Form inline>
             <Input type="hidden" {...getFieldProps('state',{initialValue: '50'})} />
             <FormItem label="订单生成时间:">
             	  <RangePicker disabledDate={this.disabledDate} {...getFieldProps('time', { initialValue: this.state.initdate ? [year+'-'+month+'-'+day+' 00:00:00',year1+'-'+month1+'-'+day1+' 23:59:59'] : ''}) } />
             </FormItem>
             <FormItem label="真实姓名:">
                  <Input onPressEnter={this.handleQuery} {...getFieldProps('realName')} />
             </FormItem>
             <FormItem label="手机号码:">
                  <Input onPressEnter={this.handleQuery} {...getFieldProps('phone')} />
             </FormItem>
             <FormItem label="订单号:">
                  <Input onPressEnter={this.handleQuery} {...getFieldProps('orderNo')} />
             </FormItem>
             <FormItem><Button type="primary" onClick={this.handleQuery}>查询</Button></FormItem>
             <FormItem><Button type="reset" onClick={this.handleReset}>重置</Button></FormItem>
             <FormItem><Button onClick={this.handleOut}>导出</Button></FormItem>
                <div>
                    <Upload {...props}>
                        <Button
                            type="primary"
                            disabled={this.state.fileList.length === 1}
                            loading={uploading}
                        >
                            <Icon type="upload" /> 提交仲裁订单文件
                        </Button>
                    </Upload>
                    <Button
                        className="upload-demo-start"
                        type="primary"
                        onClick={this.handleUpload}
                        disabled={this.state.fileList.length === 0}
                        loading={uploading}
                    >
                        执行
                    </Button>

                    {
                        this.state.fileList.map((con,ind)=>{
                            return <div key={ind} >{con.name} <button style={{color:"#fbfeff", background:"#57c5f7", border: 0 , padding : "2px"}}
                                onClick={()=>{
                                props.onRemove(con)}}
                                >删除</button></div>
                        })
                    }
                </div>

            </Form>
        );
    }
});

SeachForm = createForm()(SeachForm);
export default SeachForm;