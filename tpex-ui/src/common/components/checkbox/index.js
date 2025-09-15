import React from "react";
import "./style.css";

const Checkbox = ({
    id,
    type,
    name,
    handleClick,
    isChecked,
    isDisabled,
    classNamesContainer="form-check",
    classNamesInput="form-check-input"
}) => (
    <div
        data-testid="tpex-checkbox"
        className={classNamesContainer}
    >
        <input
            id={id}
            className={classNamesInput}
            name={name}
            type={type}
            onChange={handleClick}
            checked={isChecked}
            disabled={isDisabled}
        />
    </div>
);

export default Checkbox;