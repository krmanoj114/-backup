import { ddmmTOmmddChange } from "../../../helpers/util";
import "../../../styles/table.css";
import Checkbox from "../checkbox";
import { TpexMultiSelectSeach } from "../multiselect/multiselect";
import { TpexSelect } from "../select";
import { CODE_MASTER_ID, INVOICE_SERVICE } from "../../../constants/constant";
import { TpexDatePicker } from "../datepicker/datepicker";

const getMandatoryClass = isRequired => isRequired ? 'mandatoryControl' : '';
const isDisabledForEditAdd = _isDisabled => _isDisabled ? 'disabled' : '';
const getColumnFormat = columnFormat => columnFormat || "dd/MM/yyyy";
const getMinDate = minDate => minDate ? new Date(minDate) : null;
const getStep = columnStep => columnStep || '';

const getDateSelected = (addData, addindex, column) => {
    if (addData[addindex][column.id]) {
        if ((column.format && column.format === "yyyy/MM")) {
            return new Date(addData[addindex][column.id]);
        } else {
            return new Date(ddmmTOmmddChange(addData[addindex][column.id]));
        }
    }
    return null;
};

const getIsCheckedTrueorFalse = value => {
    return !!((value === "Y" || value === true));
};

const getDDValueLabelObject = (code, obj) => {
    console.log(code, obj)
    return obj.find(o => o.label === code);
};

const getCodeFromCodeName = codeName => {
    return codeName.split("-")[0];
};

const isDisabled = (column, data, codeMaster) => {
    //conditional disabling    
    if (codeMaster == CODE_MASTER_ID.PAYMENT_TERM_MASTER && ["NO_OF_DAYS", "DAY_OF_MTH", "MTH_NO"].includes(column.id)) {
        return data[column.id + '_DISABLE'] ? 'disabled' : '';
    } else {
        return "";   
    }
};

const isMandatory = (column, data, codeMaster) => {
    //conditional mandatory
    if (codeMaster == CODE_MASTER_ID.PAYMENT_TERM_MASTER && ["NO_OF_DAYS", "DAY_OF_MTH", "MTH_NO"].includes(column.id)) {
        return data[column.id + '_DISABLE'] ? '' : 'mandatoryControl';
    }
    return column.required ? "mandatoryControl" : ""
}

export const TableAddRow = ({
    columns,
    addData,
    addindex,
    codeMaster,
    dropDownData,
    inputBoxChange,
    handleDateSelected,
    handleRowCheckBoxClick,
    onChangeDDSelectionAddNonSuggestive,
    onChangeDDSuggestiveSelectionInAdd
}) => {
    let k = 0;
    return (
        <>
            {
                columns.map(column => {
                    let tdItem = null;
                    
                    switch (column.type) {
                        case 'string':
                            tdItem = (
                                <td
                                    key={`str-${column.id}-${k++}`}
                                    className={getMandatoryClass(column.required)}
                                >
                                    <input type="text" 
                                        value={addData[addindex][column.id]} 
                                        placeholder={column.doNotEditInAdd ? '' : `Enter ${column.name}`} 
                                        name={`${column.id}__${addindex}`} 
                                        className="form-control" 
                                        minLength={column.min}
                                        maxLength={column.max}
                                        id={`add-${column.id}-${addindex}`} 
                                        onChange={event => inputBoxChange(event)}
                                        disabled={isDisabledForEditAdd(column.doNotEditInAdd)}
                                    />
                                </td>
                            );
                        break;
                        case 'date':
                            tdItem = (
                                <td
                                    key={`date-${column.id}-${k++}`}
                                    className={getMandatoryClass(column.required)}
                                >
                                    <TpexDatePicker
                                        id={`add-${column.id}-${addindex}`}
                                        name={`${column.id}__${addindex}`}
                                        dateFormat={getColumnFormat(column.format)}
                                        minDate={getMinDate(column.minDate)}
                                        dateSelected={getDateSelected(addData, addindex, column)}
                                        handleDateSelected={date => handleDateSelected(date, column.id, addindex, column.format)}
                                        showMonthYearPicker={!!(column.format && column.format === "yyyy/MM")}
                                        isDirectDatePicker={true}
                                    />
                                </td>
                            );
                        break;
                        case 'dropdown':
                            if (column.suggestive) {
                                tdItem = (
                                    <td
                                        key={`add-${column.id}-${addindex}`}
                                        className={getMandatoryClass(column.required)}
                                    >
                                        <TpexMultiSelectSeach
                                            isMandatory={true}
                                            insideGrid={true}
                                            searchUrl={''}
                                            name={`${column.id}-${addindex}`}
                                            noOptionsText="Search..."
                                            value={getDDValueLabelObject(addData[addindex][column.id], dropDownData[column.id])}
                                            isMulti={false}
                                            id={`${column.id}-${addindex}`}
                                            serverSide={false}
                                            staticValues={dropDownData[column.id]}
                                            BASE_URL={INVOICE_SERVICE}
                                            handleSelectedOptions={e => onChangeDDSuggestiveSelectionInAdd(e, column.id, addindex)}
                                        />
                                    </td>
                                );
                            } else {
                                tdItem = (
                                    <td key={`add-${column.id}-${addindex}`} className={column.required ? "mandatoryControl" : ""}>
                                        <TpexSelect
                                            moduleName={`${column.id}-${addindex}`}
                                            selected=""
                                            id="id"
                                            options={dropDownData[column.id]}
                                            hasValue={getCodeFromCodeName(addData[addindex][column.id])}
                                            onChangeSelection={e => onChangeDDSelectionAddNonSuggestive(e, column.id, addindex)}
                                            blankRequired={CODE_MASTER_ID.FINAL_DEST_MASTER == codeMaster}
                                        />
                                    </td>
                                );
                            }
                        break;
                        case 'number':
                            tdItem = (
                                <td
                                    key={`number-${column.id}-${k++}`}
                                    className={isMandatory(column, addData[addindex], codeMaster)}
                                >
                                    <input type="number" 
                                        step={getStep(column.step)} 
                                        value={addData[addindex][column.id]} 
                                        name={`${column.id}__${addindex}`} 
                                        min={column.min ? column.min : ""}
                                        max={column.max ? column.max : ""}
                                        disabled={isDisabled(column, addData[addindex], codeMaster)}
                                        className="form-control" 
                                        id={`add-${column.id}-${addindex}`} 
                                        onChange={event => inputBoxChange(event)} 
                                    />
                                </td>
                            );
                        break;
                        case 'checkbox':
                            tdItem = (
                                <td
                                    key={`checkbox-${column.id}-${k++}`}
                                    className={getMandatoryClass(column.required)}
                                >
                                    <Checkbox
                                        key={`checkbox-${column.id}-${addindex}`}
                                        type="checkbox"
                                        name={`${column.id}__${addindex}`}
                                        id={`checkbox-${column.id}-${addindex}`}
                                        handleClick={e => handleRowCheckBoxClick(e, column.id, addindex)}
                                        isChecked={getIsCheckedTrueorFalse(addData[addindex][column.id])}

                                    />
                                </td>
                            );
                        break;
                        default:
                            tdItem = (
                                <td key={`str-${column.id}-${k++}`}>
                                    <input
                                        type="text"
                                        value={addData[addindex][column.id]}
                                        name={`${column.id}__${addindex}`}
                                        className="form-control"
                                        id={`add-${column.id}-${addindex}`}
                                        onChange={event => inputBoxChange(event)}
                                    />
                                </td>
                            );
                    }

                    return tdItem;
                })
            }
        </>
    );
};