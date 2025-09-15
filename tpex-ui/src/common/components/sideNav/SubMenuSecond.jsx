import React from 'react';
import { Link } from 'react-router-dom';
import { getIconImg } from './utils';

const SubMenuSecond = ({ data, id, heading }) => {

    return (
        <div className="collapse offcanvas-subMenu offcanvas-subMenu2 second-label-menu" id={`collapsesubmenu${id}`}>
            <div className="subMenu-header">
                <h2>{heading}</h2>
            </div>
            <ul className="list-group">
                {
                    data.map((item, i) => {

                        return (
                            <li className="list-group-item" key={item.id}>
                                <img
                                    src={getIconImg(item.imgClass)}
                                    width={20}
                                    height={20}
                                    alt=''
                                />

                                <Link
                                    className="list-link no-arrow"
                                    title={item.label}
                                    to={item.routeLink}>{item.label}
                                </Link>
                            </li>
                        );
                    })
                }
            </ul>
        </div>
    )
}
export default SubMenuSecond;