import { TpexSelect } from "../../common/components/select";
import Checkbox from '../../common/components/checkbox';

export const EnginePartTableEditRow = ({ 
  row, 
  isCheck, 
  columns, 
  primaryKey, 
  index,
  dataForEdit, 
  editInputBoxChange, 
  selectBoxChange, 
  handlePLSClick, 
  chkBoxFlag
}) => {

  const colSelection = colId => (
    colId === 'priorityOne' ||
    colId === 'priorityTwo' ||
    colId === 'priorityThree' ||
    colId === 'priorityFour' ||
    colId === 'priorityFive'
  ); 
   
  const controlSelection = (type, flg, val) => (
    (type === val && flg === true) ? true : false
  );

  const conditionClassName = (colId, key) => colId === "portOfDischargeLabel" ? key : ''; 
 
  const unCheckedControls = column => {
    let tdControl = null;
    
    if (column.type === 'checkbox') {
      tdControl = (
        <td key={`td-${column.id}${index}`} className="mandatoryControl">
          <Checkbox
            type="checkbox"
            name={`${column.id}__${index}`}
            id={`${column.id}__${index}`}
            isChecked={chkBoxFlag(row[column.id])}
            isDisabled={true}
          />
        </td>
      ); 
    } else if (colSelection(column.id) && row[column.id].length > 1) {
      tdControl = (
        <td
          id={`${column.id}${index}`}
          key={column.id}
          className={`${row[column.id]}-status`}
        ><span>{row[column.id].filter(n => n).join(', ')}</span></td>
      );
    } else {
      tdControl = (
        <td
          id={`${column.id}${index}`}
          key={column.id}
          className={`${row[column.id]}-status`}
        >
          <span
            data-title={conditionClassName(`${column.id}`, row[column.id])}
            className={conditionClassName(`${column.id}`, "tooltip-title")}
          >{(typeof (row[column.id]) == "object" ? row[column.id].label : row[column.id]) || ''}</span>
        </td>
      );
    }

    return tdControl;
  };

  return (
    <>
      {
        columns.map(column => {
          if (isCheck.includes(row[primaryKey])) {

            if (controlSelection(column.type, column.editFlag, 'select')) {
              return <td key={`td-${column.id}${index}`} className="mandatoryControl">
                <TpexSelect
                  moduleName={`${column.id}__${index}`}
                  options={column.selectList}
                  margin="minWdt"
                  blankRequired={false}
                  selected={dataForEdit[row[primaryKey]][column.id]}
                  onChangeSelection={(event) => selectBoxChange(event, row[primaryKey])}
                  hasValue={dataForEdit[row[primaryKey]][column.id]}
                />
              </td>
            } else if (column.type === 'checkbox') {
              return <td key={`td-${column.id}${index}`} className="mandatoryControl">
                <Checkbox
                  type="checkbox"
                  name={`${column.id}__${index}`}
                  id={`${column.id}__${index}`}
                  handleClick={(e) => handlePLSClick(e, row[primaryKey])}
                  isChecked={chkBoxFlag(dataForEdit[row[primaryKey]][column.id])}
                />
              </td>
            } else if (controlSelection(column.type, column.editFlag, 'text')){
              return <td key={`td-${column.id}${index}`} className="mandatoryControl">
                <input
                  key={`txt-${column.id}${index}`}
                  id={`${column.id}${index}`}
                  name={`${column.id}__${index}`}
                  type="text"
                  className={`form-control`}
                  maxLength={column.maxLength}
                  value={dataForEdit[row[primaryKey]][column.id] || ""}
                  onChange={event => editInputBoxChange(event, row[primaryKey])}
                />
              </td>
            } else {
              return <td id={`${column.id}-${index}`} className={`nonEdit`} key={`${column.id}${index}`}><span>{(typeof (row[column.id]) == "object" ? row[column.id].label : row[column.id]) || ''}</span></td>
            }
          } else {
            return unCheckedControls (column);
          }
        })
      }
    </>
  );
}