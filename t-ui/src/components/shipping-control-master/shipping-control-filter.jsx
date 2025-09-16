import TpexSimpleButton from '../../common/components/button';
export function ShippingTableFilter({ toggleFilter, isCrud, filter, columns, rows, handleActionClick, isCheck, moduleName, isSearchBtnClick }) {
    return (
        <>
            <div className="row g-0">
            {isCrud && rows.length ?
                <div className="form-group col-11 align-self-center g-0 pb-10">
                    <div className="d-flex justify-content-end">
                    {moduleName !== "INVOICE_RECALCULATION" && <TpexSimpleButton color="outline-primary" text="Add" handleClick={event => handleActionClick(event)} />}
                    { (moduleName === "SHIPPING_CONTROL_MASTER_LIST" 
                      || moduleName === "ENGINE_PART_MASTER" 
                      || moduleName === "RETURNABLE_PACKING_MASTER") 
                      && <TpexSimpleButton color="btn btn-outline-primary" text="Copy" leftmargin="3" handleClick={event => handleActionClick(event)} />}
                    {moduleName !== "INVOICE_RECALCULATION" && <TpexSimpleButton color="outline-primary" leftmargin="3" text={`Delete (${isCheck.length})`} handleClick={event => handleActionClick(event)} />}
                    </div>
                </div>
            : ''
            }
            {((moduleName === "MIX_PRIVILEGE_MASTER" 
                || moduleName === "INVOICE_SETUP_MASTER" 
                || moduleName === "ENGINE_PART_MASTER" 
                || moduleName === "RETURNABLE_PACKING_MASTER") 
                && isSearchBtnClick && rows.length === 0) 
            || ((moduleName === "SHIPPING_CONTROL_MASTER_LIST" 
                || moduleName === "RETURNABLE_PACKING_MASTER") 
                && rows.length === 0) ?
            <div className="form-group col-11 align-self-center g-0 pb-10">
                    <div className="d-flex justify-content-end">
                        <TpexSimpleButton color="outline-primary" text="Add" handleClick={event => handleActionClick(event)} />
                        {(moduleName === "RETURNABLE_PACKING_MASTER") && rows.length === 0 
                      && <TpexSimpleButton color="btn btn-outline-primary" text="Copy" leftmargin="3" handleClick={event => handleActionClick(event)} />}
                      {(moduleName === "RETURNABLE_PACKING_MASTER") && rows.length === 0 
                      && <TpexSimpleButton color="outline-primary" leftmargin="3" text={`Delete (${isCheck.length})`} handleClick={event => handleActionClick(event)} />
                      }
                    </div>
                </div>
            : ''
            }
            {filter && columns.length ?
            <>
                {/* filter option  */}
                {filter ?
                    <div className="col">
                    <div className="d-flex justify-content-end mt-2">
                        <button className="button-filter" onClick={toggleFilter}><i className="filter-button"></i><span>Filters</span></button>
                    </div>
                    </div> : ''
                }
            </>
            : ''
            }
            </div>
        </>
        )
    
    }