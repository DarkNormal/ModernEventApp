package com.lordan.mark.PosseUp.UI.CreateEventGroup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.lordan.mark.PosseUp.Model.Event;
import com.lordan.mark.PosseUp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mark on 14/01/2016
 */
public class FirstEventFragment extends Fragment {
    private SwitchCompat mSwitch;
    private View v;
    private final Event newEvent = new Event();
    private boolean allDayEvent;
    private final int PLACE_PICKER_REQUEST = 1;
    private TextView chosenLocation;
    private Place chosenPlace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        v = inflater.inflate(R.layout.add_event_basic_details, container, false);
        chosenLocation =(TextView) v.findViewById(R.id.event_current_location);
        Spinner spinner = (Spinner) v.findViewById(R.id.create_event_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.event_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        configDateTimeChooser();
        placePickerOnClick();

        return v;
    }
    public static FirstEventFragment newInstance(String text) {

        FirstEventFragment f = new FirstEventFragment();

        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    private void placePickerOnClick(){
        CardView placeCard = (CardView) v.findViewById(R.id.placePickerCard);
        placeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == -1){
                 chosenPlace = PlacePicker.getPlace(getContext(), data);
                String toastMsg  = String.format("%s", chosenPlace.getName());
                chosenLocation.setText(toastMsg);
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void configDateTimeChooser() {
        final EditText startDateInput = (EditText) v.findViewById(R.id.create_event_date);
        final EditText endDateInput = (EditText) v.findViewById(R.id.create_event_date_end);
        configDateChooser(startDateInput, false);
        configDateChooser(endDateInput, true);
        final EditText startTimeInput = (EditText) v.findViewById(R.id.create_event_time);
        final EditText endTimeInput = (EditText) v.findViewById(R.id.create_event_time_end);
        configTimeChooser(startTimeInput, false);
        configTimeChooser(endTimeInput, true);

    }

    private void configDateChooser(final EditText dateTextView, final boolean addDay){
        Calendar today = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("d/M/y");
        if(addDay){
            today.add(Calendar.DATE, 1);
        }
        dateTextView.setHint(format.format(today.getTime()));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                if (addDay) {
                    mCurrentDate.add(Calendar.DATE, 1);
                }
                int mYear = mCurrentDate.get(Calendar.YEAR);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedMonth = selectedMonth + 1;
                        dateTextView.setText(getString(R.string.event_date,selectedDay, selectedMonth,selectedYear));
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });
    }
    private void configTimeChooser(final EditText timeTextView, final boolean addHour){
        Calendar now = Calendar.getInstance();
        final SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        timeTextView.setHint(timeFormat.format(now.getTime()));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        boolean addZeroHour = false;
                        boolean addZeroMinute = false;
                        if (selectedHour < 10) {
                            addZeroHour = true;
                        }
                        if (selectedMinute < 10) {
                            addZeroMinute = true;
                        }
                        if (addZeroHour && !addZeroMinute) {
                            timeTextView.setText("0" + selectedHour + ":" + selectedMinute);
                        } else if (!addZeroHour && addZeroMinute) {
                            timeTextView.setText(selectedHour + ":0" + selectedMinute);
                        } else if (addZeroHour && addZeroMinute) {
                            timeTextView.setText("0" + selectedHour + ":0" + selectedMinute);
                        } else {
                            timeTextView.setText(selectedHour + ":" + selectedMinute);
                        }

                    }
                }, hour, minute, true);

                mTimePicker.show();
            }
        });
    }
    public Event getEvent(){
        EditText title =(EditText) v.findViewById(R.id.create_event_title_input);
        EditText startDate =(EditText) v.findViewById(R.id.create_event_date);
        EditText startTime = (EditText) v.findViewById(R.id.create_event_time);
        EditText eventDesc = (EditText) v.findViewById(R.id.add_event_desc);
        boolean emptyTitle = false;
        boolean emptyDate = false;
        boolean emptyDesc = false;
        if(isEmpty(title)){
            title.setError("Title required");
            emptyTitle = true;
        }
        if(isEmpty(startDate) || isEmpty(startTime)){
            startDate.setError("Date & Time Required");
            emptyDate = true;
        }
        if(isEmpty(eventDesc)){
            eventDesc.setError("Description Required");
            emptyDesc = true;
        }
        if(!emptyTitle && !emptyDate && !emptyDesc){
            String startingDate = startDate.getText().toString() + " " + startTime.getText().toString();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm");
            Date date = null;
            try {
                date = df.parse(startingDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            newEvent.setStartingCal(cal);
            Calendar endCal = Calendar.getInstance();
            if(!allDayEvent){
                EditText dateEnd = (EditText) v.findViewById(R.id.create_event_date_end);
                EditText timeEnd = (EditText) v.findViewById(R.id.create_event_time_end);
                if(isEmpty(dateEnd) || isEmpty(timeEnd)){
                    dateEnd.setError("Date & Time Required");
                    return null;
                }
                else {

                    String endDate = dateEnd.getText().toString() + " " + timeEnd.getText().toString();
                    Date endingDate = null;
                    try {
                        endingDate = df.parse(endDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    endCal.setTime(endingDate);
                    newEvent.setEndDateTime(endCal);
                }

            }
            else{
               endCal.setTime(cal.getTime());
                endCal.add(Calendar.DATE, 1);
                newEvent.setEndDateTime(endCal);
            }
            newEvent.setEventDesc(eventDesc.getText().toString());
            newEvent.setEventName(title.getText().toString());
            newEvent.setStartDateTime(cal);
            if(chosenPlace != null) {
                newEvent.setPlaceDetails(chosenPlace);
            }

            return newEvent;
        }
        return null;
    }
    private boolean isEmpty(EditText v){
        return v.getText().toString().equals("");
    }
}
