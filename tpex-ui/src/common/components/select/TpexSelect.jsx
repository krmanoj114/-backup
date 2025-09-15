import React, { useEffect, useState } from 'react';
import "./select.css";

function TpexSelect({
    blankRequired=true,
    hasValue="",
    id="id",
    labelText="",
    margin="",
    moduleName="",
    onChangeSelection,
    options=[],
    selected="",
    valueKey="id",
    labelKey="name",
    isDisabled=false
}) {
    const [valueSelected, setValueSelected] = useState('');
    const [optionsList, setOptionsList] = useState([]);
    let i = 0;
    
    useEffect(() => {
        setValueSelected(hasValue);        
    }, [hasValue]);

    useEffect(() => {   
        if(options !== null)
            setOptionsList(options);        
    }, [options]);

    return (
        <>
            {
                labelText ? (
                    <label
                        htmlFor={`${moduleName}-id`}
                        className="ms-1"
                    >{labelText}:</label>
                ) : ''
            }
            
            <select
                name={moduleName}
                className={`form-select customSelect ${margin}`}
                id={`${moduleName}-id`}
                value={valueSelected}
                aria-label="Please select"
                onChange={onChangeSelection}
                disabled={isDisabled}
            >
                {
                    blankRequired ? (
                        <option
                            key={`default_chk_bx_id-${moduleName}`}
                            value=''
                        >{selected}</option>
                    ) : ''
                }
                    
                {
                    optionsList.map(option => {    
                        return (
                            <option
                                key={`${moduleName}-${i++}`}
                                value={option[valueKey]}
                            >{option[labelKey]}</option>
                        );
                    })
                }
            </select>
        </>
    );
}

export {TpexSelect};
