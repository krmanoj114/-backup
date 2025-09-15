import { CODE_MASTER_ID } from "../../../constants/constant";

const isDisabled = (column, data, codeMaster) => {
    //conditional disabling
    const {id, editable} = column;
    if (
        codeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER &&
        ["NO_OF_DAYS", "DAY_OF_MTH", "MTH_NO"].includes(id)
    ) {
        if(data['PAYMENT_TERM'] === "1" && (column.id === "DAY_OF_MTH" || column.id === "MTH_NO")){
            return 'disabled';
        }
        if(data['PAYMENT_TERM'] === "2" && column.id === "NO_OF_DAYS"){
            return 'disabled';
        }
        return data[id + '_DISABLE'] ? 'disabled' : '';
    }

    return editable ? '' : 'disabled';
};

export const TableInputNumber = ({
    column,
    row,
    primaryKey,
    index,
    codeMaster,
    dataForEdit,
    editInputBoxChange
}) => {
    
    let valueForInput = dataForEdit[row[primaryKey]][column.id];
    if (typeof valueForInput === 'string' && valueForInput.indexOf(',') !== -1) {
        valueForInput = parseFloat(valueForInput.replace(/,/g, ''));
    }
    
    return (
        <>
            <input
                key={`num-${column.id}${row[primaryKey]}`}
                id={`${column.id}${index}`}
                name={`${column.id}__${index}`}
                type="number"
                step={column.step ? column.step : ""}
                className={`form-control`}
                value={valueForInput}
                onChange={event => editInputBoxChange(event, row[primaryKey])}
                disabled={isDisabled(column, dataForEdit[row[primaryKey]], codeMaster)}
                min={column.min ? column.min : ""}
                max={column.max ? column.max : ""}
            />
        </>
    );
}

