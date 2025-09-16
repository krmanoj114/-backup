import React, { useState } from 'react';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';

const TestMultiSelectWithSearch = () => {

    let options = [
        { value: 'ocean', label: 'Ocean' },
        { value: 'blue', label: 'Blue' },
        { value: 'purple', label: 'Purple' },
        { value: 'red', label: 'Red' },
        { value: 'orange', label: 'Orange' },
        { value: 'yellow', label: 'Yellow' },
        { value: 'green', label: 'Green' },
        { value: 'forest', label: 'Forest' },
        { value: 'slate', label: 'Slate' },
        { value: 'silver', label: 'Silver' }
    ];

    const [optionsFor1, setOptionsFor1] = useState(options);
    const [optionsFor2, setOptionsFor2] = useState(options);
    const [optionsFor3, setOptionsFor3] = useState(options);
    
    const [selectedDestFor1, setSelectedDestFor1] = useState([]);
    const [selectedDestFor2, setSelectedDestFor2] = useState([]);
    const [selectedDestFor3, setSelectedDestFor3] = useState([]);

    const getUnUsedOptions = usedOptions => {
        const unUsedOptions = options.filter(option => {
            const unUsedOption = usedOptions.find(
                usedOption => usedOption.value === option.value
            );
            return unUsedOption ? true : false;
        });

        console.log(unUsedOptions);

        return unUsedOptions;
    };

    const handleSelectedOptionsFor1 = selectedOptionObj => {
        setSelectedDestFor1(selectedOptionObj);

        const usedOptions = [
            ...selectedOptionObj,
            ...optionsFor2,
            ...optionsFor3
        ];

        console.log(usedOptions);

        const unUsedOptions = getUnUsedOptions(usedOptions);
        
        setOptionsFor2(unUsedOptions);
        setOptionsFor3(unUsedOptions);
    };

    const handleSelectedOptionsFor2 = selectedOptionObj => {
            
        setSelectedDestFor2(selectedOptionObj);
        
        const usedOptions = [
            ...selectedOptionObj,
            ...optionsFor1,
            ...optionsFor3
        ];

        const unUsedOptions = getUnUsedOptions(usedOptions);
        
        setOptionsFor1(unUsedOptions);
        setOptionsFor3(unUsedOptions);
    };

    const handleSelectedOptionsFor3 = selectedOptionObj => {

        setSelectedDestFor3(selectedOptionObj);
        const usedOptions = [
            ...selectedOptionObj,
            ...optionsFor1,
            ...optionsFor2
        ];

        const unUsedOptions = getUnUsedOptions(usedOptions);
        
        setOptionsFor1(unUsedOptions);
        setOptionsFor2(unUsedOptions);
    };

    return (
        <>
            <div>
                <TpexMultiSelectSeach
                    handleSelectedOptions={handleSelectedOptionsFor1}
                    name="destination_select"
                    noOptionsText="Select Destination"
                    value={selectedDestFor1}
                    staticValues={optionsFor1}
                />
            </div>

            <div>
                <TpexMultiSelectSeach
                    handleSelectedOptions={handleSelectedOptionsFor2}
                    name="destination_select"
                    noOptionsText="Select Destination"
                    value={selectedDestFor2}
                    staticValues={optionsFor2}
                />
            </div>

            <div>
                <TpexMultiSelectSeach
                    handleSelectedOptions={handleSelectedOptionsFor3}
                    name="destination_select"
                    noOptionsText="Select Destination"
                    value={selectedDestFor3}
                    staticValues={optionsFor3}
                />
            </div>
        </>
        
    );
};

export {TestMultiSelectWithSearch};

