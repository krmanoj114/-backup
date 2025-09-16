import { LABEL_CONST } from "../../../constants/label.constant.en";
import TpexSimpleButton from "../button";

export function TableAction({ isCrud, columns, isCheck, copyAction, handleActionClick, actionOnTop = false }) {
    return <>

        {isCrud && columns.length ?
            <div className="gridfooter mt-10">
                <div className="row g-0">
                    <div className="form-group col-6 align-self-center g-0">
                        {!actionOnTop ? <TpexSimpleButton color="outline-primary" text="Add" handleClick={event => handleActionClick(event)} /> : ""}
                        {!actionOnTop && copyAction ? <TpexSimpleButton color="outline-primary" leftmargin="3" text="Copy" handleClick={event => handleActionClick(event)} /> : ""}
                        {!actionOnTop ? <TpexSimpleButton color="outline-primary" leftmargin="3" text={`Delete (${isCheck.length})`} handleClick={event => handleActionClick(event)} /> : ""}
                    </div>
                    <div className="form-group col-6 align-self-center">
                        <div className="d-flex justify-content-end">
                            <TpexSimpleButton color="primary" text={LABEL_CONST.SAVE} handleClick={event => handleActionClick(event)} />
                        </div>
                    </div>
                </div>
            </div>
            : ''
        }
    </>
}