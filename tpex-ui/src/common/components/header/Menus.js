import { Link } from 'react-router-dom';

const Menus = () => {

  return (
    <>
      <a
        className="nav-link dropdown-toggle"
        href="/" id="navbarDarkDropdownMenuLink"
        role="button"
        data-bs-toggle="dropdown"
        aria-expanded="false"
      >Menu</a>
      
      <ul
        className="dropdown-menu dropdown-menu-light"
        aria-labelledby="navbarDarkDropdownMenuLink"
      >
        <li>
          <Link
            className='dropdown-item'
            to="/home"
          >Home</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/report/invoice-shipping"
          >Invoice & Shipping Report</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/ondemanddownload"
          >On Demand Download Report </Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/uploaddownload"
          >Common Download Upload</Link></li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/codemaster"
          >Code master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/invoice-gen-work-plan-master"
          >Invoice Generation Work Plan Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/invoice-maintenance"
          >Invoice Maintenance</Link>
        </li>

        <li>
          <Link
            className='dropdown-item'
            to="/invoice/shipping-container-result"
          >Inquiry Screen for Shipping Container result</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/haisen-details"
          >Invoice Operations Haisen Details</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/inquiry-for-process-status"
          >Inquiry for Process Status and Control</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/lot-price-master"
          >Lot Price Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/shipping-control-master"
          >Shipping Control Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/vesselBookingMaster"
          >Vessel Booking Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/pxp-part-price-maintenance"
          >PxP Part Price Maintenance</Link></li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/mix-privilege-master"
          >Mix Privilege Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/renban-group-code-master"
          >Renban Group Code Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/returnable-packing-master"
          >Returnable Packing Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/invoice-setup-master"
          >Invoice Setup Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/engine-part-master"
          >Engine Part Master</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/invoice-recalculation"
          >Invoice Recalculation</Link>
        </li>
        
        <li>
          <Link
            className='dropdown-item'
            to="/part-master-inquiry"
          >Part Master Inquiry</Link>
        </li>

        <li>
          <Link
            className='dropdown-item'
            to="/iso-container-no-master-search"
          >ISO Container No. Master-Search</Link>
        </li>
        <li>
          <Link
            className='dropdown-item'
            to="/manual-invoice-generation"
          >Manual Invoice Generation</Link>
        </li>
      </ul>
    </>
  );
};

export default Menus;