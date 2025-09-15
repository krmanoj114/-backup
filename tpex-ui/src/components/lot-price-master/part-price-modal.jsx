import { TpexTable } from '../../common/components/tables';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { useState, useEffect } from 'react';
import Modal from 'react-bootstrap/Modal';
import "./lot-price-master.css";
import { columnsPopup } from './columns-details';

function TpexPartPriceModal({ show, popupRows, selectedrow, onHide, validationObj }) {

    const [popupRows2, setPopupRows2] = useState([]);
    const [searchcriteria, setSearchCriteria] = useState({});
    const [validationObject, setValidationObject] = useState({});

    useEffect(() => {
        setPopupRows2(popupRows);
    }, [popupRows]);

    useEffect(() => {
        console.log(validationObject);
        setValidationObject(validationObj);
    }, [validationObj])

    useEffect(() => {
        setSearchCriteria(selectedrow);
    }, [selectedrow])

    function updateParentRowsStateFromChild(updatedRowsComingFromChild) {
        setPopupRows2([...updatedRowsComingFromChild]);
    }

    function addEditDataForParent(add, edit) {
        console.log(' ');
    }      

    return (

        <Modal
            show={show}
            onHide={onHide}
            size="lg"
            aria-labelledby="contained-modal-title-vcenter"
            centered
            backdropClassName="modalTable-backdrop"
            className="modal-table"
        >
            <Modal.Header closeButton>
                <i className="bg-border"></i>
                <Modal.Title>{LABEL_CONST.LOT_PART_PRICE_MASTER} </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div>
                    <div className="row">
                        <div className="col-2 label-text">{LABEL_CONST.EFFECTIVE_FROM_MONTH}</div>
                        <div className="col-2 field-text">: {searchcriteria && searchcriteria.effectiveFromMonth ? searchcriteria.effectiveFromMonth : ""}</div>
                        <div className="col-2 label-text">{LABEL_CONST.EFFECTIVE_TO_MONTH}</div>
                        <div className="col-2 field-text">: {searchcriteria && searchcriteria.effectiveToMonth ? searchcriteria.effectiveToMonth : ""}</div>
                        <div className="col-2 label-text">{LABEL_CONST.CAR_FAMILY}</div>
                        <div className="col-2 field-text">: {searchcriteria && searchcriteria.carFamily && searchcriteria.carFamily.label ? searchcriteria.carFamily.label : ''}</div>
                    </div>
                    <div className="row mt-10">
                        <div className="col-2 label-text">{LABEL_CONST.DESTINATION}</div>
                        <div className="col-2 field-text">: {searchcriteria && searchcriteria.destination && searchcriteria.destination.label ? searchcriteria.destination.label : ""}</div>
                        <div className="col-2 label-text">{LABEL_CONST.LOT_CODE}</div>
                        <div className="col-2 field-text">: {searchcriteria && searchcriteria.lotCode ? searchcriteria.lotCode : ""}</div>
                        <div className="col-2 label-text">{LABEL_CONST.CURRENCY}</div>
                        <div className="col-2 field-text">: {searchcriteria && searchcriteria.currency ? searchcriteria.currency : ""}-{searchcriteria && searchcriteria.curreDesc ? searchcriteria.curreDesc : ""}</div>
                    </div>
                </div>

                {/* table  */}
                <div className="grid-panel mt-10 p-0 pb-0">
                    <TpexTable
                        rows={popupRows2}
                        idName="partNumber"
                        moduleName="LOT_PRICE_MASTER"
                        rowPerPage={10}
                        selectAll={false}
                        selectRow={false}
                        columns={columnsPopup}
                        isCrud={false}
                        pagination={true}
                        filter={true}
                        serverSideFilter={false}
                        editTable={true}
                        primaryKey="partNumber"
                        validationObj={validationObj}
                        customActions={{ name: LABEL_CONST.SAVE }}
                        customEdit={true}
                        otherData={searchcriteria}
                        updateParentRowsStateFromChild={updateParentRowsStateFromChild}
                        addEditDataForParent = { addEditDataForParent }
                    />
                </div>
            </Modal.Body>

        </Modal>

    );
}

export default TpexPartPriceModal;
