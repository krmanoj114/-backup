import { PmiSelect } from "./PmiSelect";
import { isDisabled , isMandatory} from './partMasterUtil.js';

const DataTableEditRow = ({ 
  row, 
  isCheck, 
  columns, 
  primaryKey, 
  index,
  dataForEdit, 
  editInputBoxChange
}) =>
{

const controlSelection = (type, flg, val) => (type === val && flg === true);

  return (
    <>
      {columns.map((column) => {
        if (isCheck.includes(row[primaryKey])) {
          if (controlSelection(column.type, column.editFlag, 'select')) {
            return <td key={`td-${column.id}${index}`} className={isMandatory(column.id, dataForEdit[row[primaryKey]], column.required)}>
              <PmiSelect
                moduleName={`${column.id}__${index}`}
                options={column.selectList}
                margin="minWdt"
                blankRequired={false}
                selected={dataForEdit[row[primaryKey]][column.id]}
                onChangeSelection={(event) => editInputBoxChange(event, row[primaryKey])}
                hasValue={dataForEdit[row[primaryKey]][column.id]}
                isDisabled={isDisabled(column.id, dataForEdit[row[primaryKey]])}
                columnName = {column.id}
              />
            </td>
          }          
          else if (controlSelection(column.type, column.editFlag, 'text')){
            return <td key={`td-${column.id}${index}`} className={isMandatory(column.id, dataForEdit[row[primaryKey]], column.required)}>
              <input
                key={`txt-${column.id}${index}`}
                id={`${column.id}${index}`}
                name={`${column.id}__${index}`}
                type="text"
                className={`form-control`}
                maxLength={column.maxLength}
                value={dataForEdit[row[primaryKey]][column.id] || ""}                
                disabled={isDisabled(column.id, dataForEdit[row[primaryKey]])}
                onChange={event => editInputBoxChange(event, row[primaryKey])}
              />
            </td>
          } else {
            return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status`}>
            <span>{row[column.id] || ''}</span></td>
          } 

        }
        else {
            return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status`}>
              <span>{row[column.id] || ''}</span></td>
        }

      })}

    </>

  )
};

export default DataTableEditRow;