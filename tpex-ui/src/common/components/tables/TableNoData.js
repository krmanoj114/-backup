import { LABEL_CONST } from "../../../constants/label.constant.en";

export const TableNoData = ({
    rows,
    isLoading,
    columns,
    selectRow,
    moduleName=""
}) => {
    const colSpanExceptShippingControlMaster = columns.length + (selectRow ? 1 : 0); 
    return (
        <>
            {
                !rows.length &&
                !isLoading &&
                <tr>
                    <td
                        colSpan={
                            moduleName==="SHIPPING_CONTROL_MASTER_LIST" ?
                            "10" :
                            colSpanExceptShippingControlMaster
                        }
                        className="noData"
                    >{LABEL_CONST.NO_DATA_FOUND}</td>
                </tr>
            }
        </>
    )
}