import TpexSimpleButton from "../../common/components/button";
import { TableFilter } from './TableFilter';

export const TableAction = ({
    toggleFilter,
    filter,
    columns2,
    isCrud,
    columns,
    isCheck,
    hideAction,
    copyAction,
    handleActionClick
}) => {
    return (
        <>
            {
                isCrud &&
                columns.length &&
                <div className="row g-0 pb-10">
                    {
                        hideAction &&
                        <div
                            className="form-group col-11 align-self-center g-0 text-end"
                        >
                            <TpexSimpleButton
                                color="outline-primary"
                                text="Add"
                                handleClick={event => handleActionClick(event)}
                            />                        
                            
                            <TpexSimpleButton
                                color="outline-primary"
                                leftmargin="3"
                                text={`Delete (${isCheck.length})`}
                                handleClick={event => handleActionClick(event)}
                            />
                        </div>
                    }
                    

                    <TableFilter
                        toggleFilter={toggleFilter}
                        filter={filter}
                        columns={columns2}
                    />
                </div>
            }
        </>
    ); 
}