import { TpexDatePicker } from "../../common/components/datepicker/datepicker";
import { isPastDate } from "../../helpers/util";
import "../../styles/table.css";

export const TableEditRow = ({
    row,
    isCheck,
    column,
    idName,
    index,
    handleEditDateSelected,
    dataForEdit
}) => {

    const isAlreadyChecked = isCheck.includes(row[idName]);
    let dateSelected = "2/12/2023";
    
    if (isAlreadyChecked && column.date === true) {
        dateSelected = getDateSelected(dataForEdit, row, column, idName);
    }

    if(isAlreadyChecked && column.id === 'effctiveToDt') {
        if(!isPastDate(row.effctiveToDt)){
            return <td key={`td-${column.id}${row[idName]}`} className="mandatoryControl">
                <TpexDatePicker
                    id={`${column.id}__${index}`}
                    name={`${column.id}__${index}`}
                    dateSelected={dateSelected}
                    handleDateSelected={(date) => handleEditDateSelected(date, `${column.id}`, row[idName])}
                    isDirectDatePicker={true}
                />
            </td>
        }
    }
    return (
        column.id !== 'check' && 
        <td id={`${column.id}-${index}`} key={`${column.id}`} className={`${row[column.id]}-status ${column.type === "number" ? ' text-end' : ''}`}>
            <span>
            {row[column.id]}
            </span>
        </td>

    )
}

const parseDateMonthYear = s => {
    let [d, m, y] = s.split(/\D/);
    return new Date(y, m-1, d);
};

function getDateSelected(dataForEdit, row, column, idName) {
    if (dataForEdit[row[idName]][column.id] !== "") {
        return parseDateMonthYear(dataForEdit[row[idName]][column.id]);
    }
}
