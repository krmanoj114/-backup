import { LABEL_CONST } from "../../../constants/label.constant.en";
import TpexSimpleButton from "../button";

export function TableCustomActions({ customActions, calculatedRows, isCheck, handleActionClick, handleCustomAction }) {
    return (

        <>
            {customActions ?
                <div className="row">
                    <div className="form-group col-12">
                        {customActions.name === "Save" ?
                            <div className="d-flex justify-content-end mb-2 me-2">
                                <TpexSimpleButton color="primary" text={LABEL_CONST.SAVE} handleClick={event => handleActionClick(event)} />
                            </div> :
                            <div className="d-flex justify-content-end mb-2 me-2">
                                <TpexSimpleButton disabled={calculatedRows.length ? '' : 'disabled'} color="primary" text={customActions.name} handleClick={event => handleCustomAction(event, isCheck)} />
                            </div>
                        }
                    </div>
                </div> : ""
            }

        </>
    )
}