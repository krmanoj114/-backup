import TpexSimpleButton from "../../common/components/button";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";
import { TpexSelect } from "../../common/components/select";
import React from "react";

const EditableRow = ({
  id,
  editFormData,
  handleEditFormChange,
  handleDateEtd,
  etdDate,
  handleDateEta,
  etaDate,
  selectedPol,
  selectedPod,
  onChangeSelectionPol,
  onChangeSelectionPod,
  portLoadDis,
}) => {
  let optionsPOL = portLoadDis
    .map((option) => {
      let { cd: id, name } = option;
      return {
        id: id.toString(),
        name: name.replace(/([a-z](?=[A-Z]))/g, "$1 "),
      };
    })
    .sort((a, b) => (a.id > b.id ? 1 : -1));

  let optionsPOD = portLoadDis
    .map((option) => {
      let { cd: id, name } = option;
      return {
        id: id.toString(),
        name: name.replace(/([a-z](?=[A-Z]))/g, "$1 "),
      };
    })
    .sort((a, b) => (a.id > b.id ? 1 : -1));

  const searchIndex = editFormData.findIndex((row) => row.id === id);
  
  return (
    <tr>
      <td className="buttonCell">
        <TpexSimpleButton color="outline-primary" text="Edit" />
      </td>
      <td className="table-active">
        {editFormData[searchIndex].haisenYearMonth}
      </td>
      <td className="table-active">{editFormData[searchIndex].haisenNo}</td>
      <td className="mandatoryControl">
        <TpexDatePicker
          className={"form-control w-auto"}
          dateSelected={etdDate[searchIndex].etd}
          handleDateSelected={(date) => handleDateEtd(date, searchIndex)}
          id="etddate"
          isDirectDatePicker={true}
        />
      </td>
      <td className="mandatoryControl">
        <TpexDatePicker
          id="etadate"
          className={"form-control w-auto"}
          dateFormat="dd/MM/yyyy"
          dateSelected={etaDate[searchIndex].eta}
          handleDateSelected={(date) => handleDateEta(date, searchIndex)}
          isDirectDatePicker={true}
        />
      </td>
      <td className="table-active">{editFormData[searchIndex].buyer}</td>
      <td className="mandatoryControl">
        <TpexSelect
          selected={editFormData[searchIndex].portOfLoading}
          options={optionsPOL}
          onChangeSelection={(event) =>
            onChangeSelectionPol(event, searchIndex)
          }
          hasValue={selectedPol}
        />
      </td>
      <td className="mandatoryControl">
        <TpexSelect
          selected={editFormData[searchIndex].portOfDischarge}
          options={optionsPOD}
          onChangeSelection={(event) =>
            onChangeSelectionPod(event, searchIndex)
          }
          hasValue={selectedPod}
        />
      </td>
      <td className="mandatoryControl">
        <input
          type="text"
          className={"form-control"}
          required="required"
          id="oceanVessel"
          name="oceanVessel"
          maxLength="30"
          defaultValue={editFormData[searchIndex].oceanVessel}
          onChange={(event) => handleEditFormChange(event, searchIndex)}
        ></input>
      </td>
      <td className="mandatoryControl">
        <input
          type="text"
          className={"form-control"}
          required="required"
          id="oceanVoyage"
          name="oceanVoyage"
          maxLength="10"
          defaultValue={editFormData[searchIndex].oceanVoyage}
          onChange={(event) => handleEditFormChange(event, searchIndex)}
        ></input>
      </td>
      <td>
        <input
          type="text"
          className={"form-control"}
          required="required"
          id="feederVessel"
          name="feederVessel"
          maxLength="30"
          defaultValue={editFormData[searchIndex].feederVessel}
          onChange={(event) => handleEditFormChange(event, searchIndex)}
        ></input>
      </td>
      <td>
        <input
          type="text"
          className={"form-control"}
          required="required"
          id="feederVoyage"
          name="feederVoyage"
          maxLength="10"
          defaultValue={editFormData[searchIndex].feederVoyage}
          onChange={(event) => handleEditFormChange(event, searchIndex)}
        ></input>
      </td>
      <td className="table-active">
        {editFormData[searchIndex].noOf20ftContainer}
      </td>
      <td className="table-active">
        {editFormData[searchIndex].noOf40ftContainer}
      </td>
      <td className="table-active">
        {editFormData[searchIndex].containerEfficiency}
      </td>
    </tr>
  );
};

export default EditableRow;
