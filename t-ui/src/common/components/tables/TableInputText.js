export function TableInputText({ column, row, primaryKey, index, dataForEdit, editInputBoxChange }) {
    return (
        <>
            <input
                key={`txt-${column.id}${row[primaryKey]}`}
                id={`${column.id}${index}`}
                name={`${column.id}__${index}`}
                type="text"
                className={`form-control`}
                value={dataForEdit[row[primaryKey]][column.id] || ""}
                onChange={event => editInputBoxChange(event, row[primaryKey])}
                disabled={column.editable ? '' : 'disabled'}
                minLength={column.min}
                maxLength={column.max}
            />

        </>
    )
}