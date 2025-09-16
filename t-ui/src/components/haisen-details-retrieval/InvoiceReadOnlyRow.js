import React from "react";

const InvoiceReadOnlyRow = ({ singleRow, handleEditClick }) => {
  return (
    <tr onClick={(event) => handleEditClick(event, singleRow)}>
      <td>{singleRow.invoiceNo}</td>
      <td>{singleRow.invoiceDate}</td>
      <td>{singleRow.invoiceAmount}</td>
      <td>{singleRow.invoiceM3}</td>
      <td>{singleRow.etdDate}</td>
      <td>{singleRow.etaDate}</td>
      <td>{singleRow.oceanVessel}</td>
      <td>{singleRow.oceanVoyage}</td>
      <td>{singleRow.feederVessel}</td>
      <td>{singleRow.feederVoyage}</td>
      <td>{singleRow.portOfLoading}</td>
      <td>{singleRow.portOfDischarge}</td>
      <td>{singleRow.haisenNo}</td>
    </tr>
  );
};

export default InvoiceReadOnlyRow;
