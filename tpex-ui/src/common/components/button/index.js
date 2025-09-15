import React from 'react';
import "./style.css";

const TpexSimpleButton = ({
    id="id",
    databstoggle="",
    databstarget="",
    disabled="",
    title="",
    topmargin= 0,
    leftmargin=0,
    rightmargin=0,
    bottommargin=0,
    isHidden=false,
    color,
    text,
    handleClick
}) => (
    <button
        data-testid="tpex-simple-button"
        type="button"
        data-bs-target={databstarget} data-bs-toggle={databstoggle}
        className={`btn btn-${color} ms-${leftmargin} mt-${topmargin} me-${rightmargin} mb-${bottommargin} ${disabled}`}
        onClick={handleClick}
        title={title}
        id={id}
        hidden={isHidden}
    >{text}</button>
);

export default TpexSimpleButton;
