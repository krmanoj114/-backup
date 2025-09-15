import { ddmmTOmmddChange, getFilename } from "../../../helpers/util";
import "../../../styles/table.css";
import { TableInputNumber } from "./TableInputNumber";
import { TableInputText } from "./TableInputText";
import { TpexSelect } from '../select';
import { TpexMultiSelectSeach } from "../multiselect/multiselect";
import Checkbox from "../checkbox";
import { CODE_MASTER_ID, INVOICE_SERVICE } from "../../../constants/constant";
import { TpexDatePicker } from "../datepicker/datepicker";

const getDateValue = value => {
    return new Date(value);
};

const getDateSelected = (dataForEdit, row, column, primaryKey) => {
    let dateValue = '';
    if (dataForEdit[row[primaryKey]][column.id]) {
        if ((column.format && column.format === "yyyy/MM")) {
            dateValue = getDateValue(dataForEdit[row[primaryKey]][column.id]);
        } else {
            dateValue = getDateValue(ddmmTOmmddChange(dataForEdit[row[primaryKey]][column.id]));
        }
    }
    return dateValue;
};

const getDDValueLabelObject = (code, obj) => {
    return obj.find(o => o.label === code);
};

const getCodeFromCodeName = codeName => {
    return codeName.split("-")[0];
};

const getIsCheckedTrueorFalse = value => {
    return !!((value === "Y" || value === true));
};

const isMandatory = (column, data, codeMaster) => {    
    //conditional mandatory
    if (codeMaster == CODE_MASTER_ID.PAYMENT_TERM_MASTER && ["NO_OF_DAYS", "DAY_OF_MTH", "MTH_NO"].includes(column.id)) {
        
        if(data.PAYMENT_TERM === 2 && (column.id === "DAY_OF_MTH" || column.id === "MTH_NO")){
            return 'mandatoryControl';
        }
        if(data.PAYMENT_TERM === 1 && column.id === "NO_OF_DAYS"){
            return 'mandatoryControl';
        }
        return data[column.id + '_DISABLE'] ? '' : 'mandatoryControl';
    }
    return column.required ? "mandatoryControl" : ""
}

export const TableEditRow = ({
    row,
    codeMaster,
    isCheck,
    columns,
    primaryKey,
    index,
    dataForEdit,
    dropDownData,
    editInputBoxChange,
    handleEditDateSelected,
    handleDownload,
    handlePopUp,
    onChangeDDSelection,
    onChangeDDSuggestiveSelection,
    handleRowCheckBoxClick
}) => {
    const isAlreadyChecked = isCheck.includes(row[primaryKey]);
    const getColumnFormat = column => column.format ? column.format : "dd/MM/yyyy";
    const getColumnMinDate = column => column.minDate ? new Date(column.minDate) : "";
    const isColumnInFormat = column => !!((column.format && column.format === "yyyy/MM"));
    const getMandatoryClass = column => column.required ? "mandatoryControl" : "";
    const getColumnTextEnd = column => column.type === "number" ? ' text-end' : '';

    const getTDByType = (type, obj, dataForEdit, editInputBoxChange, dropDownData) => {
        const {column, row, primaryKey, index, codeMaster} = obj;
        switch (type) {
            case 'number':
                return (
                    <td
                        key={`td-${column.id}${row[primaryKey]}`} className={isMandatory(column, dataForEdit[row[primaryKey]], codeMaster)}>
                        <TableInputNumber
                            column={column}
                            row={row}
                            primaryKey={primaryKey}
                            index={index}
                            codeMaster={codeMaster}
                            dataForEdit={dataForEdit}
                            editInputBoxChange={editInputBoxChange}                            
                        />
                    </td>
                );
            case 'date':
                let showMonthYearPickerFlag = isColumnInFormat(column);
                let dateSelected = getDateSelected(dataForEdit, row, column, primaryKey);
                return (
                        <td key={`td-${column.id}${row[primaryKey]}`} className={getMandatoryClass(column)}>
                            <TpexDatePicker
                                id={`${column.id}__${index}`}
                                name={`${column.id}__${index}`}
                                dateFormat={getColumnFormat(column)}
                                dateSelected={dateSelected}
                                handleDateSelected={(date, _name) => handleEditDateSelected(date, `${column.id}`, row[primaryKey], column.format)}
                                minDate={getColumnMinDate(column)}
                                showMonthYearPicker={showMonthYearPickerFlag}
                                isDirectDatePicker={true}
                            />
                        </td>
                    );
            case 'dropdown':

                if (column.suggestive) {
                    return (
                        <td key={`td-${column.id}${row[primaryKey]}`} className={getMandatoryClass(column)}>
                            <TpexMultiSelectSeach
                                searchUrl={''}
                                name={`${column.id}__${index}`}
                                noOptionsText="Search..."
                                value={getDDValueLabelObject(dataForEdit[row[primaryKey]][column.id], dropDownData[column.id])}
                                isMulti={false}
                                id={`${column.id}__${index}`}
                                serverSide={false}
                                staticValues={dropDownData[column.id]}
                                BASE_URL={INVOICE_SERVICE}
                                handleSelectedOptions={e => onChangeDDSuggestiveSelection(e, row[primaryKey], column.id)}

                            />
                        </td>
                    );
                } else {
                    return (
                        <td key={`td-${column.id}${index}`} className={getMandatoryClass(column)}>
                            <TpexSelect
                                moduleName={`${column.id}-${index}`}
                                selected=""
                                id="id"
                                options={dropDownData[column.id]}
                                hasValue={getCodeFromCodeName(dataForEdit[row[primaryKey]][column.id])}
                                onChangeSelection={e => onChangeDDSelection(e, row[primaryKey], column.id)}
                                blankRequired={false}
                            />
                        </td>
                        );
                }

            case 'string':
                return (
                        <td key={`td-${column.id}${row[primaryKey]}`} className={getMandatoryClass(column)}>
                            <TableInputText
                                column={column}
                                row={row}
                                primaryKey={primaryKey}
                                index={index}
                                dataForEdit={dataForEdit}
                                editInputBoxChange={editInputBoxChange}
                            />
                        </td>
                        );
            case 'checkbox':
                return (
                        <td key={`td-${column.id}${row[primaryKey]}`} className={getMandatoryClass(column)}>
                            <Checkbox
                                key={`td-${column.id}${row[primaryKey]}`}
                                type="checkbox"
                                name={`${column.id}-${index}`}
                                id={`${column.id}-${index}`}
                                handleClick={e => handleRowCheckBoxClick(e, row[primaryKey], column.id)}
                                isChecked={getIsCheckedTrueorFalse(dataForEdit[row[primaryKey]][column.id])}
                            />
                        </td>
                        );
        }
    };

    return (
        <>
            {columns.map((column) => {

                if(isAlreadyChecked && column.editable === true){
                    let obj = {
                        column : column,
                        row : row,
                        primaryKey : primaryKey,
                        index : index,
                        codeMaster : codeMaster
                    }
                    return getTDByType(column.type, obj, dataForEdit, editInputBoxChange, dropDownData);
                }
                
                // link 
                if (column.link && column.link === true) {
                    return (
                        <td
                            id={`${column.id}${index}`}
                            key={column.id}
                        >
                            <a
                                rel="noreferrer"
                                target="_blank"
                                className={row[column.id]}
                                href={row[column.id]}
                                onClick={e => handleDownload(e)} 
                            >{getFilename(row[column.id])}</a>
                        </td>
                    );
                } else if (column.popup && column.popup === true) {
                    return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status`}>
                        <span id={`${column.id}-${index}`} onClick={() => handlePopUp(row)}>{row[column.id]}</span>
                        </td>
                } else {
                    return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status ${getColumnTextEnd(column)}`}>
                        <span>{row[column.id] || ''}</span>
                        </td>
                }

            })}


        </>

    );
};
