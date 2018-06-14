import React from 'react'
import {Table, Modal, Icon} from 'antd';
import Lookdetails from "./Lookdetails";
const objectAssign = require('object-assign');
import AddWin from "./AddWin";
export default React.createClass({
    getInitialState() {
        return {
            selectedRowKeys: [], // 这里配置默认勾选列
            loading: false,
            data: [],
            pagination: {
                pageSize: 10,
                current: 1
            },
            canEdit: true,
            visible: false,
            visible1: false,
            visible2: false,
            pictureData: [],
            creditReportData: [],
            rowRecord:[],
            record:"",
            visibleAdd:false,

        };
    },
    componentWillReceiveProps(nextProps, nextState) {
        this.fetch(nextProps.params);
    },
    componentDidMount(){
        this.fetch();
    },
    fetch(params = {}) {
        this.setState({
            loading: true
        });
        var year, month, day;
        var year1, month1, day1;
        var date = new Date();
        if(date.getDate() < 10 ){
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1;
            day1 = date.getDate();
            switch(month1){
                case 1:
                    year = year1-1;
                    month = 12;
                    day = day1+22;
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                    year = year1;
                    month = month1<10 ? '0'+(month1-1) : (month1-1);
                    day = day1+22;
                    break;
                case 3:
                    year = year1;
                    month = '0'+(month1-1);
                    day = year&4==0&&year%100!=0||year%400==0 ? day1+20 : day1+19;
                    break;
                case 5:
                case 7:
                case 10:
                case 12:
                    year = year1;
                    month = month1<=10 ? '0'+(month1-1) : (month1-1);
                    day = day1+21;
                    break;
            }
            month1 = month1<10?'0'+month1 : month1;

        }else{
            year1 =  date.getFullYear();
            month1 = date.getMonth()+1<10 ? '0'+(date.getMonth()+1) : date.getMonth()+1;
            day1 = date.getDate();
            year = year1;
            month = month1;
            day = day1 < 20 ? '0'+(day1-9) : day1-9;
        }
        if (!params.pageSize) {
            var params = {};
            params = {
                search: JSON.stringify({startDate:year+'-'+month+'-'+day,endDate:year1+'-'+month1+'-'+day1}),
                pageSize: 10,
                current: 1
            }
        }
        Utils.ajaxData({
            url: '/modules/manage/rc/data/statistics/list.htm',
            method: "post",
            data: params,
            callback: (result) => {
                const pagination = this.state.pagination;
                pagination.current = params.current;
                pagination.pageSize = params.pageSize;
                pagination.total = result.page.total;
                if (!pagination.current) {
                    pagination.current = 1
                }
                ;
                this.setState({
                    loading: false,
                    data: result.data,
                    pagination
                });
            }
        });
    },

    //查看弹窗
    showModal(title,record, canEdit) {
      
        this.setState({
            visible: true,
            canEdit: canEdit,
            record: record,
            title: title
        },()=>{ 
            
            this.refs.Lookdetails.setFieldsValue(record);
        })
    },
  //新增
  addModal(title, record, canEdit){
      this.setState({
        visibleAdd:true,
        title:title,  
      })

  },
    //隐藏弹窗
    hideModal() {
        this.setState({
            visible: false,
            visible1: false,
            visible2: false,
            selectedIndex: '',
            selectedRowKeys: [],
            visibleAdd:false
        });
        this.refreshList();
    },
    rowKey(record) {
        return record.date;
    },

    //选择
    onSelectChange(selectedRowKeys) {
        this.setState({
            selectedRowKeys
        });
    },

    //分页
    handleTableChange(pagination, filters, sorter) {
        const pager = this.state.pagination;
        pager.current = pagination.current;
        this.setState({
            pagination: pager,
        });
        this.refreshList();
    },

    refreshList() {
        var pagination = this.state.pagination;
        var params = objectAssign({}, this.props.params, {
            current: pagination.current,
            pageSize: pagination.pageSize,
        });
        this.fetch(params);
    },

    //行点击事件
    onRowClick(record) {
        //console.log(record)
        this.setState({
            selectedRowKeys: [record.id],
            selectedRow: record,
            rowRecord:record
        },()=>{
            this
        });
    },

    // 列表添加选中行样式
    rowClassName(record) {
        let selected = this.state.selectedIndex;
        //console.log('selected', this.state.selectedIndex)
        return (record.id == selected && selected !== '') ? 'selectRow' : '';

    },

    //选择
    onSelectAll(selected, selectedRowKeys, selectedRows, changeRows) {
        if (selected) {
            this.setState({
                selectedRowKeys
            })
        } else {
            this.setState({
                selectedRowKeys: []
            })
        }
    },

    render() {
        const {
            loading,
            selectedRowKeys
            } = this.state;
        const rowSelection = {
            type: 'checkbox',
            selectedRowKeys,
            onSelectAll: this.onSelectAll,
        };
        let me=this;
        const hasSelected = selectedRowKeys.length > 0;
        let openEdit = true;
        if (hasSelected && selectedRowKeys.indexOf("0") === -1) {
            openEdit = false;
        }
        var columns = [{
            title: '日期',
            width:160,
            dataIndex: 'createTime',
            render(text,record){     		
        		return text.substring(0,10)
        	}
         },{
        	title: '渠道',
        	width:110,
        	dataIndex: 'channelId',
        	render(text,record){
        		if(text == 0){
        			return "全部"
        		} else if(text == 1){
        			return "卡牛"
        		} else {
        			return "成长钱包APP"
        		}
        	}
        },{
            title: '当日应还笔数',
            width:110,
            dataIndex: 'dateShouldPayCount'
        },{
        	 title: '当日应还总本金',
        	 width:130,
             dataIndex: 'dateShouldPayMoney'
        }, {
            title: '当日逾期笔数',
            width:110,
            dataIndex: 'dateOverPayCount'
        }, {
            title: '当日逾期总本金',
            width:130,
            dataIndex: 'dateOverPayMoney'
        }, {
            title: '当日已还款笔数',
            width:130,
            dataIndex: 'dateHadPayCount'
        }, {
            title: '当日已还款总本金',
            width:140,
            dataIndex: "dateHadPayMoney"
        },{
            title: '当日提前还款笔数',
            width:140,
            dataIndex: "dateAdvancePayCount"
        }, {
            title: '当日提前还款总金额',
            width:150,
            dataIndex: 'dateAdvancePayMoney'
        }, {
            title: '当日逾期笔数比率',
            width:140,
            dataIndex: "dateOverPayCountRatio",
        }, {
            title: '当日逾期本金比率',
            width:140,
            dataIndex: 'dateOverPayMoneyRatio',
        }, {
            title: '应还总笔数',
            width:100,
            dataIndex: 'shouldPayCount'
        }, {
            title: '应还总本金',
            width:120,
            dataIndex: "shouldPayMoney"
        },{
            title: '已还款总笔数',
            width:110,
            dataIndex: "hadPayCount"
        }, {
            title: '已还款总本金',
            width:120,
            dataIndex: 'hadPayMoney'
        }, {
            title: '提前还款总笔数',
            width:120,
            dataIndex: "advancePayCount",
        }, {
            title: '提前还款总金额',
            width:120,
            dataIndex: 'advancePayMoney',
        }, {
            title: '逾期总笔数',
            width:100,
            dataIndex: 'overPayCount'
        }, {
            title: '逾期总本金',
            width:120,
            dataIndex: "overPayMoney"
        },{
            title: '坏账总笔数',
            width:100,
            dataIndex: "noPayCount"
        }, {
            title: '坏账总本金',
            width:120,
            dataIndex: 'noPayMoney'
        }, {
            title: '逾期笔数比率',
            width:120,
            dataIndex: "overPayCountRatio",
        }, {
            title: '逾期本金比率',
            width:120,
            dataIndex: 'overPayMoneyRatio',
        }, {
            title: 'D7逾期笔数',
            width:100,
            dataIndex: 'dsevenOverPayCount'
        }, {
            title: 'D7逾期比率',
            width:120,
            dataIndex: "dsevenOverPayRatio"
        },{
            title: 'D15逾期笔数',
            width:100,
            dataIndex: "dfifteenOverPayCount"
        }, {
            title: 'D15逾期比率',
            width:120,
            dataIndex: 'dfifteenOverPayRatio'
        }, {
            title: 'M1逾期笔数',
            width:100,
            dataIndex: "moneOverPayCount",
        }, {
            title: 'M1逾期笔数比率',
            width:130,
            dataIndex: 'moneOverPayCountRatio',
        }, {
            title: 'M2逾期笔数',
            width:100,
            dataIndex: 'mtwoOverPayCount'
        }, {
            title: 'M2逾期笔数比率',
            width:130,
            dataIndex: "mtwoOverPayCountRatio"
        },{
            title: 'M3逾期笔数',
            width:100,
            dataIndex: "mthreeOverPayCount"
        }, {
            title: 'M3逾期笔数比率',
            width:130,
            dataIndex: 'mthreeOverPayCountRatio'
        }]

        var state = this.state;
        var hStyle = {color: '#1C86EE'};
        return (
            <div className="block-panel rcDataStatisticsTable">
                <div className="actionBtns" style={{ marginBottom: 16 }}>
                    {/*<button onClick={me.addModal.bind(me,'新增')} className="ant-btn"> 
                        新增(测试)
                    </button>*/}
                </div>
                <Table columns={columns} rowKey={this.rowKey} ref="table"  
                       onRowClick={this.onRowClick}
                       dataSource={this.state.data}
                       rowClassName={this.rowClassName}
                       pagination={this.state.pagination}
                       onChange={this.handleTableChange}
                />
                <div>
                    <h3 style={hStyle}>共{this.state.pagination.total}条记录</h3>
                </div>
                <Lookdetails ref="Lookdetails" visible={state.visible} title={state.title} hideModal={me.hideModal} record={state.record}
                canEdit={state.canEdit} />
                <AddWin ref="AddWin"  visible={state.visibleAdd} hideModal={me.hideModal} title={state.title}/>
            </div>
        );
    }
})
