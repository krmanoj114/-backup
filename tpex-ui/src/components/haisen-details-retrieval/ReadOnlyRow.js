import React from "react";
import TpexSimpleButton from "../../common/components/button";

const ReadOnlyRow = ({
  singleRow,
  handleEditClick,
  rowClicked,
  selectedId,
}) => {
  return (
    <tr
      onClick={(event) => handleEditClick(event, singleRow)}
      className={rowClicked && selectedId === singleRow.id ? "trselection" : ""}
    >
      <td className="buttonCell">
        <TpexSimpleButton color="outline-primary" text="Edit" />
      </td>
      <td>{singleRow.haisenYearMonth}</td>
      <td>{singleRow.haisenNo}</td>
      <td>{singleRow.etd}</td>
      <td>{singleRow.eta}</td>
      <td>{singleRow.buyer}</td>
      <td>{singleRow.portOfLoading}</td>
      <td>{singleRow.portOfDischarge}</td>
      <td>{singleRow.oceanVessel}</td>
      <td>{singleRow.oceanVoyage}</td>
      <td>
        {singleRow.flag === true ? (
          <span className="text-red"> {singleRow.feederVessel}</span>
        ) : (
          singleRow.feederVessel
        )}
      </td>
      <td>
        {singleRow.flag === true ? (
          <span className="text-red"> {singleRow.feederVoyage}</span>
        ) : (
          singleRow.feederVoyage
        )}
      </td>
      <td>{singleRow.noOf20ftContainer}</td>
      <td>{singleRow.noOf40ftContainer}</td>
      <td>{singleRow.containerEfficiency}</td>
    </tr>
  );
};

export default ReadOnlyRow;
