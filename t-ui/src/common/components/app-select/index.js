import React from 'react';
import { v4 as uuidv4 } from 'uuid';
import './style.css';

const TpexAppSelect = ({
  id="id",
  items=[],
  currentSelectedItemValue="",
  onChangeHandler=null,
  value="value",
  label="label"
}) => {
    
  const getCurrentSelectedItem = _currentSelectedItemValue => items.find(
    selectedItem => selectedItem[value] === _currentSelectedItemValue
  );

  const getLabelToShow = () => getCurrentSelectedItem(currentSelectedItemValue)[label];
    
  return (
    <>
      <div
        id=""
        className="nav-link dropdown-toggle"
        role="button"
        data-bs-toggle="dropdown"
        aria-expanded="false"
      >{items.length > 0 ? getLabelToShow() : ''}</div>

      <ul className="dropdown-menu app-select-dropdown-list">
        {
          items.map(option => (
            <li
              key={uuidv4()}
              onClick={() => onChangeHandler(option[value])}
            >{option[label]}</li>
          ))
        }
      </ul>
    </>
  );
};

export default TpexAppSelect;