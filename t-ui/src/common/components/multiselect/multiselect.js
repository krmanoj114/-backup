import React from 'react';
import AsyncSelect from 'react-select/async';
import Select from 'react-select';
import { getRequest } from '../../../services/axios-client';

export const TpexMultiSelectSeach = ({
    id="id",
    isMulti=true,
    isClearable=true,
    value,
    handleSelectedOptions,
    searchUrl="",
    name="",
    noOptionsText="No Options",
    serverSide,
    staticValues=[],
    BASE_URL,
    availableWidth=0,
    allowLeftTo=0,
    isMandatory=false,
    insideGrid=false,
    className="react-select-container",
    classNamePrefixForMenu="react-select-child"
}) => {
    const promiseOptions = async (inputValue) => {
        if (!inputValue) {
            return [];
        }
        const data = await getRequest(BASE_URL, searchUrl + inputValue);
        return data.data;
    };

    const height = insideGrid ? '27px' : '34px';

    const customStyles = {
        container: provided => ({
            ...provided,
            minWidth: '120px'
        }),
        control: provided => {
            const toProvide = {
                ...provided,
                '&:hover': {
                    boxShadow: 'none'
                  },
                backgroundColor: isMandatory ? '#b1dffc' : provided['backgroundColor'],
                minHeight: height  
            };

            if (!isMulti) {
                toProvide.height = height;
            }   

            return toProvide;
        },
        valueContainer: provided => ({
            ...provided,
            fontSize: '16px',
            padding: '0 4px',
            minHeight: height
        }),
        multiValueLabel: provided => ({
            ...provided,
            padding: '0 3px'
        }),
        input: provided => {

            const toProvide = {
                ...provided,
                margin: '0px'
            };

            if (isMulti && insideGrid) {
                toProvide.paddingTop = '0';
                toProvide.paddingBottom = '0';
            }

            return toProvide;
        },
        indicatorsContainer: provided => {
            const toProvide = {
                ...provided,
                height: height
            };
            
            return isMulti ? provided : toProvide; 
        },
        clearIndicator: provided => ({
            ...provided,
            padding: '4px'
        }),
        dropdownIndicator: provided => {
            const toProvide = {
              ...provided,
              padding: '4px'
            };
      
            return toProvide;
        },
        menuPortal: provided => {
            
            const {left, width} = provided;
            return {
                ...provided,
                left: left - allowLeftTo,
                width: width + availableWidth,
                zIndex: 9999,
                fontSize: '16px',
                fontFamily: "'LatoRegular', Helvetica, Arial, Verdana, Tahoma, sans-serif"
            };
        },
        menu: provided => {
            return {
                ...provided,
                zIndex: 9999,
                fontSize: '16px',
                fontFamily: "'LatoRegular', Helvetica, Arial, Verdana, Tahoma, sans-serif"
            };
        }
    };
    
    return (
        <>
            {serverSide === true ?
                <AsyncSelect
                    isMulti={isMulti}
                    cacheOptions
                    defaultOptions
                    id={id}
                    value={value}
                    loadOptions={promiseOptions}
                    onChange={handleSelectedOptions}
                    name={name}
                    noOptionsMessage={({ inputValue }) => !inputValue ? noOptionsText : "No results found"}
                />
                :
                <Select
                    className={className}
                    /* menuIsOpen={true} */
                    classNamePrefix={classNamePrefixForMenu}
                    menuPortalTarget={document.getElementById('root')}
                    menuPosition={'fixed'}
                    styles={customStyles}
                    isMulti={isMulti}
                    name={name}
                    id={id}
                    value={value}
                    isClearable={isClearable}
                    options={staticValues}
                    onChange={handleSelectedOptions}
                    closeMenuOnScroll={e => {
                        return (
                            e.type === 'scroll' &&
                            e.target &&
                            e.target.classList &&
                            e.target.classList.length > 0 &&
                            e.target.classList[0] !== `${classNamePrefixForMenu}__menu-list` ? true : false
                        );
                    }}
                    noOptionsMessage={
                        ({ inputValue }) => !inputValue ? noOptionsText : "No results found"
                    }
                />
            }
        </>
    );
}
