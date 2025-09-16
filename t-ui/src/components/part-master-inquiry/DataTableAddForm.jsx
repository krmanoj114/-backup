import React from 'react'
import { PmiSelect } from "./PmiSelect";
import { isDisabled , isMandatory} from './partMasterUtil.js';

const DataTableAddForm = (props) => {

    return (
    <>
      {
        props.addData.map((_add, i) => {
            const addindex = i;
            return (
            <tr key={`addData-${addindex}`}>
            <td className="checkboxCol">
                <div className="minus-box"><i className="minus-icon" onClick={event => props.removeAddForm(event, addindex)}></i></div>
            </td>
            {
            props.columns.map((column) => {
                if (column.type === 'select') {
                    return <td key={`td-${column.id}${addindex}`} className={isMandatory(column.id, props.addData[addindex], column.required)}>
                        <PmiSelect
                            moduleName={`${column.id}__${addindex}`}
                            options={column.selectList} 
                            margin="minWdt" 
                            blankRequired={column.isBlank}
                            onChangeSelection={(event) => props.inputBoxChange(event, addindex)}
                            hasValue={props.addData[addindex][column.id]}
                            isDisabled={isDisabled(column.id, props.addData[addindex])}
                            columnName = {column.id}
                        />
                    </td>
                } else if (column.type === 'text'){
                    return <td key={`td-${column.id}${addindex}`} className={isMandatory(column.id, props.addData[addindex], column.required)}>
                        <input
                            key={`txt-${column.id}${addindex}`}
                            id={`${column.id}${addindex}`}
                            name={`${column.id}__${addindex}`}
                            type="text"
                            className={`form-control`}
                            maxLength={column.maxLength}
                            value={props.addData[addindex][column.id]}
                            onChange={event => props.inputBoxChange(event, addindex)}
                            disabled={isDisabled(column.id, props.addData[addindex])}
                        />
                    </td>
                }
            })
            }
            </tr>)
        })
    }
    </>
  )
}

export default DataTableAddForm;
