import React from 'react';
import './loader.css';

export const TpexLoader = ({
    isLoading=false
}) => (
    <>
        {
            isLoading &&
            <div
                data-testid="tpex-loader"
                className="d-flex justify-content-center spinner-parent"
            >
                <div
                    className="spinner-border"
                    role="status"
                ><span className="visually-hidden">Loading...</span></div>
            </div>
        }
    </>
);