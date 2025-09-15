import React, { useState, useEffect } from "react";
import { TpexSelect } from "../../common/components/select";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";
import TpexSimpleButton from '../../common/components/button';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { getRequest } from '../../services/axios-client';
import { useSelector } from "react-redux/es/hooks/useSelector";
import { TpexLoader } from "../../common/components/loader/loader";



const server = process.env.REACT_APP_API_BASE_URL2;
const Reportform = (
    {
        reportType,
        customFlag,
    }) => {

    const companyCode = useSelector(state => state.app.currentCompanyCode);
    const [vanningPlantDD, setVanningPlantDD] = useState([]);
    const [isLoading, setIsLoading] = useState(false)
    const [containerDestinationDD, setContainerDestinationDD] = useState([]);
    const [importerCodeDD, setImporterCodeDD] = useState([]);
    const [packagingPlantDD, setPackagingPlantDD] = useState([]);
    const [ssLineGroupBoxDD, setssLineGroupBoxDD] = useState([]);
    const [vanningDate, setVanningDate] = useState('');
    
    const [containerDestination, setContainerDestination] = useState(
        containerDestinationDD.length > 0 ? containerDestinationDD[0] : ''
    );
    const [packgingDate, setPackgingDate] = useState('');
    const [ssLineGroupBox, setSsLineGroupBox] = useState(
        ssLineGroupBoxDD.length > 0 ? ssLineGroupBoxDD[0] : ''
    );

    const [payloadData, setPayloadData] = useState({
        packgingLine:'',
        vanningLine:'',
        lotMode:'',
        case:'',
        vanningPlant:'',
        packgingPlant:'',
        conteDes:'',
        importerCode:'',
        partNo:'',
        boxNo:'',
        ssLine:'',
        barCode:'',
        containerRanbanNo:''
    })

    const handleInputChangePVIR = (name, value) => {
        setPayloadData({
            ...payloadData,
            [name]: value,
        });
    };


    const getVanningPlant = () => {
        setIsLoading(true)
        getRequest(server, `invoice/vanningPlant?companyCode=${companyCode}&planningFlag=v`).then((res) => {
            const vanningPlant = res.data.vanningPlantCodeName.map(({ id, name }) => ({
                id: id,
                name: `${id} - ${name}`
            }));
            setVanningPlantDD(vanningPlant)
        }).catch((error) => {
            console.log("error", error)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    const getPackingPlant = () => {
        setIsLoading(true)
        getRequest(server, `invoice/vanningPlant?companyCode=${companyCode}&planningFlag=p`).then((res) => {
            const packingPlant = res.data.vanningPlantCodeName.map(({ id, name }) => ({
                id: id,
                name: `${id} - ${name}`
            }));
            setPackagingPlantDD(packingPlant)
        }).catch((error) => {
            console.log("error", error)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    const getDestinationCode = () => {
        setIsLoading(true)
        getRequest(server, `invoice/destination?companyCode=${companyCode}`).then((res) => {
            const destination = res.data.containerDestination.map(({ id, name }) => ({
                id: id,
                name: `${id} - ${name}`
            }));
            setContainerDestinationDD(destination)
            setImporterCodeDD(destination)
        }).catch((error) => {
            console.log("error", error)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    const sslGruop = () => {
        setIsLoading(true)
        getRequest(server, `invoice/ssLineGroupBox`).then((res) => {
            const ssLineGroupBox = res.data.sslinegroupCode.map((item, i) => ({
                name: item.groupCode
            }));
            setssLineGroupBoxDD(ssLineGroupBox)
        }).catch((error) => {
            console.log("error", error)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    useEffect(() => {
        if (companyCode) {
            sslGruop()
            getPackingPlant()
            getVanningPlant()
            getDestinationCode()

        }
    }, [companyCode])

    useEffect(() => {
    }, [customFlag])



    
    useEffect(() => {
        if(reportType){
           setPayloadData({
            packgingLine:'',
            vanningLine:'',
            boxNo:'',
            lotMode:'',
            case:'',
            barCode:'',
            containerRanbanNo:''
           })
           setPackgingDate('');
           setVanningDate('');
           setSsLineGroupBox('');
        }
    }, [reportType])



    return (
        <form>
            {
                isLoading && <TpexLoader />
            }
            <div className="row pt-2">
                <div className="col">
                    <div className="customDatePicker">
                        <label htmlFor="vanningDate">Vanning Date</label>
                        <TpexDatePicker
                            id="vanningDate"
                            dateSelected={vanningDate}
                            value={vanningDate}
                            handleDateSelected={(date)=> setVanningDate(date)}
                            isDisabled={
                                (reportType === "RDLY019") ||
                                !(reportType === "RDLY021" ||
                                    reportType === "RDLY020")

                            }
                        />
                    </div>

                    <div className="pt-2">
                        <label htmlFor="vanningPlant">Vanning Plant</label>
                        <TpexSelect
                            selected=""
                            id="vanningPlant"
                            hasValue={payloadData.vanningPlant}
                            onChangeSelection={(e) => handleInputChangePVIR('vanningPlant', e.target.value)}
                            options={vanningPlantDD}
                            isDisabled={
                                (reportType === "RDLY019") ||
                                !(reportType === "RDLY021" ||
                                    reportType === "RDLY020")
                            }
                        />
                    </div>

                    <div className="pt-2">
                        <label htmlFor="etd">Vanning Line</label>
                        <input
                            type="text"
                            className="form-control"
                            id="vanningLine"
                            name="vanningLine"
                            minLength="0"
                            maxLength="2"
                            value={payloadData.vanningLine}
                            onChange={(e)=> handleInputChangePVIR("vanningLine", e.target.value)}
                            disabled={
                                (reportType === "RDLY019") ||
                                !(reportType === "RDLY021" ||
                                    reportType === "RDLY020")

                            }
                        />

                    </div>

                    <div className="pt-2">
                        <label htmlFor="ContainerRanbanNo">Container Ranban No.</label>
                        <input
                            type="text"
                            className="form-control"
                            id="containerRanbanNo"
                            name="containerRanbanNo"
                            value={payloadData.containerRanbanNo}
                            minLength="0"
                            maxLength="4"
                            onChange={(e)=>{handleInputChangePVIR("containerRanbanNo", e.target.value)}}
                            disabled={
                                (reportType === "RDLY019") ||
                                !(reportType === "RDLY021" ||
                                    reportType === "RDLY020")
                            }
                        />
                    </div>

                    <div className="pt-2">
                        <label htmlFor="containerDestDD">Container Dest.</label>
                        <TpexSelect
                            id="containerDestination"
                            hasValue={containerDestination}
                            selected={containerDestination}
                            onChangeSelection={evt => setContainerDestination(evt.target.value)}
                            options={containerDestinationDD}
                            isDisabled={

                                (reportType === "RDLY019" || reportType === "RDLY021"

                                    || reportType === "RDLY020" || reportType === "RDLY023" || reportType == "RDLY024" || reportType === "RDLY022" || reportType === "RDLY025" || reportType === "RDLY026"

                                    || reportType === "RDLY027" || reportType === "RDLY028"

                                    || reportType === "RDLY011"

                                    || reportType === "RDLY012"

                                    || reportType === "RDLY029"
                                    || reportType === "RDLY030"

                                )
                            }
                        />
                    </div>
                </div>
                <div className="col">
                    <div className="customDatePicker">
                        <label htmlFor="packgingDate">Packging Date</label>
                        <TpexDatePicker
                            dateFormat="dd/MM/yyyy"
                            dateSelected={packgingDate}
                            id="packgingDate"
                            value={packgingDate}
                            handleDateSelected={e => setPackgingDate(e)}
                            isDisabled={

                                !(reportType === "RDLY019" ||
                                    reportType === "RDLY023" ||
                                    reportType === "RDLY024" ||
                                    reportType === "RDLY022" ||
                                    reportType === "RDLY025" ||
                                    reportType === "RDLY026" ||
                                    reportType === "RDLY027" ||
                                    reportType === "RDLY028" ||
                                    reportType === "RDLY011" ||
                                    reportType === "RDLY012" ||
                                    reportType === "RDLY029") ||
                                (reportType === "RDLY020")}
                        />
                    </div>


                    <div className="pt-2">
                        <label htmlFor="etd">Packging Plant</label>
                        <TpexSelect
                            selected=""
                            id="packgingPlant"
                            hasValue={payloadData.packgingPlant}
                            onChangeSelection={(e) => handleInputChangePVIR('packgingPlant', e.target.value)}
                            options={packagingPlantDD}
                            isDisabled={

                                !(reportType == "RDLY019" || reportType === "RDLY023" ||

                                    reportType === "RDLY024" ||

                                    reportType === "RDLY022"

                                    || reportType === "RDLY025"

                                    || reportType === "RDLY026" || reportType === "RDLY027"

                                    || reportType === "RDLY028"

                                    || reportType === "RDLY011"

                                    || reportType === "RDLY012"

                                    || reportType === "RDLY029")

                                || (reportType == "RDLY020")

                            }
                        />
                    </div>


                    <div className="pt-2">
                        <label htmlFor="packgingLine">Packging Line</label>
                        <input
                            type="text"
                            className="form-control"
                            id="packgingLine"
                            name="packgingLine"
                            value={payloadData.packgingLine}
                            minLength="0"
                            maxLength="2"
                            onChange={(e) => handleInputChangePVIR('packgingLine', e.target.value)}
                            disabled={

                                !(reportType == "RDLY019" ||
                                    reportType == "RDLY024" ||
                                    reportType === "RDLY022"
                                    || reportType === "RDLY025"

                                    || reportType === "RDLY026" || reportType === "RDLY027"

                                    || reportType === "RDLY028"

                                    || reportType === "RDLY011"

                                    || reportType === "RDLY012"

                                    || reportType === "RDLY029")

                                || (reportType == "RDLY020")
                            }
                        />
                    </div>
                    <div className="pt-2">
                        <div className="row lot-num">
                            <div className="col">
                                <label htmlFor="lotNoModNo">Lot No./Mod No</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="lotMode"
                                    name="lotMode"
                                    value={payloadData.lotMode}
                                    minLength="0"
                                    maxLength="6"
                                    onChange={(e) => handleInputChangePVIR('lotMode', e.target.value)}
                                    disabled={!!(reportType === "RDLY021"
                                        || reportType === "RDLY023"
                                        || reportType === "RDLY030"
                                    )
                                        || (reportType == "RDLY020" || reportType == "RDLY019")
                                    }
                                />
                            </div>
                            <div className="col">
                                <label htmlFor="case">Case</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="case"
                                    name="case"
                                    value={payloadData.case}
                                    minLength="0"
                                    maxLength="6"
                                    onChange={(e) => handleInputChangePVIR('case', e.target.value)}
                                    disabled={(reportType == "RDLY019"
                                        || reportType == "RDLY020"
                                        || reportType == "RDLY021"
                                        || reportType == "RDLY023"
                                        || reportType === "RDLY030"
                                    )

                                    }
                                />
                            </div>
                        </div>
                    </div>
                    <div className="pt-2">
                        <label htmlFor="importerCode">Importer Code</label>
                        <TpexSelect
                            selected=""
                            id="importerCode"
                            hasValue={payloadData.importerCode}
                            onChangeSelection={(e) => handleInputChangePVIR('importerCode', e.target.value)}
                            options={importerCodeDD}
                            isDisabled={(reportType == "RDLY030")}
                        />
                    </div>

                </div>
                <div className="col">
                    <div className="pt-0">
                        <label htmlFor="partNo">Part No.</label>
                        <input
                            type="text"
                            className="form-control"
                            id="partNo"
                            name="partNo"
                            value={payloadData.partNo}
                            minLength="0"
                            maxLength="12"
                            onChange={evt => console.log(evt.target.value)}
                            disabled={(reportType === "RDLY027" || reportType === "RDLY028"
                                || reportType === "RDLY011"
                                || reportType === "RDLY012"
                                || reportType === "RDLY029"
                                || reportType === "RDLY024"
                                || reportType === "RDLY022"
                                || reportType === "RDLY025"
                                || reportType === "RDLY026"
                                || reportType === "RDLY019"
                                || reportType === "RDLY020"
                                || reportType === "RDLY021"
                                || reportType === "RDLY023"
                                || reportType === "RDLY030"
                            )

                            }
                        />
                    </div>
                    <div className="pt-2">
                        <label htmlFor="boxNo">Box No.</label>
                        <input
                            type="text"
                            className="form-control"
                            id="boxNo"
                            name="boxNo"
                            value={payloadData.boxNo}
                            minLength="0"
                            maxLength="3"
                            onChange={(e)=>{handleInputChangePVIR("boxNo", e.target.value)}}
                            disabled={
                                (
                                    reportType === "RDLY024"
                                    || reportType === "RDLY022"
                                    || reportType === "RDLY025"
                                    || reportType === "RDLY026"
                                    || reportType === "RDLY019"
                                    || reportType === "RDLY020"
                                    || reportType === "RDLY021"
                                    || reportType === "RDLY023"
                                    || reportType === "RDLY030")}
                        />
                    </div>
                </div>
                <div className="col">
                    <div className="pt-0">
                        <label htmlFor="ContainerRanbanNo">SS Line Group Box</label>
                        <TpexSelect
                            selected=""
                            id="ssLineGroupBox"
                            hasValue={ssLineGroupBox}
                            onChangeSelection={evt => setSsLineGroupBox(evt.target.value)}
                            options={ssLineGroupBoxDD}
                            isDisabled={
                                (reportType === "RDLY027" ||
                                    reportType === "RDLY028"
                                    || reportType === "RDLY024"
                                    || reportType === "RDLY022"
                                    || reportType === "RDLY019"
                                    || reportType === "RDLY020"
                                    || reportType === "RDLY021"
                                    || reportType === "RDLY023"
                                    || reportType === "RDLY026"
                                    || reportType === "RDLY030"
                                    || reportType === "RDLY025")}

                        />
                    </div>
                </div>
                <div className="col">
                    <div className="pt-0">
                        <label htmlFor="barCodeValue">BarCode Value</label>
                        <input
                            type="text"
                            className="form-control"
                            id="barCode"
                            name="barCode"
                            value={payloadData.barCode}
                            minLength="0"
                            maxLength="15"
                            onChange={(e)=> {handleInputChangePVIR("barCode", e.target.value)}}
                            disabled={customFlag !== "Y" || customFlag.flag === "undefined"}
                        />
                    </div>
                </div>
            </div>
            <div className="row">
                <div className="col pt-2">
                    <div className="d-flex justify-content-end">
                        <TpexSimpleButton
                            color="primary"
                            text={LABEL_CONST.PRINT}
                            handleClick={() => console.log('Print button Clicked')}
                        />
                    </div>
                </div>
            </div>
        </form>
    )

}

export default Reportform