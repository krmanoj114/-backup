import Checkbox from "../checkbox";
import "../../../styles/table.css";
import "react-datepicker/dist/react-datepicker.css";

export function TableCheckBox({ key1, name, id, handleClick, isChecked }) {
    return (
        <Checkbox
            key={key1}
            type="checkbox"
            name={name}
            id={id}
            handleClick={handleClick}
            isChecked={isChecked}
        />       
    )
}
