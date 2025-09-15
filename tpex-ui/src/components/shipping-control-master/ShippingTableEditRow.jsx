import { TpexSelect } from "../../common/components/select";
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import Checkbox from '../../common/components/checkbox';
import { ddmmTOmmddChange, formatedDate } from "../../helpers/util";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

export function ShippingTableEditRow({ 
  row, 
  isCheck, 
  columns, 
  primaryKey, 
  index,
  dataForEdit, 
  editInputBoxChange, 
  selectBoxChange, 
  handleSelectedOptions,
  selectBoxList, 
  handlePLSClick, 
  handleEditDateSelected, 
  classNameRetun, 
  chkBoxFlag, 
  dateSelected, 
  screenModule 
}) 
{

  function colSelection(colId){
    return colId === 'priorityOne' || colId === 'priorityTwo' || colId === 'priorityThree' || colId === 'priorityFour' || colId === 'priorityFive';
  } 
   function controlSelection(type, flg, val){
   return !!((type === val && flg === true));
 }
 function conditionClassName(colId, key){
     return (colId === "portOfDischargeLabel" || colId === "partName") ? key : '';
  } 

  const mandClass = d => (d?.required) ? "mandatoryControl" : '';
 
  function vanDateEdit(d1, d2) {
    let date1 = new Date(ddmmTOmmddChange(d1));
    let sysDate = ddmmTOmmddChange(formatedDate(new Date()));
    let systemDate = new Date(sysDate);
    let date2 = new Date(ddmmTOmmddChange(d2));
    let result;
    if (date1 >= systemDate) {
        result = false;
    } else if (date1 < systemDate && date2 >= systemDate) {
        result = true;
    }
    return result;
  }
  function multiSelectControlPart(column, index, row, primaryKey, dataForEdit  ) {
    if (screenModule === 'MIX_PRIVILEGE_MASTER') {
      if (!(vanDateEdit(row['effFrom'], row['effTo']))) {
        return <td key={`td-${column.id}${index}`} className={classNameRetun(column.id)}>
          <div className="custom-multiSelect">
            <TpexMultiSelectSeach
              id={`id-${column.id}-${index}`}
              handleSelectedOptions={(e) => handleSelectedOptions(e, row[primaryKey], `${column.id}__${index}`, row[column.id + 'Obj'])}
              name={column.id}
              noOptionsText={column.id}
              value={dataForEdit[row[primaryKey]][column.id + 'Obj']}
              isMulti={column.isMulti}
              serverSide={false}
              staticValues={selectBoxList(column.id, column.multiSelectList, dataForEdit[row[primaryKey]])}
            />
          </div>
        </td>
      } else {
        if (colSelection(column.id) && row[column.id].length > 1) {
          return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status`}>
            <span>{row[column.id].filter(n => n).join(', ')}</span></td>
        } else {
          return <td id={`${column.id}-${index}`} key={`${column.id}${index}`}><span>{row[column.id] || ''}</span></td>
        }
      }
    } else {
      return <td key={`td-${column.id}${index}`} className={classNameRetun(column.id)}>
        <div className="custom-multiSelect">
          <TpexMultiSelectSeach
            id={`id-${column.id}-${index}`}
            handleSelectedOptions={(e) => handleSelectedOptions(e, row[primaryKey], `${column.id}__${index}`)}
            name={column.id}
            noOptionsText={column.id}
            value={dataForEdit[row[primaryKey]][column.id + 'Obj']}
            isMulti={column.isMulti}
            serverSide={false}
            staticValues={selectBoxList(column.id, column.multiSelectList, dataForEdit[row[primaryKey]])}
          />
        </div>
      </td>
    }
  } 

  function dateControlPart(column, index, row,primaryKey,dataForEdit ){
    if (screenModule === 'MIX_PRIVILEGE_MASTER') {
      if (vanDateEdit(row['effFrom'], row['effTo'])
        && column.id === 'effTo') {
        return <td key={`td-${column.id}${index}`}>
          <div className="mandatoryControl">
            <TpexDatePicker
              id={`${column.id}__${index}`}
              name={`${column.id}__${index}`}
              dateSelected={dateSelected(dataForEdit[row[primaryKey]][column.id])}
              handleDateSelected={(date) => handleEditDateSelected(date, `${column.id}`, row[primaryKey], row['effTo'])}
              isDirectDatePicker={true}
            />
          </div>
        </td>
      } else if (!vanDateEdit(row['effFrom'], row['effTo'])
        && column.id === 'effFrom') {
        return <td key={`td-${column.id}${index}`}>
          <div className="mandatoryControl">
            <TpexDatePicker
              id={`${column.id}__${index}`}
              name={`${column.id}__${index}`}
              dateSelected={dateSelected(dataForEdit[row[primaryKey]][column.id])}
              handleDateSelected={(date) => handleEditDateSelected(date, `${column.id}`, row[primaryKey], row['effFrom'])}
            />
          </div>
        </td>
      } else {
        return <td id={`${column.id}-${index}`} key={`${column.id}${index}`}><span>{row[column.id] || ''}</span></td>
      }
    } else {
      return <td key={`td-${column.id}${index}`}>
        <div className="mandatoryControl">
          <TpexDatePicker
            id={`${column.id}__${index}`}
            name={`${column.id}__${index}`}  
            dateSelected={dateSelected(dataForEdit[row[primaryKey]][column.id])}
            handleDateSelected={(date) => handleEditDateSelected(date, `${column.id}`, row[primaryKey])}
            isDirectDatePicker={true}
          />
        </div>
      </td>
    }
  }
  return (
    <>
      {columns.map((column) => {
        if (isCheck.includes(row[primaryKey]) && screenModule !=='INVOICE_SETUP_MASTER') {
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
          }
          else if (controlSelection(column.type, column.editFlag, 'multiselect')) {
            return multiSelectControlPart(column, index, row, primaryKey, dataForEdit);
          }
          else if (controlSelection(column.type, column.editFlag, 'date')) {
            return dateControlPart(column, index, row, primaryKey, dataForEdit);
          }
          else if (column.type === 'checkbox') {
            return <td key={`td-${column.id}${index}`} className="mandatoryControl">
              <Checkbox
                type="checkbox"
                name={`${column.id}__${index}`}
                id={`${column.id}__${index}`}
                handleClick={(e) => handlePLSClick(e, row[primaryKey])}
                isChecked={chkBoxFlag(dataForEdit[row[primaryKey]][column.id])}
              />
            </td>
          }
          else if (controlSelection(column.type, column.editFlag, 'text')){
            return <td key={`td-${column.id}${index}`} className={mandClass(column)}>
              <input
                key={`txt-${column.id}${index}`}
                id={`${column.id}${index}`}
                name={`${column.id}__${index}`}
                type="text"
                className={`form-control`}
                maxLength={column.maxLength}
                value={dataForEdit[row[primaryKey]][column.id] || ""}
                onChange={event => editInputBoxChange(event, row[primaryKey], column.firstLnth, column.lastLnth)}
              />
            </td>
          } else {
            return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status`}>
            <span data-title={conditionClassName(`${column.id}`, row[column.id])} className={conditionClassName(`${column.id}`, "tooltip-title")}>{row[column.id] || ''}</span></td>
          }

        } else {
          if (column.type === 'checkbox') {
            return <td key={`td-${column.id}${index}`} className="mandatoryControl">
              <Checkbox
                type="checkbox"
                name={`${column.id}__${index}`}
                id={`${column.id}__${index}`}
                isChecked={chkBoxFlag(row[column.id])}
                isDisabled={true}
              />
            </td>
          } else if (colSelection(column.id) && row[column.id].length > 1) {
            return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status`}>
              <span>{row[column.id].filter(n => n).join(', ')}</span></td>
          }
          else {
            return <td id={`${column.id}${index}`} key={column.id} className={`${row[column.id]}-status`}>
              <span data-title={conditionClassName(`${column.id}`, row[column.id])} className={conditionClassName(`${column.id}`, "tooltip-title")}>{row[column.id] || ''}</span></td>
          }
        }

      })}

    </>

  )
}