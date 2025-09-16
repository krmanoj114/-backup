import React, { useEffect, useState } from 'react';
import "../../common/components/select/select.css";

function TpexSelect({ selected="", moduleName="", options, id="id", onChangeSelection, margin="", labelText='', blankRequired=true, hasValue=""}) {
    const [valueSelected, setValueSelected] = useState('');
    const [optionsList, setOptionsList] = useState([]);
    let i = 0;
    useEffect(() => {   
        setValueSelected(hasValue);        
    },[hasValue])

    useEffect(() => {   
        setOptionsList(options);        
    },[options])


    return (
        <>
        {labelText ? <label htmlFor={`${moduleName}-id`} className="ms-1">{labelText}:</label> : ''}
        <select name={`${moduleName}`} className={`form-select customSelect ${margin}`} id={`${moduleName}-id`} value={valueSelected} aria-label="Please select" onChange={onChangeSelection}>
                {blankRequired ? <option key={`default_chk_bx_id-${moduleName}`} value=''>{selected}</option>: ''}
                {optionsList.map((option,idx) => {    
                    return <option key={`${moduleName}-${i++}`} value={option[id]} >{option.name}</option>
                })

            }

        </select>
        </>
    );
}

export {TpexSelect};
