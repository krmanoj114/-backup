import { TpexSelect } from "../../common/components/select";
import React from "react";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

const InvoiceEditableRow = ({
  id,
  editFormData,
  handleEditFormChange,
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
      <td className="table-active">{editFormData[searchIndex].invoiceNo}</td>
      <td className="table-active">{editFormData[searchIndex].invoiceDate}</td>
      <td className="table-active">
        {editFormData[searchIndex].invoiceAmount}
      </td>
      <td className="table-active">{editFormData[searchIndex].invoiceM3}</td>
      <td className="table-active">{editFormData[searchIndex].etdDate}</td>
      <td className="mandatoryControl">
        
        <TpexDatePicker
          id="etadate"
          className={"form-control w-auto"}
          handleDateSelected={date => handleDateEta(date, searchIndex)}
          dateSelected={etaDate[searchIndex].etaDate}
          isDirectDatePicker={true}
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
      <td className="table-active">{editFormData[searchIndex].feederVessel}</td>
      <td className="table-active">{editFormData[searchIndex].feederVoyage}</td>
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
      <td className="table-active">{editFormData[searchIndex].haisenNo}</td>
    </tr>
  );
};

export default InvoiceEditableRow;
