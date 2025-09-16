import { useState } from "react";
import "../../styles/table.css";
import { TpexMultiSelectSeach } from "../../common/components/multiselect/multiselect";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

export const TableAddRow = ({
    columns,
    addData,
    addindex,
    dropDownData,
    inputBoxChange,
    handleDateSelected,
    dropdownChange,
    handleRowCheckBoxClick
}) => {

    const [optionsFor1, setOptionsFor1] = useState(dropDownData);
    const [optionsFor2, setOptionsFor2] = useState(dropDownData);
    const [optionsFor3, setOptionsFor3] = useState(dropDownData);
    const [optionsFor4, setOptionsFor4] = useState(dropDownData);
    const [optionsFor5, setOptionsFor5] = useState(dropDownData);

    const [selectedOptionFor1, setSelectedOptionFor1] = useState([]);
    const [selectedOptionFor2, setSelectedOptionFor2] = useState([]);
    const [selectedOptionFor3, setSelectedOptionFor3] = useState([]);
    const [selectedOptionFor4, setSelectedOptionFor4] = useState([]);
    const [selectedOptionFor5, setSelectedOptionFor5] = useState([]);

    const handleSelectedOptionsFor1 = (e, id, addindex, indexId) => {

        setSelectedOptionFor1(e);

        dropdownChange(e, id, addindex, indexId);

        const usedOptions = [
            ...e,
            ...selectedOptionFor2,
            ...selectedOptionFor3,
            ...selectedOptionFor4,
            ...selectedOptionFor5
        ];

        const unUsedOptions = dropDownData.filter(option => {
            const anyUsedOption = usedOptions.find(
                usedOption => usedOption.value === option.value
            );

            return !anyUsedOption;
        });

        setOptionsFor2(unUsedOptions);
        setOptionsFor3(unUsedOptions);
        setOptionsFor4(unUsedOptions);
        setOptionsFor5(unUsedOptions);
    };

    const handleSelectedOptionsFor2 = (e, id, addindex, indexId) => {
        
        setSelectedOptionFor2(e);

        dropdownChange(e, id, addindex, indexId);
        

        const usedOptions = [
            ...e,
            ...selectedOptionFor1,
            ...selectedOptionFor3,
            ...selectedOptionFor4,
            ...selectedOptionFor5
        ];

        const unUsedOptions = dropDownData.filter(option => {
            const anyUsedOption = usedOptions.find(
                usedOption => usedOption.value === option.value
            );

            return !anyUsedOption;
        });

        setOptionsFor1(unUsedOptions);
        setOptionsFor3(unUsedOptions);
        setOptionsFor4(unUsedOptions);
        setOptionsFor5(unUsedOptions);
    };

    const handleSelectedOptionsFor3 = (e, id, addindex, indexId) => {
        
        setSelectedOptionFor3(e);

        dropdownChange(e, id, addindex, indexId);

        const usedOptions = [
            ...e,
            ...selectedOptionFor1,
            ...selectedOptionFor2,
            ...selectedOptionFor4,
            ...selectedOptionFor5
        ];

        const unUsedOptions = dropDownData.filter(option => {
            const anyUsedOption = usedOptions.find(
                usedOption => usedOption.value === option.value
            );

            return !anyUsedOption;
        });

        setOptionsFor1(unUsedOptions);
        setOptionsFor2(unUsedOptions);
        setOptionsFor4(unUsedOptions);
        setOptionsFor5(unUsedOptions);

    };

    const handleSelectedOptionsFor4 = (e, id, addindex, indexId) => {
        
        setSelectedOptionFor4(e);

        dropdownChange(e, id, addindex, indexId);

        const usedOptions = [
            ...e,
            ...selectedOptionFor1,
            ...selectedOptionFor2,
            ...selectedOptionFor3,
            ...selectedOptionFor5
        ];

        const unUsedOptions = dropDownData.filter(option => {
            const anyUsedOption = usedOptions.find(
                usedOption => usedOption.value === option.value
            );

            return !anyUsedOption;
        });

        setOptionsFor1(unUsedOptions);
        setOptionsFor2(unUsedOptions);
        setOptionsFor3(unUsedOptions);
        setOptionsFor5(unUsedOptions);

    };

    const handleSelectedOptionsFor5 = (e, id, addindex, indexId) => {
        
        setSelectedOptionFor5(e);
        dropdownChange(e, id, addindex, indexId);

        const usedOptions = [
            ...e,
            ...selectedOptionFor1,
            ...selectedOptionFor2,
            ...selectedOptionFor3,
            ...selectedOptionFor5
        ];

        const unUsedOptions = dropDownData.filter(option => {
            const anyUsedOption = usedOptions.find(
                usedOption => usedOption.value === option.value
            );

            return !anyUsedOption;
        });

        setOptionsFor1(unUsedOptions);
        setOptionsFor2(unUsedOptions);
        setOptionsFor3(unUsedOptions);
        setOptionsFor4(unUsedOptions);

    };
    
    const parseDateMonthYear = s => {
        let [d, m, y] = s.split(/\D/);
        const date = d + "/" + m + "/" + y;
        return new Date(date);
    };

    const getDateSelected = (addData, addindex, column) => {

        if (addData[addindex][column.id]) {
            return parseDateMonthYear(addData[addindex][column.id]);
        }

        return column.id === "effectiveFromDt" ? null : parseDateMonthYear("12/31/9999");
        
    };

    let k = 0;

    return (
        <>
            {
                columns.map(column => {          
                    if (
                        column.date === true &&
                        (column.id === "effectiveFromDt" || column.id === "effctiveToDt")
                    ) {
                        const selectedDate = getDateSelected(addData, addindex, column);
                        return (
                            <td key={`date-${column.id}-${k++}`} className="mandatoryControl">
                                <TpexDatePicker
                                    id={`add-${column.id}-${addindex}`}
                                    name={`${column.id}__${addindex}`}
                                    dateSelected={selectedDate || ''}
                                    handleDateSelected={(date) => handleDateSelected(date, column.id, addindex)}
                                    isDirectDatePicker={true}
                                />
                            </td>
                        );
                    } else if(column.name === "Renban Code") {
                        switch (column.indexId) {
                            case 1:
                                return (
                                    <td key={`add-${column.id}-${addindex}`} className="mandatoryControl">
                                        <TpexMultiSelectSeach
                                            isMandatory={true}
                                            insideGrid={true}
                                            handleSelectedOptions={
                                                e => handleSelectedOptionsFor1(e, column.id, addindex, column.indexId)
                                            }
                                            name={`renban_code-${column.id}`}
                                            noOptionsText="Select Renban Code"
                                            value={selectedOptionFor1}
                                            staticValues={optionsFor1}
                                        />
                                    </td>
                                );    
                            case 2:
                                return (
                                    <td key={`add-${column.id}-${addindex}`}>
                                        <TpexMultiSelectSeach
                                            insideGrid={true}
                                            handleSelectedOptions={e => handleSelectedOptionsFor2(e, column.id, addindex, column.indexId)}
                                            name={`renban_code-${column.id}`}
                                            noOptionsText="Select Renban Code"
                                            value={selectedOptionFor2}
                                            staticValues={optionsFor2}
                                        />
                                    </td>
                                );

                            case 3:
                                return (
                                    <td key={`add-${column.id}-${addindex}`}>
                                        <TpexMultiSelectSeach
                                            insideGrid={true}
                                            handleSelectedOptions={e => handleSelectedOptionsFor3(e, column.id, addindex, column.indexId)}
                                            name={`renban_code-${column.id}`}
                                            noOptionsText="Select Renban Code"
                                            value={selectedOptionFor3}
                                            staticValues={optionsFor3}
                                        />
                                    </td>
                                );

                            case 4:
                                return (
                                    <td key={`add-${column.id}-${addindex}`}>
                                        <TpexMultiSelectSeach
                                            insideGrid={true}
                                            handleSelectedOptions={e => handleSelectedOptionsFor4(e, column.id, addindex, column.indexId)}
                                            name={`renban_code-${column.id}`}
                                            noOptionsText="Select Renban Code"
                                            value={selectedOptionFor4}
                                            staticValues={optionsFor4}
                                        />
                                    </td>
                                );

                            case 5:
                                return (
                                    <td key={`add-${column.id}-${addindex}`}>
                                        <TpexMultiSelectSeach
                                            insideGrid={true}
                                            handleSelectedOptions={e => handleSelectedOptionsFor5(e, column.id, addindex, column.indexId)}
                                            name={`renban_code-${column.id}`}
                                            noOptionsText="Select Renban Code"
                                            value={selectedOptionFor5}
                                            staticValues={optionsFor5}
                                        />
                                    </td>
                                );
                        }
                    } else {
                        return column.id !== "check" && (
                            <td
                                key={`str-${column.id}-${k++}`}
                                className={column.id === 'folderName1' ? 'mandatoryControl' : ''}
                            >
                                <input
                                    type="text"
                                    maxLength={25}
                                    value={addData[addindex][column.id]}
                                    name={`${column.id}__${addindex}`}
                                    className="form-control"
                                    id={`add-${column.id}-${addindex}`}
                                    onChange={event => inputBoxChange(event)}
                                />
                            </td>
                        );
                    }
                })
            }
        </>
    );
};
