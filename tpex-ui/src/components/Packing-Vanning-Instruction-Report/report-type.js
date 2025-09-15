import React, { useEffect, useState } from "react";
import { TpexSelect } from "../../common/components/select";
import { getRequest } from "../../services/axios-client";

const server = process.env.REACT_APP_API_BASE_URL2;
const ReportTypeList = (props) => {
    const [customLabel, setCustomLabel] = useState([])
    const [customLabelValue, setCustomLabelValue] = useState('')
    const [customData, setCustomData]= useState([])
    

    const getCustomLabel = () => {
        getRequest(server, 'invoice/customLabel?companyCode=TMT').then((res) => {
            const customLable = res.data.customLabelDescription.map(({ id, name}) => ({
                id: id,
                name: `${id} - ${name}`,
            }))
            setCustomLabel(customLable)
            setCustomData(res.data.customLabelDescription)
        }).catch((error) => {
            console.log("error", error)
        })
    }
    useEffect(() => {
        getCustomLabel()
    }, [])

    const getCustomValue = (e) => {
        setCustomLabelValue(e.target.value)
        const result = customData.filter(item =>item.id === e.target.value)
        let [flag] = result
        props.setCustomFlag(flag?.barcodeInputRequired)
    }
    useEffect(()=>{
        if(props.reportType !== "RDLY030"){
            setCustomLabelValue('')
            props.setCustomFlag('')
        }
    }, [props.reportType])
    return (
        <form>
            <div className='row'>
                <div className='col pt-2'>
                    <div className='row'>
                        <h5>Packing and Vanning Ins.</h5>
                        <div className='form-group radio-main-container radio-container'>

                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY019"
                                    id="RDLY019"
                                    value="RDLY019"
                                    checked={props.reportType === "RDLY019"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY019">Packing Sequence List </label>
                            </div>

                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY020"
                                    id="RDLY020"
                                    value="RDLY020"
                                    checked={props.reportType === "RDLY020"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY020">Vanning Schedule List </label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY021"
                                    id="RDLY021"
                                    value="RDLY021"
                                    checked={props.reportType === "RDLY021"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY021">Container Stuffing List</label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY023"
                                    id="RDLY023"
                                    value="RDLY023"
                                    checked={props.reportType === "RDLY023"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY023">Module Transport Order</label>
                            </div>
                        </div>
                    </div>

                </div>

                <div className='col pt-2'>
                    <div className='row'>
                        <h5>Module Label</h5>
                        <div className='form-group radio-main-container radio-container'>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY024"
                                    id="RDLY024"
                                    value="RDLY024"
                                    checked={props.reportType === "RDLY024"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY024">Shipping Mark</label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY022"
                                    id="RDLY022"
                                    value="RDLY022"
                                    checked={props.reportType === "RDLY022"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY022">Content List</label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY025"
                                    id="RDLY025"
                                    value="RDLY025"
                                    checked={props.reportType === "RDLY025"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY025">Shipping Mark with Content List</label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY026"
                                    id="RDLY026"
                                    value="RDLY026"
                                    checked={props.reportType === "RDLY026"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY026">Shipping Mark /Content List with Part Label</label>
                            </div>
                        </div>
                    </div>

                </div>

                <div className='col pt-2'>
                    <div className='row'>
                        <h5>Part Label</h5>
                        <div className='form-group radio-main-container radio-container'>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="PartLabel"
                                    id="RDLY027"
                                    value="RDLY027"
                                    checked={props.reportType === "RDLY027"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY027">Part Label</label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="RDLY028"
                                    id="RDLY028"
                                    value="RDLY028"
                                    checked={props.reportType === "RDLY028"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY028">Part Label with header</label>
                            </div>
                        </div>
                    </div>


                </div>

                <div className='col pt-2'>
                    <div className='row'>
                        <h5>Sub Line</h5>
                        <div className='form-group radio-main-container radio-container'>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="SSLineGroup"
                                    id="RDLY011"
                                    value="RDLY011"
                                    checked={props.reportType === "RDLY011"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY011">SS Line Group Box</label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="SSLinePart"
                                    id="RDLY012"
                                    value="RDLY012"
                                    checked={props.reportType === "RDLY012"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY012">SS Line Part Label</label>
                            </div>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="SSLineContentList"
                                    id="RDLY029"
                                    value="RDLY029"
                                    checked={props.reportType === "RDLY029"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY029">SS Line Content List</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div className='col pt-2'>
                    <div className='row'>
                        <h5>Custom Label</h5>
                        <div className='form-group radio-main-container radio-container'>
                            <div className="form-check">
                                <input
                                    className="form-check-input"
                                    type="radio"
                                    name="customLabel"
                                    id="RDLY030"
                                    value="RDLY030"
                                    checked={props.reportType === "RDLY030"}
                                    onChange={(e) => {
                                        props.reportChange(e)
                                    }}
                                />
                                <label className="form-check-label ordertype-label" htmlFor="RDLY030">Custom Label</label>
                            </div>
                            <div className="form-check p-0">
                                <TpexSelect
                                    id="customLabelDD"
                                    hasValue={customLabelValue}
                                    onChangeSelection={getCustomValue}
                                    options={customLabel}

                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>


        </form>
    )
}

export default ReportTypeList