
import React from 'react';
import SubMenuSecond from './SubMenuSecond';
import { Link } from 'react-router-dom';
import { getIconImg } from './utils';

const SubMenu = ({ data, id, heading, setShowPanel }) => {

  return (
    <div className="collapse offcanvas-subMenu offcanvas-subMenu1 first-label-menu" id={`collapsesubmenu${id}`}>
        <div className="subMenu-header">
            <h2>{heading}</h2>
        </div>

        <ul className="list-group">
            {
                data.map((item, i) => {
                                        
                    return (
                        item.id ==="A301" ? null :
                        <li
                            className={item.subMenu ? "list-group-item isSubmenu" : "list-group-item"}
                            key={item.id}
                            onClick={() => setShowPanel(false)}>
                            <img src={getIconImg(item.imgClass)} width={20} height={20} alt='' />

                            {
                                !item.subMenu ? (
                                    <Link
                                        className="list-link no-arrow"
                                        title={item.value}
                                        to={item.routeLink}
                                    >{item.label}</Link>
                                ) : (
                                    <b className="list-link list-padding">{item.label}
                                        {
                                            item.subMenu &&
                                            <SubMenuSecond
                                                data={item.subMenu}
                                                id={item.id}
                                                heading={item.label}
                                            />
                                        }
                                    </b>
                                )
                            }
                        </li>
                    )
                })
            }
        </ul>
    </div>
  );
}
export default SubMenu;