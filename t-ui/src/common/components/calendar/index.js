import React, { useState } from "react";
import Calendar from "react-calendar";
import "./timecalender.css";
import "react-calendar/dist/Calendar.css";

function TpexCalendar({existingData=[]}) {
  const [value, OnValueChange] = useState(new Date());

  function alreadyselectedDates({date}) {
    return existingData.indexOf(date.toLocaleDateString('en-US')) >= 0;
  }
    
  return (
    <div className="calendar_container">
      <Calendar
        minDetail="year"
        maxDate={new Date(2099, 12, 31)}
        value={value}
        onChange={OnValueChange}
        tileDisabled={alreadyselectedDates}
      />   
    </div>
  );
}
export default TpexCalendar;