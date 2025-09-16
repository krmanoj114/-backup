import React, { useState } from 'react';
import "./style.css";

import logoOpen from '../../../assets/images/toyota-logo-white.png';
import logoClose from '../../../assets/images/toyota-logo-white-icon.png';
import { getIconImg } from './utils';

import SubMenu from "./SubMenu";
import { Link } from 'react-router-dom';

const SideNav = props => {
    
    const {menus} = props;
    const [showPanel, setShowPanel] = useState(false);

    return (
        <div className={`offcanvas offcanvas-start ${showPanel ? 'show' : ''}`} >
            <div className="offcanvas-header">
                <h5 className="offcanvas-title" id="offcanvasLabel">
                    <Link className="dropdown-item" to="/home">
                        <img
                            src={logoOpen}
                            alt=""
                            width={170}
                            height={30}
                            className="logo-open"
                        />

                        <img
                            src={logoClose}
                            alt=""
                            width={30}
                            height={30}
                            className="logo-close"
                        />
                    </Link>
                </h5>
                <button
                    type="button"
                    className="btn-open-toggle"
                    onClick={() => setShowPanel(!showPanel)}
                ></button>
            </div>

            <div className="offcanvas-body">
                <ul className="list-group">
                    {
                        menus.map(({id, imgClass, iconUrl, label, routeLink, subMenu}) => (
                            <div key={id}>
                                {
                                    <li
                                        className={subMenu ? "list-group-item isSubmenu" : "list-group-item"}
                                        key={id}
                                    >
                                        <div
                                            className="offcanvas-tooltip toolstip"
                                            onClick={() => { setShowPanel(!showPanel) }}
                                        >
                                            
                                            <img src={getIconImg(imgClass)} width={20} height={20} alt='' />
                                            <span className="tooltiptext">{label}</span>
                                        </div>
                                        
                                        {
                                            !subMenu ? (
                                                <Link
                                                    className="list-link no-arrow"
                                                    title={label}
                                                    to={routeLink}
                                                >{label}</Link>
                                            ) : (
                                                <b className="list-link">
                                                    {label}
                                                    {
                                                        subMenu &&
                                                        <SubMenu
                                                            data={subMenu}
                                                            id={id}
                                                            heading={label}
                                                            setShowPanel={setShowPanel}
                                                        />
                                                    }
                                                </b>
                                            )
                                        }
                                    </li>
                                }
                            </div>
                        ))
                    }
                </ul>
            </div>
        </div>
    );
};

export default SideNav;