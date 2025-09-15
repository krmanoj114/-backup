import { useState } from "react";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

const MyDatePicker = () => {

  const [valYearlyPicker, setValYearlyPicker] = useState(null);

  const [valMonthlyPicker, setValMonthlyPicker] = useState(null);

  const [valDateTimePicker, setValDateTimePicker] = useState(null);

  const [valDatePicker, setValDatePicker] = useState(null);

  const [valOriginalDatePicker, setValOriginalDatePicker] = useState(null);

  return (
    <div>
      
      <div>
        <h3>Yearly Picker</h3>
        <div>
          <TpexDatePicker
            dateSelected={valYearlyPicker}
            handleDateSelected={date => setValYearlyPicker(date)}
            showYearPicker={true}
            isDirectDatePicker={true}
          />
        </div>
      </div>

      <div>
        <h3>Monthly Picker</h3>
        <div>
          <TpexDatePicker
            dateSelected={valMonthlyPicker}
            handleDateSelected={date => setValMonthlyPicker(date)}
            showMonthYearPicker={true}
            isDirectDatePicker={true}
          />
        </div>
      </div>

      <div>
        <h3>Day and Date Picker</h3>
        <div>
          <TpexDatePicker
            dateSelected={valDatePicker}
            handleDateSelected={date => setValDatePicker(date)}
            isDirectDatePicker={true}
          />
        </div>
      </div>

      <div>
        <h3>Day, Date and Time Picker</h3>
        <div>
          <TpexDatePicker
            dateSelected={valDateTimePicker}
            handleDateSelected={date => setValDateTimePicker(date)}
            showDateAndTime={true}
            isDirectDatePicker={true}
          />
        </div>
      </div>

      <div>
        <h3>Original Day, Date and Time Picker</h3>
        <div>
          <TpexDatePicker
            id="requestDateOriginal"
            dateSelected={valOriginalDatePicker}
            handleDateSelected={date => setValOriginalDatePicker(date)}
          />
        </div>
      </div>

    </div>
  );

};

export default MyDatePicker;