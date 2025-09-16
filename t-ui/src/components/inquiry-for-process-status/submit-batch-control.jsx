import { useState, useContext } from "react";
import EditDataContext from "../../components/VesselBookingMaster/EditDataContext";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";
import { TpexSelect } from "../../common/components/select";
import { formatedDate, formatedDate_yyyymm } from "../../helpers/util";
import { TpexMultiSelectSeach } from "../../common/components/multiselect/multiselect";


export const DateControl = ({
    controlType,
    labelName,
    index,
    fieldName,
    isRequired,
}) => {
    const editDataContextRef = useContext(EditDataContext);
    const [selectedDateVal, setSelectedDateVal] = useState(null);
    const classText = isRequired === "yes" ? "mandatoryControl" : "";
    return (
        <div className="col-4">
            <label htmlFor={`${fieldName}-${index}`}>{labelName}</label>
            <div className={`customDatePicker ${classText}`}>
            <TpexDatePicker
                dateFormat={controlType === 'monthYear' ? "yyyy/MM" : "dd/MM/yyyy"}
                dateSelected={selectedDateVal}
                id={`${fieldName}-${index}`}
                    handleDateSelected={e => {
                        setSelectedDateVal(e);
                        let objToEdit = {};
                        objToEdit[`${fieldName}`] = controlType === 'monthYear' ? formatedDate_yyyymm(e) : formatedDate(e);

                        if (editDataContextRef.current[index]) {
                            editDataContextRef.current[index] = objToEdit;
                        } else {
                            editDataContextRef.current.push(objToEdit);
                        }
                    }
                    }
                showMonthYearPicker={controlType ==='monthYear' ? true : false}
            />
            </div>
        </div>
    );
};

export const DropDownControl = ({
    labelName,
    index,
    selectDropdownList,
    fieldName,
    isRequired,
}) => {
    const editDataContextRef = useContext(EditDataContext);
    const [selectDropdownData, setSelectDropdownData] = useState('');
    const classText = isRequired === "yes" ? "mandatoryControl" : "";
    return (
    <div className="col-4">
        <label htmlFor={`${fieldName}-${index}`}>{labelName}</label>
        <div className={`customDatePicker ${classText}`}>
            <TpexSelect
                moduleName={`${fieldName}-${index}`}
                options={selectDropdownList}
                onChangeSelection={e => {
                     setSelectDropdownData(e.target.value);
                     const objToEdit = {};
                     objToEdit[`${fieldName}`] = e.target.value;
                        if (editDataContextRef.current[index]) {
                            editDataContextRef.current[index] = objToEdit;
                        } else {
                            editDataContextRef.current.push(objToEdit);
                        }
                    }
                }
                hasValue={selectDropdownData}
            />
        </div>
    </div>
    )
};

export const SuggestiveDropDown = ({
    labelName,
    index,
    dataList,
    fieldName,
    isRequired,
}) => {
    const editDataContextRef = useContext(EditDataContext);
    const [selectedAddPara, setSelectedAddPara] = useState([]);
    const classText = isRequired === "yes" ? "mandatoryControl" : "";
    return (
        <div className="col-6">
        <div className={`custom-multiSelect ${classText}`}>
            <label htmlFor={`${fieldName}-${index}`}>{labelName}</label>
            <TpexMultiSelectSeach
                id={`${fieldName}-${index}`}
                handleSelectedOptions={e => {
                    setSelectedAddPara(e);
                    const objToEdit = {};
                    objToEdit[`${fieldName}`] = (e.map(k => k.value)).toString();
                       if (editDataContextRef.current[index]) {
                           editDataContextRef.current[index] = objToEdit;
                       } else {
                           editDataContextRef.current.push(objToEdit);
                       }
                   }
               }
                name={`${fieldName}-${index}`}
                noOptionsText="Select Parameter"
                value={selectedAddPara}
                staticValues={dataList}   // required when server side is false
            />
        </div>
        </div>
    );

};


export const InputTextControl = ({
    labelName,
    fieldName,
    index,
    isRequired,
    maxlength,
    processId
}) => {
    const editDataContextRef = useContext(EditDataContext);
    const [textValue, setTextValue] = useState('');
    const classText = isRequired === "yes" ? "mandatoryControl" : "";
    return (
        <div className={`col-6 ${fieldName} ${processId}`}>
        <div className={`custom-input ${classText}`}>
            <label htmlFor={`${fieldName}-${index}`}>{labelName}</label>
            <input
            type="text"
            className="form-control"
            id={`${fieldName}-${index}`}
            name={fieldName}
            required={isRequired}
            maxLength={maxlength}
            defaultValue={textValue}
            onBlur={(e) => {
                setTextValue(e.target.value);
                const objToEdit = {};
                objToEdit[`${fieldName}`] = e.target.value;
                if (editDataContextRef.current[index]) {
                    editDataContextRef.current[index] = objToEdit;
                } else {
                    editDataContextRef.current.push(objToEdit);
                }
                }
            }
            />
        </div>
        </div>
    );

};