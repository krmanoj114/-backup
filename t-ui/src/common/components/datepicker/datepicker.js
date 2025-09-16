import * as React from 'react';

import DatePicker, { registerLocale } from 'react-datepicker';
import enUS from "date-fns/locale/en-GB";
import './style.css';

const DATE_FORMAT = {
    DATE: 'dd/MM/yyyy',
    DATE_TIME: {
        DATE: 'P p',
        TIME: 'p'
    },
    MONTH_YEAR: 'yyyy/MM',
    YEAR: 'yyyy'
};

const NATIVE_EVENT = {
    INSERT_TEXT: 'insertText',
    INSERT_FROM_PASTE: 'insertFromPaste'
};

const formatMonthYearInputForSix = (v, e) => {
    if (e.nativeEvent.inputType === NATIVE_EVENT.INSERT_FROM_PASTE && /\d{6}/.test(v)) {
        e.target.value = `${v.substring(0, 4)}/${v.substring(4, 6)}`;
    }  
};

const formatMonthYearInputForSeven = (v, e) => {
    const {inputType} = e.nativeEvent;

    if (inputType === NATIVE_EVENT.INSERT_FROM_PASTE && /\d{4}-\d{2}/.test(v)) {
        e.target.value = `${v.substring(0, 4)}/${v.substring(5, 7)}`;
        
    } else if(inputType === NATIVE_EVENT.INSERT_FROM_PASTE && /\d{2}-\d{4}/.test(v)) {
        e.target.value = `${v.substring(3, 7)}/${v.substring(0, 2)}`;
    }
};

export const TpexDatePicker = ({
    classNames="form-control",
    dateFormat="dd/MM/yyyy",
    id="id",
    showMonthYearPicker=false,
    showYearPicker=false,
    showDateAndTime=false,
    isDisabled=false,
    placeholderText="",
    portalId="root",
    dateSelected,
    handleDateSelected
}) => {

    if (showYearPicker) {
        return (
            <DatePicker
                wrapperClassName="mandatory-min-width"
                className={classNames}
                id={id}
                placeholderText={placeholderText}
                selected={dateSelected}
                onChange={date => handleDateSelected(date)}
                portalId={portalId}

                dateFormat={DATE_FORMAT.YEAR}
                showYearPicker={showYearPicker}
            />
        );
    }

    if (showMonthYearPicker) {
        return (
            <DatePicker
                wrapperClassName="mandatory-min-width"
                placeholderText={placeholderText}
                className={classNames}
                selected={dateSelected}
                onChange={date => handleDateSelected(date)}
                id={id}
                portalId={portalId}

                dateFormat={DATE_FORMAT.MONTH_YEAR}
                showMonthYearPicker={showMonthYearPicker}
                onChangeRaw={e => {
                    const v = e.target.value;
                    if (v && v !== '') {

                        const stringLength = v.length;
                        const {inputType} = e.nativeEvent;

                        switch (stringLength) {
                            case 4:
                                if (inputType === NATIVE_EVENT.INSERT_TEXT && /\d{4}/.test(v)) {
                                    e.target.value = `${v}/`;
                                }
                            break;

                            case 6:
                                formatMonthYearInputForSix(v, e);      
                            break;

                            case 7:
                                formatMonthYearInputForSeven(v, e);
                            break;
                        }
                    }
                }}
            />
        );
    }

    if (showDateAndTime) {
        registerLocale("enUS", enUS);
        return (
            <DatePicker
                wrapperClassName="mandatory-min-width"
                className={classNames}
                placeholderText={placeholderText}
                selected={dateSelected}
                onChange={date => handleDateSelected(date)}
                id={id}
                portalId={portalId}

                locale={enUS}
                showTimeInput
                timeFormat="p"
                dateFormat="P p"
            />    
        );
    }

    return (
        <DatePicker
            wrapperClassName="mandatory-min-width"
            dateFormat={dateFormat}
            className={classNames}
            id={id}
            selected={dateSelected}
            onChange={date => handleDateSelected(date)}
            portalId={portalId}
            
            isDisabled={isDisabled}
            onChangeRaw={e => {
                const v = e.target.value;
                
                if (v !== '') {

                    switch (e.nativeEvent.inputType) {
                        case NATIVE_EVENT.INSERT_TEXT:
                            if (
                                (v.length === 2 && /\d{2}/.test(v)) ||
                                (v.length === 5 && /\d{2}\/\d{2}/.test(v)) 
                            ) {
                                e.target.value = `${v}/`;
                            }
                        break;

                        case NATIVE_EVENT.INSERT_FROM_PASTE:
                            if (v.length === 8 && /\d{8}/.test(v)) {
                                e.target.value = `${v.substring(0, 2)}/${v.substring(2, 4)}/${v.substring(4, 8)}`;
                            } else if (v.length === 10 && /\d{2}-\d{2}-\d{4}/.test(v)) {
                                e.target.value = `${v.substring(0, 2)}/${v.substring(3, 5)}/${v.substring(6, 10)}`;
                            }
                        break;    
                    }
                }
            }}
        />
    );
};
